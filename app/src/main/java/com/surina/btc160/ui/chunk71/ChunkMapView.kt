package com.surina.btc160.ui.chunk71

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.surina.btc160.data.PuzzleData

class ChunkMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    companion object {
        const val GRID_COLS = 200
        const val GRID_ROWS = 500    // 200 × 500 = 100,000
    }

    // colours
    private val COL_NORMAL  = Color.parseColor("#424242")  // unchecked, outside target
    private val COL_TARGET  = Color.parseColor("#F7931A")  // unchecked, inside target (orange)
    private val COL_DONE    = Color.parseColor("#2E7D32")  // done, outside target
    private val COL_DONE_TG = Color.parseColor("#66BB6A")  // done, inside target (bright green)
    private val COL_SEL     = Color.WHITE

    private var doneChunks: Set<Int> = emptySet()
    private var selectedChunk = -1
    private var bitmap: Bitmap? = null

    private val bitmapPaint = Paint().apply { isFilterBitmap = false }
    private val selPaint    = Paint().apply {
        color = COL_SEL; style = Paint.Style.STROKE; strokeWidth = 3f
    }
    private val labelPaint  = Paint().apply {
        color = Color.WHITE; textSize = 28f; isFakeBoldText = true
        setShadowLayer(4f, 1f, 1f, Color.BLACK)
    }

    var onChunkSelected: ((Int) -> Unit)? = null

    fun updateDoneChunks(done: Set<Int>) {
        doneChunks = done
        rebuildBitmap()
        invalidate()
    }

    fun setSelected(idx: Int) {
        selectedChunk = idx
        invalidate()
    }

    private fun rebuildBitmap() {
        val pixels = IntArray(PuzzleData.N_CHUNKS)
        val targetStart = PuzzleData.TARGET_START
        val targetEnd   = PuzzleData.TARGET_END
        for (i in 0 until PuzzleData.N_CHUNKS) {
            val inTarget = i in targetStart..targetEnd
            val done     = i in doneChunks
            pixels[i] = when {
                done && inTarget -> COL_DONE_TG
                done             -> COL_DONE
                inTarget         -> COL_TARGET
                else             -> COL_NORMAL
            }
        }
        bitmap = Bitmap.createBitmap(pixels, GRID_COLS, GRID_ROWS, Bitmap.Config.ARGB_8888)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec).coerceAtLeast(200)
        val h = w * GRID_ROWS / GRID_COLS
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        val bmp = bitmap
        if (bmp == null) {
            rebuildBitmap()
            return
        }

        // draw scaled bitmap (crisp nearest-neighbour)
        canvas.drawBitmap(bmp, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), bitmapPaint)

        // target zone border
        val targetStartRow = PuzzleData.TARGET_START / GRID_COLS
        val targetEndRow   = PuzzleData.TARGET_END   / GRID_COLS
        val cellH = height.toFloat() / GRID_ROWS
        val borderPaint = Paint().apply {
            color = Color.parseColor("#FFCC02"); style = Paint.Style.STROKE; strokeWidth = 4f
        }
        canvas.drawRect(0f, targetStartRow * cellH, width.toFloat(), (targetEndRow + 1) * cellH, borderPaint)

        // label target zone
        canvas.drawText("TARGET ZONE  51k–84k", 12f, targetStartRow * cellH + 36f, labelPaint)

        // selected chunk highlight
        if (selectedChunk in 0 until PuzzleData.N_CHUNKS) {
            val row  = selectedChunk / GRID_COLS
            val col  = selectedChunk % GRID_COLS
            val cellW = width.toFloat() / GRID_COLS
            canvas.drawRect(col * cellW, row * cellH, (col + 1) * cellW, (row + 1) * cellH, selPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val cellW = width.toFloat() / GRID_COLS
            val cellH = height.toFloat() / GRID_ROWS
            val col = (event.x / cellW).toInt().coerceIn(0, GRID_COLS - 1)
            val row = (event.y / cellH).toInt().coerceIn(0, GRID_ROWS - 1)
            val idx = row * GRID_COLS + col
            selectedChunk = idx
            onChunkSelected?.invoke(idx)
            invalidate()
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    /** Y-pixel that corresponds to the first row of the target zone */
    fun targetZoneY(): Int {
        val targetRow = PuzzleData.TARGET_START / GRID_COLS
        return (height.toFloat() * targetRow / GRID_ROWS).toInt()
    }
}
