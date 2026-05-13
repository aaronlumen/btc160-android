package com.surina.btc160.ui.puzzles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.surina.btc160.R
import com.surina.btc160.data.PuzzleInfo
import com.surina.btc160.databinding.ItemPuzzleBinding

class PuzzlesAdapter(
    private var items: List<PuzzleInfo>,
    private val onExpand: (PuzzleInfo) -> Unit,
) : RecyclerView.Adapter<PuzzlesAdapter.VH>() {

    private val expanded = mutableSetOf<Int>()

    inner class VH(val b: ItemPuzzleBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPuzzleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, position: Int) {
        val p   = items[position]
        val ctx = h.itemView.context

        h.b.tvBits.text    = "${p.bits}-bit"
        h.b.tvBtc.text     = "${p.btcValue} BTC"
        h.b.tvAddress.text = if (p.address.isNotEmpty()) p.address else "(early puzzle)"
        h.b.ivStatus.setImageResource(
            if (p.solved) R.drawable.ic_check else R.drawable.ic_search
        )
        h.b.ivStatus.setColorFilter(
            ContextCompat.getColor(ctx, if (p.solved) R.color.green else R.color.orange)
        )

        // puzzle 71 highlight
        if (p.bits == 71) {
            h.b.root.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.card_highlight))
        } else {
            h.b.root.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.card_surface))
        }

        val isExp = p.bits in expanded
        h.b.layoutDetail.visibility = if (isExp) View.VISIBLE else View.GONE
        h.b.tvHash160.text  = if (p.hash160.isNotEmpty()) "hash160: ${p.hash160}" else ""
        h.b.tvPubkey.text   = if (!p.publicKey.isNullOrEmpty()) "pubkey: ${p.publicKey}" else ""
        h.b.tvSolvedKey.text = if (!p.solvedKey.isNullOrEmpty()) "key: ${p.solvedKey}" else ""
        h.b.tvRange.text    = "${p.rangeMin}\n→ ${p.rangeMax}"

        h.itemView.setOnClickListener {
            if (isExp) expanded.remove(p.bits) else expanded.add(p.bits)
            notifyItemChanged(position)
            if (!isExp) onExpand(p)
        }
    }

    fun updateFilter(showSolved: Boolean) {
        items = if (showSolved) com.surina.btc160.data.PuzzleData.ALL
                else com.surina.btc160.data.PuzzleData.unsolved
        notifyDataSetChanged()
    }
}
