package com.surina.btc160.data

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DgxRepository(private val prefs: SharedPreferences) {

    companion object {
        const val KEY_SERVER_URL = "server_url"
        const val DEFAULT_URL    = "http://192.168.1.100:8000"
    }

    private var _serverUrl = prefs.getString(KEY_SERVER_URL, DEFAULT_URL) ?: DEFAULT_URL
    val serverUrl get() = _serverUrl

    private var api = buildApi(_serverUrl)

    private fun buildApi(url: String): DgxApi {
        val base = if (url.endsWith("/")) url else "$url/"
        return Retrofit.Builder()
            .baseUrl(base)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DgxApi::class.java)
    }

    fun updateServerUrl(url: String) {
        _serverUrl = url.trimEnd('/')
        prefs.edit().putString(KEY_SERVER_URL, _serverUrl).apply()
        api = buildApi(_serverUrl)
    }

    suspend fun fetchChunkStatus(): UiState<ChunkStatusResponse> = withContext(Dispatchers.IO) {
        safeCall { api.getChunkStatus() }
    }

    suspend fun fetchTelemetry(): UiState<TelemetryResponse> = withContext(Dispatchers.IO) {
        safeCall { api.getTelemetry() }
    }

    suspend fun fetchChunkInfo(idx: Int): UiState<ChunkInfoResponse> = withContext(Dispatchers.IO) {
        safeCall { api.getChunkInfo(idx) }
    }

    suspend fun launchChunk(idx: Int): UiState<LaunchResponse> = withContext(Dispatchers.IO) {
        safeCall { api.launchChunk(idx) }
    }

    suspend fun stopSearch(): UiState<Unit> = withContext(Dispatchers.IO) {
        try {
            api.stopSearch()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.friendlyMessage())
        }
    }

    suspend fun markDone(idx: Int): UiState<Unit> = withContext(Dispatchers.IO) {
        try {
            api.markDone(idx)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.friendlyMessage())
        }
    }

    private suspend fun <T> safeCall(block: suspend () -> retrofit2.Response<T>): UiState<T> {
        return try {
            val resp = block()
            if (resp.isSuccessful) {
                val body = resp.body()
                if (body != null) UiState.Success(body)
                else UiState.Error("Empty response from server")
            } else {
                UiState.Error("Server error ${resp.code()}: ${resp.message()}")
            }
        } catch (e: UnknownHostException) {
            UiState.Offline
        } catch (e: SocketTimeoutException) {
            UiState.Offline
        } catch (e: Exception) {
            UiState.Error(e.friendlyMessage())
        }
    }

    private fun Exception.friendlyMessage(): String =
        message?.take(120) ?: javaClass.simpleName
}
