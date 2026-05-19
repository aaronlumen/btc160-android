package com.surina.btc160.data

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("device_id")   val deviceId: String,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("btc_address") val btcAddress: String,
    @SerializedName("contact")     val contact: String = "",
)

data class RegisterResponse(
    @SerializedName("player_token")       val playerToken: String,
    @SerializedName("player_name")        val playerName: String,
    @SerializedName("already_registered") val alreadyRegistered: Boolean,
)

data class ClaimRequest(
    @SerializedName("player_token") val playerToken: String,
    @SerializedName("puzzle")       val puzzle: Int = 71,
)

data class ClaimResponse(
    @SerializedName("chunk_id")       val chunkId: String,
    @SerializedName("puzzle")         val puzzle: Int,
    @SerializedName("start_hex")      val startHex: String,
    @SerializedName("end_hex")        val endHex: String,
    @SerializedName("size")           val size: Long,
    @SerializedName("in_target_zone") val inTargetZone: Boolean,
    @SerializedName("deadline")       val deadline: String,
    @SerializedName("target_address") val targetAddress: String,
    @SerializedName("canary_keys")    val canaryKeys: List<String>,
)

data class HeartbeatRequest(
    @SerializedName("player_token")  val playerToken: String,
    @SerializedName("keys_checked")  val keysChecked: Long,
    @SerializedName("speed_kps")     val speedKps: Float,
)

data class HeartbeatResponse(
    @SerializedName("status") val status: String,
    @SerializedName("reason") val reason: String?,
)

data class CanaryAnswer(
    @SerializedName("priv_hex") val privHex: String,
    @SerializedName("address")  val address: String,
)

data class CompleteRequest(
    @SerializedName("player_token")    val playerToken: String,
    @SerializedName("keys_checked")    val keysChecked: Long,
    @SerializedName("elapsed_seconds") val elapsedSeconds: Float,
    @SerializedName("canary_answers")  val canaryAnswers: List<CanaryAnswer>,
    @SerializedName("found_key")       val foundKey: String = "",
)

data class CompleteResponse(
    @SerializedName("verified") val verified: Boolean,
    @SerializedName("status")   val status: String,
    @SerializedName("message")  val message: String?,
)
