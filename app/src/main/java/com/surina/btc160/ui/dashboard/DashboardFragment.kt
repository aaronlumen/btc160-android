package com.surina.btc160.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.surina.btc160.data.ChunkStatusResponse
import com.surina.btc160.data.PuzzleData
import com.surina.btc160.data.TelemetryResponse
import com.surina.btc160.data.UiState
import com.surina.btc160.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val vm: DashboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvServerUrl.text = vm.serverUrl.value
        binding.btnRefresh.setOnClickListener { vm.refreshStatus() }

        vm.telemetry.observe(viewLifecycleOwner)    { updateTelemetry(it) }
        vm.chunkStatus.observe(viewLifecycleOwner)  { updateStatus(it) }
        vm.serverUrl.observe(viewLifecycleOwner)    { binding.tvServerUrl.text = it }

        vm.refreshStatus()
        vm.startPolling()
    }

    private fun updateTelemetry(state: UiState<TelemetryResponse>) {
        when (state) {
            is UiState.Success -> {
                val d = state.data
                binding.tvConnStatus.text  = if (d.running) "CONNECTED — SEARCHING" else "CONNECTED — IDLE"
                binding.tvSpeed.text       = if (d.running) "${"%.1f".format(d.speedMks)} Mk/s" else "—"
                binding.tvActiveChunk.text = if (d.running) "Chunk ${d.activeChunk}" else "—"
                binding.tvLastOutput.text  = d.recentOutput.takeLast(3).joinToString("\n")
            }
            UiState.Offline -> {
                binding.tvConnStatus.text  = "OFFLINE"
                binding.tvSpeed.text       = "—"
                binding.tvActiveChunk.text = "—"
                binding.tvLastOutput.text  = "Cannot reach server"
            }
            else -> Unit
        }
    }

    private fun updateStatus(state: UiState<ChunkStatusResponse>) {
        when (state) {
            is UiState.Success -> {
                val d           = state.data
                val total       = PuzzleData.N_CHUNKS
                val targetTotal = d.targetEnd - d.targetStart + 1
                val overallPct  = d.doneCount * 100 / total
                val targetPct   = if (targetTotal > 0) d.targetDone * 100 / targetTotal else 0

                binding.tvProgressOverall.text  = "${d.doneCount} / $total chunks  ($overallPct%)"
                binding.tvProgressTarget.text   = "${d.targetDone} / $targetTotal in target  ($targetPct%)"
                binding.pbOverall.progress      = overallPct
                binding.pbTarget.progress       = targetPct
            }
            is UiState.Error -> {
                binding.tvProgressOverall.text = state.message
                binding.tvProgressTarget.text  = ""
            }
            else -> Unit
        }
    }

    override fun onResume() {
        super.onResume()
        vm.startPolling()
    }

    override fun onPause() {
        super.onPause()
        vm.stopPolling()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
