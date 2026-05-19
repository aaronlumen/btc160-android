package com.surina.btc160.ui.puzzles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.surina.btc160.data.PuzzleData
import com.surina.btc160.databinding.FragmentPuzzlesBinding

class PuzzlesFragment : Fragment() {

    private var _binding: FragmentPuzzlesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PuzzlesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPuzzlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PuzzlesAdapter(PuzzleData.unsolved) { /* expand callback */ }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter       = adapter

        binding.toggleSolved.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                adapter.updateFilter(checkedId == com.surina.btc160.R.id.btn_all)
            }
        }
        binding.toggleSolved.check(com.surina.btc160.R.id.btn_unsolved)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
