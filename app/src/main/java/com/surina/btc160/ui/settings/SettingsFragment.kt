package com.surina.btc160.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.surina.btc160.data.DgxRepository
import com.surina.btc160.data.UiState
import com.surina.btc160.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repo: DgxRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("btc160", Context.MODE_PRIVATE)
        repo = DgxRepository(prefs)

        binding.etServerUrl.setText(repo.serverUrl)

        binding.btnSave.setOnClickListener {
            val url = binding.etServerUrl.text.toString().trim()
            if (url.isEmpty()) {
                binding.etServerUrl.error = "URL required"
                return@setOnClickListener
            }
            repo.updateServerUrl(url)
            Toast.makeText(requireContext(), "Server URL saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnTestConnection.setOnClickListener {
            val url = binding.etServerUrl.text.toString().trim()
            if (url.isEmpty()) return@setOnClickListener
            repo.updateServerUrl(url)
            binding.tvTestResult.text = "Testing…"
            lifecycleScope.launch {
                when (val r = repo.fetchTelemetry()) {
                    is UiState.Success -> binding.tvTestResult.text =
                        "Connected  |  Running: ${r.data.running}  |  Speed: ${"%.1f".format(r.data.speedMks)} Mk/s"
                    UiState.Offline    -> binding.tvTestResult.text = "Cannot reach server"
                    is UiState.Error   -> binding.tvTestResult.text = "Error: ${r.message}"
                    else               -> Unit
                }
            }
        }

        binding.tvAppInfo.text = buildString {
            appendLine("BTC160 Android — v1.0")
            appendLine("Puzzle 71 target: chunks 51,000–84,000 of 100,000")
            appendLine("Chunk size: 11,805,916,207,174,113 keys (~5.4×10¹⁶)")
            appendLine()
            appendLine("Chunk 51,000 starts at:")
            appendLine("  0x60A3D70A3D70A3D038")
            appendLine("Chunk 84,000 ends at:")
            appendLine("  0x75C2B94D9407895600")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
