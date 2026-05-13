package com.surina.btc160.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.surina.btc160.data.ChunkStatusResponse
import com.surina.btc160.data.DgxRepository
import com.surina.btc160.data.TelemetryResponse
import com.surina.btc160.data.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("btc160", Context.MODE_PRIVATE)
    val repo = DgxRepository(prefs)

    private val _telemetry    = MutableLiveData<UiState<TelemetryResponse>>(UiState.Loading)
    val telemetry: LiveData<UiState<TelemetryResponse>> = _telemetry

    private val _chunkStatus  = MutableLiveData<UiState<ChunkStatusResponse>>(UiState.Loading)
    val chunkStatus: LiveData<UiState<ChunkStatusResponse>> = _chunkStatus

    private val _serverUrl    = MutableLiveData(repo.serverUrl)
    val serverUrl: LiveData<String> = _serverUrl

    private var pollJob: Job? = null

    fun startPolling() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (true) {
                _telemetry.postValue(repo.fetchTelemetry())
                delay(3_000)
            }
        }
    }

    fun stopPolling() { pollJob?.cancel() }

    fun refreshStatus() {
        viewModelScope.launch {
            _chunkStatus.value = UiState.Loading
            _chunkStatus.value = repo.fetchChunkStatus()
        }
    }

    fun updateUrl(url: String) {
        repo.updateServerUrl(url)
        _serverUrl.value = url
    }
}
