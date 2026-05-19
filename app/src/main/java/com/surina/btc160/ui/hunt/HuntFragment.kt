package com.surina.btc160.ui.hunt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.surina.btc160.databinding.FragmentHuntBinding
import com.surina.btc160.service.PhoneSearchService
import com.surina.btc160.service.SearchState
import kotlinx.coroutines.launch

class HuntFragment : Fragment() {

    private var _binding: FragmentHuntBinding? = null
    private val binding get() = _binding!!
    private val vm: HuntViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHuntBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshRegistrationUi()

        binding.btnRegister.setOnClickListener { onRegisterClicked() }
        binding.btnStart.setOnClickListener   { onStartClicked() }
        binding.btnStop.setOnClickListener    { onStopClicked() }

        vm.searchState.observe(viewLifecycleOwner) { applyState(it) }

        lifecycleScope.launch {
            vm.registerResult.collect { msg ->
                if (msg != null) {
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                    refreshRegistrationUi()
                }
            }
        }
    }

    private fun refreshRegistrationUi() {
        if (vm.isRegistered) {
            binding.layoutRegister.visibility = View.GONE
            binding.tvPlayerInfo.visibility   = View.VISIBLE
            binding.tvPlayerInfo.text         = "Player: ${vm.playerName}  |  Payout: ${vm.btcAddress}"
            binding.btnStart.isEnabled        = true
        } else {
            binding.layoutRegister.visibility = View.VISIBLE
            binding.tvPlayerInfo.visibility   = View.GONE
            binding.btnStart.isEnabled        = false
        }
    }

    private fun onRegisterClicked() {
        val name = binding.etPlayerName.text?.toString()?.trim() ?: ""
        val addr = binding.etBtcAddress.text?.toString()?.trim() ?: ""
        if (name.isEmpty()) { binding.etPlayerName.error = "Required"; return }
        if (addr.isEmpty()) { binding.etBtcAddress.error = "Required"; return }
        val deviceId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        binding.btnRegister.isEnabled = false
        lifecycleScope.launch {
            vm.register(deviceId, name, addr)
            binding.btnRegister.isEnabled = true
        }
    }

    private fun onStartClicked() {
        val ctx = requireContext()
        val intent = Intent(ctx, PhoneSearchService::class.java).apply {
            action = PhoneSearchService.ACTION_START
        }
        ctx.startForegroundService(intent)
    }

    private fun onStopClicked() {
        val ctx = requireContext()
        val intent = Intent(ctx, PhoneSearchService::class.java).apply {
            action = PhoneSearchService.ACTION_STOP
        }
        ctx.startService(intent)
    }

    private fun applyState(state: SearchState) {
        binding.btnStart.isEnabled = state is SearchState.Idle || state is SearchState.Error
        binding.btnStop.isEnabled  = state is SearchState.Running || state is SearchState.Claiming

        when (state) {
            SearchState.Idle -> {
                binding.tvStatus.text    = "IDLE"
                binding.tvChunkInfo.text = ""
                binding.tvSpeed.text     = ""
                binding.progressBar.progress = 0
                binding.tvProgress.text  = ""
            }
            SearchState.Claiming -> {
                binding.tvStatus.text    = "CLAIMING CHUNK…"
                binding.tvChunkInfo.text = ""
                binding.tvSpeed.text     = ""
            }
            is SearchState.Running -> {
                val pct = if (state.chunkSize > 0) (state.keysChecked * 100 / state.chunkSize).toInt() else 0
                binding.tvStatus.text    = if (state.inTarget) "SEARCHING — TARGET ZONE" else "SEARCHING"
                binding.tvChunkInfo.text = "Chunk: ${state.chunkId}"
                binding.tvSpeed.text     = "${state.speedKps.toLong()} k/s"
                binding.progressBar.progress = pct
                binding.tvProgress.text  = "${state.keysChecked / 1_000}k / ${state.chunkSize / 1_000}k  ($pct%)"
            }
            is SearchState.Found -> {
                binding.tvStatus.text    = "KEY FOUND!"
                binding.tvChunkInfo.text = "Key: ${state.keyHex}\nAddress: ${state.address}"
                binding.tvSpeed.text     = ""
                binding.progressBar.progress = 100
            }
            is SearchState.Error -> {
                binding.tvStatus.text    = "ERROR"
                binding.tvChunkInfo.text = state.message
                binding.tvSpeed.text     = ""
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
