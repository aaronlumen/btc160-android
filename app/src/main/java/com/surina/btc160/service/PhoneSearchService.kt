package com.surina.btc160.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.surina.btc160.MainActivity
import com.surina.btc160.R
import com.surina.btc160.data.CanaryAnswer
import com.surina.btc160.data.ClaimResponse
import com.surina.btc160.data.CompleteRequest
import com.surina.btc160.data.DgxRepository
import com.surina.btc160.data.UiState
import com.surina.btc160.util.BitcoinUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.math.BigInteger

class PhoneSearchService : Service() {

    companion object {
        private const val CHANNEL_ID   = "btc160_hunt"
        private const val NOTIF_ID     = 1001
        const val ACTION_START = "com.surina.btc160.SEARCH_START"
        const val ACTION_STOP  = "com.surina.btc160.SEARCH_STOP"

        private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
        val state: StateFlow<SearchState> = _state
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var searchJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var repo: DgxRepository

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("btc160", Context.MODE_PRIVATE)
        repo = DgxRepository(prefs)
        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification("Starting…"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSearch()
            return START_NOT_STICKY
        }
        val prefs = getSharedPreferences("btc160", Context.MODE_PRIVATE)
        val playerToken = prefs.getString("player_token", null)
        if (playerToken == null) {
            _state.value = SearchState.Error("Not registered — enter your BTC address in Settings")
            stopSelf()
            return START_NOT_STICKY
        }
        if (searchJob?.isActive == true) return START_NOT_STICKY
        acquireWakeLock()
        searchJob = scope.launch { runSearch(playerToken) }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        releaseWakeLock()
        super.onDestroy()
    }

    private fun stopSearch() {
        searchJob?.cancel()
        releaseWakeLock()
        _state.value = SearchState.Idle
        stopSelf()
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
    }

    // ── core search loop ──────────────────────────────────────────────────────

    private suspend fun runSearch(playerToken: String) {
        while (currentCoroutineContext().isActive) {
            // claim a chunk
            _state.value = SearchState.Claiming
            updateNotification("Claiming chunk…")
            val claim = when (val r = repo.claimChunk(playerToken)) {
                is UiState.Success -> r.data
                is UiState.Error   -> { _state.value = SearchState.Error(r.message); return }
                UiState.Offline    -> { _state.value = SearchState.Error("Server offline"); return }
                else               -> return
            }

            // derive canary answers before the search (we need them at completion)
            val canaryAnswers = claim.canaryKeys.map { hexKey ->
                val key  = BigInteger(hexKey.removePrefix("0x"), 16)
                val addr = BitcoinUtils.privKeyToAddress(key)
                CanaryAnswer(hexKey, addr)
            }

            searchChunk(playerToken, claim, canaryAnswers) ?: return  // found key or cancelled
        }
    }

    private suspend fun searchChunk(
        playerToken: String,
        claim: ClaimResponse,
        canaryAnswers: List<CanaryAnswer>,
    ): Unit? {
        val start  = BigInteger(claim.startHex.removePrefix("0x"), 16)
        val end    = BigInteger(claim.endHex.removePrefix("0x"), 16)
        val target = claim.targetAddress
        val total  = (end - start + BigInteger.ONE).toLong()

        var point       = BitcoinUtils.scalarMultiplyG(start)
        var keysChecked = 0L
        var foundKey    = ""
        val startTime   = System.currentTimeMillis()
        var lastHbTime  = startTime
        var lastSpeedMs = startTime
        var speedKps    = 0f

        // emit initial state
        _state.value = SearchState.Running(
            chunkId     = claim.chunkId,
            keysChecked = 0,
            chunkSize   = total,
            speedKps    = 0f,
            inTarget    = claim.inTargetZone,
        )
        updateNotification("Searching chunk…  0 / $total")

        var key = start
        while (key <= end) {
            if (!currentCoroutineContext().isActive) return null

            val address = BitcoinUtils.pointToAddress(point)
            if (address == target) {
                foundKey = "0x${key.toString(16)}"
            }
            point = BitcoinUtils.addG(point)
            key += BigInteger.ONE
            keysChecked++

            // update speed every 5 000 keys
            if (keysChecked % 5_000L == 0L) {
                val now = System.currentTimeMillis()
                val elapsed = (now - lastSpeedMs).coerceAtLeast(1)
                speedKps = 5_000_000f / elapsed   // keys / ms → kps
                lastSpeedMs = now

                _state.value = SearchState.Running(
                    chunkId     = claim.chunkId,
                    keysChecked = keysChecked,
                    chunkSize   = total,
                    speedKps    = speedKps,
                    inTarget    = claim.inTargetZone,
                )
                updateNotification("Chunk ${claim.chunkId} — ${keysChecked / 1000}k / ${total / 1000}k  |  ${speedKps.toInt()} k/s")
            }

            // heartbeat every 30 s
            if (keysChecked % 10_000L == 0L) {
                val now = System.currentTimeMillis()
                if (now - lastHbTime >= 30_000L) {
                    lastHbTime = now
                    when (val hb = repo.heartbeat(claim.chunkId, playerToken, keysChecked, speedKps)) {
                        is UiState.Success -> if (hb.data.status == "abort") {
                            _state.value = SearchState.Error("Server: ${hb.data.reason ?: "abort"}")
                            return null
                        }
                        else -> Unit
                    }
                }
            }

            // if we already found the key, exit loop early
            if (foundKey.isNotEmpty()) break
        }

        // submit completion
        val elapsed = (System.currentTimeMillis() - startTime) / 1000f
        val completeReq = CompleteRequest(
            playerToken    = playerToken,
            keysChecked    = keysChecked,
            elapsedSeconds = elapsed,
            canaryAnswers  = canaryAnswers,
            foundKey       = foundKey,
        )
        repo.completeChunk(claim.chunkId, completeReq)

        if (foundKey.isNotEmpty()) {
            _state.value = SearchState.Found(foundKey, target, claim.chunkId)
            updateNotification("KEY FOUND: $foundKey")
            return null  // stop looping
        }

        return Unit  // continue to next chunk
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun acquireWakeLock() {
        releaseWakeLock()
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BTC160:search").also {
            it.acquire(6 * 60 * 60 * 1000L)  // 6 hour safety cap
        }
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIF_ID, buildNotification(text))
    }

    private fun buildNotification(text: String): Notification {
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopPi = PendingIntent.getService(
            this, 1,
            Intent(this, PhoneSearchService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BTC160 Hunt")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_search)
            .setContentIntent(pi)
            .addAction(0, "Stop", stopPi)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun createNotificationChannel() {
        val ch = NotificationChannel(CHANNEL_ID, "BTC160 Key Search", NotificationManager.IMPORTANCE_LOW)
        ch.description = "Background Bitcoin key search"
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
    }
}

// ── state ─────────────────────────────────────────────────────────────────────

sealed class SearchState {
    object Idle     : SearchState()
    object Claiming : SearchState()
    data class Running(
        val chunkId: String,
        val keysChecked: Long,
        val chunkSize: Long,
        val speedKps: Float,
        val inTarget: Boolean,
    ) : SearchState()
    data class Found(val keyHex: String, val address: String, val chunkId: String) : SearchState()
    data class Error(val message: String) : SearchState()
}
