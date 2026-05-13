package com.surina.btc160.ui.chunk71

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.surina.btc160.R
import com.surina.btc160.data.ChunkStatusResponse
import com.surina.btc160.data.PuzzleData
import com.surina.btc160.data.TelemetryResponse
import com.surina.btc160.data.UiState
import com.surina.btc160.databinding.FragmentChunk71Binding

class Chunk71Fragment : Fragment() {

    private var _binding: FragmentChunk71Binding? = null
    private val binding get() = _binding!!
    private val vm: Chunk71ViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChunk71Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChunkMap()
        setupButtons()
        observeViewModels()

        vm.loadStatus()
        vm.startPolling()

        // scroll to target zone after layout
        binding.scrollView.post {
            val targetY = binding.chunkMap.targetZoneY()
            val offset  = binding.scrollView.height / 4
            binding.scrollView.smoothScrollTo(0, (targetY - offset).coerceAtLeast(0))
        }
    }

    private fun setupChunkMap() {
        binding.chunkMap.onChunkSelected = { idx ->
            vm.onChunkSelected(idx)
        }
    }

    private fun setupButtons() {
        binding.btnRefresh.setOnClickListener { vm.loadStatus() }

        binding.btnLaunch.setOnClickListener {
            val info = vm.selectedInfo.value ?: run {
                Toast.makeText(requireContext(), "Tap a chunk first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            vm.launchChunk(info.chunkIdx)
        }

        binding.btnStop.setOnClickListener { vm.stopSearch() }

        binding.btnMarkDone.setOnClickListener {
            val info = vm.selectedInfo.value ?: return@setOnClickListener
            vm.markDone(info.chunkIdx)
        }
    }

    private fun observeViewModels() {
        vm.chunkStatus.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> binding.progressBar.isVisible = true
                is UiState.Success -> {
                    binding.progressBar.isVisible = false
                    updateStatusUi(state.data)
                    binding.chunkMap.updateDoneChunks(state.data.doneChunks.toHashSet())
                }
                is UiState.Error   -> {
                    binding.progressBar.isVisible = false
                    binding.tvStatus.text = "Error: ${state.message}"
                }
                UiState.Offline    -> {
                    binding.progressBar.isVisible = false
                    binding.tvStatus.text = getString(R.string.server_offline)
                    binding.chunkMap.updateDoneChunks(emptySet())
                }
            }
        }

        vm.telemetry.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) updateTelemetryUi(state.data)
        }

        vm.selectedInfo.observe(viewLifecycleOwner) { info ->
            if (info == null) return@observe
            binding.chunkMap.setSelected(info.chunkIdx)
            val start = info.startHex.uppercase()
            val end   = info.endHex.uppercase()
            val zone  = if (info.inTargetZone) " [TARGET ZONE]" else ""
            val done  = if (info.done) " ✓ DONE" else ""
            binding.tvSelectedChunk.text =
                "Chunk #${info.chunkIdx}$zone$done\n$start\n→ $end"
            binding.btnLaunch.isEnabled   = !info.done
            binding.btnMarkDone.isEnabled = !info.done
        }

        vm.actionResult.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                vm.clearActionResult()
            }
        }
    }

    private fun updateStatusUi(data: ChunkStatusResponse) {
        val total       = PuzzleData.N_CHUNKS
        val targetTotal = data.targetEnd - data.targetStart + 1
        val targetPct   = if (targetTotal > 0) data.targetDone * 100 / targetTotal else 0
        val overallPct  = data.doneCount * 100 / total

        binding.tvStatus.text = buildString {
            appendLine("Puzzle 71 — 7.1 BTC  ·  1PWo3JeB9jrGwfHDNpdGK54CRas7fsVzXU")
            appendLine("Overall  : ${data.doneCount} / $total chunks  ($overallPct%)")
            append("Target zone: ${data.targetDone} / $targetTotal  ($targetPct%)")
        }
        binding.progressOverall.progress = overallPct
        binding.progressTarget.progress  = targetPct
    }

    private fun updateTelemetryUi(data: TelemetryResponse) {
        val status = if (data.running)
            "RUNNING — chunk ${data.activeChunk}  @  ${"%.0f".format(data.speedMks)} Mk/s"
        else
            "IDLE"
        binding.tvTelemetry.text = status
        binding.btnStop.isEnabled = data.running
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
