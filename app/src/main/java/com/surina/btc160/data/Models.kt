package com.surina.btc160.data

import com.google.gson.annotations.SerializedName

// ── API response models ───────────────────────────────────────────────────────

data class ChunkStatusResponse(
    @SerializedName("puzzle")            val puzzle: Int,
    @SerializedName("n_chunks")          val nChunks: Int,
    @SerializedName("done_count")        val doneCount: Int,
    @SerializedName("done_chunks")       val doneChunks: List<Int>,
    @SerializedName("target_start")      val targetStart: Int,
    @SerializedName("target_end")        val targetEnd: Int,
    @SerializedName("target_done")       val targetDone: Int,
    @SerializedName("target_done_chunks") val targetDoneChunks: List<Int>,
)

data class ChunkInfoResponse(
    @SerializedName("chunk_idx")      val chunkIdx: Int,
    @SerializedName("start_hex")      val startHex: String,
    @SerializedName("end_hex")        val endHex: String,
    @SerializedName("done")           val done: Boolean,
    @SerializedName("in_target_zone") val inTargetZone: Boolean,
)

data class TelemetryResponse(
    @SerializedName("active_chunk")   val activeChunk: Int,
    @SerializedName("speed_mks")      val speedMks: Double,
    @SerializedName("running")        val running: Boolean,
    @SerializedName("recent_output")  val recentOutput: List<String>,
)

data class LaunchResponse(
    @SerializedName("launched")   val launched: Boolean,
    @SerializedName("chunk_idx")  val chunkIdx: Int,
    @SerializedName("start_hex")  val startHex: String,
    @SerializedName("end_hex")    val endHex: String,
    @SerializedName("error")      val error: String?,
)

// ── puzzle reference data ─────────────────────────────────────────────────────

data class PuzzleInfo(
    val bits: Int,
    val rangeMin: String,   // hex, no 0x
    val rangeMax: String,   // hex, no 0x
    val address: String,
    val btcValue: Double,
    val hash160: String,
    val publicKey: String?,
    val solved: Boolean,
    val solvedKey: String? = null,
)

// ── UI state ──────────────────────────────────────────────────────────────────

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Offline : UiState<Nothing>()
}
