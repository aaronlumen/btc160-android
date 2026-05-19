package com.surina.btc160.ui.hunt

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.surina.btc160.data.DgxRepository
import com.surina.btc160.data.RegisterRequest
import com.surina.btc160.data.UiState
import com.surina.btc160.service.PhoneSearchService
import com.surina.btc160.service.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HuntViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("btc160", Context.MODE_PRIVATE)
    private val repo  = DgxRepository(prefs)

    val searchState: LiveData<SearchState> = PhoneSearchService.state.asLiveData()

    private val _registerResult = MutableStateFlow<String?>(null)
    val registerResult: StateFlow<String?> = _registerResult

    val playerToken   get() = prefs.getString("player_token", null)
    val playerName    get() = prefs.getString("player_name", "") ?: ""
    val btcAddress    get() = prefs.getString("btc_address", "") ?: ""
    val isRegistered  get() = playerToken != null

    suspend fun register(deviceId: String, playerName: String, btcAddress: String): Boolean {
        val req = RegisterRequest(deviceId, playerName, btcAddress)
        return when (val r = repo.register(req)) {
            is UiState.Success -> {
                prefs.edit()
                    .putString("player_token", r.data.playerToken)
                    .putString("player_name", r.data.playerName)
                    .putString("btc_address", btcAddress)
                    .apply()
                _registerResult.value = "Registered as ${r.data.playerName}"
                true
            }
            is UiState.Error -> {
                _registerResult.value = "Registration failed: ${r.message}"
                false
            }
            UiState.Offline -> {
                _registerResult.value = "Server offline"
                false
            }
            else -> false
        }
    }
}
