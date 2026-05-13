package com.surina.btc160.ui.chunk71

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.surina.btc160.data.ChunkInfoResponse
import com.surina.btc160.data.ChunkStatusResponse
import com.surina.btc160.data.DgxRepository
import com.surina.btc160.data.PuzzleData
import com.surina.btc160.data.TelemetryResponse
import com.surina.btc160.data.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Chunk71ViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("btc160", Context.MODE_PRIVATE)
    val repo = DgxRepository(prefs)

    private val _chunkStatus = MutableLiveData<UiState<ChunkStatusResponse>>(UiState.Loading)
    val chunkStatus: LiveData<UiState<ChunkStatusResponse>> = _chunkStatus

    private val _telemetry = MutableLiveData<UiState<TelemetryResponse>>(UiState.Offline)
    val telemetry: LiveData<UiState<TelemetryResponse>> = _telemetry

    private val _selectedInfo = MutableLiveData<ChunkInfoResponse?>(null)
    val selectedInfo: LiveData<ChunkInfoResponse?> = _selectedInfo

    private val _actionResult = MutableLiveData<String?>(null)
    val actionResult: LiveData<String?> = _actionResult

    private var pollJob: Job? = null

    fun loadStatus() {
        viewModelScope.launch {
            _chunkStatus.value = UiState.Loading
            _chunkStatus.value = repo.fetchChunkStatus()
        }
    }

    fun startPolling() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (true) {
                val tel = repo.fetchTelemetry()
                _telemetry.postValue(tel)
                delay(3_000)
            }
        }
    }

    fun stopPolling() { pollJob?.cancel() }

    fun onChunkSelected(idx: Int) {
        viewModelScope.launch {
            val state = repo.fetchChunkInfo(idx)
            if (state is UiState.Success) _selectedInfo.value = state.data
            else _selectedInfo.value = ChunkInfoResponse(
                chunkIdx    = idx,
                startHex    = PuzzleData.chunkStart(idx).toString(16).uppercase(),
                endHex      = PuzzleData.chunkEnd(idx).toString(16).uppercase(),
                done        = false,
                inTargetZone = idx in PuzzleData.TARGET_START..PuzzleData.TARGET_END,
            )
        }
    }

    fun launchChunk(idx: Int) {
        viewModelScope.launch {
            when (val r = repo.launchChunk(idx)) {
                is UiState.Success -> _actionResult.value = "Launched chunk $idx"
                is UiState.Error   -> _actionResult.value = "Error: ${r.message}"
                UiState.Offline    -> _actionResult.value = "Server offline"
                else               -> Unit
            }
            loadStatus()
        }
    }

    fun stopSearch() {
        viewModelScope.launch {
            repo.stopSearch()
            _actionResult.value = "Search stopped"
            loadStatus()
        }
    }

    fun markDone(idx: Int) {
        viewModelScope.launch {
            repo.markDone(idx)
            _actionResult.value = "Chunk $idx marked done"
            loadStatus()
        }
    }

    fun clearActionResult() { _actionResult.value = null }
}
