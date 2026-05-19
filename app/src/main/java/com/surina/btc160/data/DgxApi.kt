package com.surina.btc160.data

import retrofit2.Response
import retrofit2.http.*

interface DgxApi {

    @GET("status/71")
    suspend fun getChunkStatus(): Response<ChunkStatusResponse>

    @GET("chunk/71/{idx}")
    suspend fun getChunkInfo(@Path("idx") idx: Int): Response<ChunkInfoResponse>

    @GET("telemetry")
    suspend fun getTelemetry(): Response<TelemetryResponse>

    @POST("launch/71/{idx}")
    suspend fun launchChunk(@Path("idx") idx: Int): Response<LaunchResponse>

    @POST("stop")
    suspend fun stopSearch(): Response<Map<String, Any>>

    @POST("mark_done/71/{idx}")
    suspend fun markDone(@Path("idx") idx: Int): Response<Map<String, Any>>

    // ── phone hunt endpoints ──────────────────────────────────────────────────

    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>

    @POST("phone/claim")
    suspend fun claimChunk(@Body body: ClaimRequest): Response<ClaimResponse>

    @POST("phone/heartbeat/{chunkId}")
    suspend fun heartbeat(
        @Path("chunkId", encoded = true) chunkId: String,
        @Body body: HeartbeatRequest,
    ): Response<HeartbeatResponse>

    @POST("phone/complete/{chunkId}")
    suspend fun completeChunk(
        @Path("chunkId", encoded = true) chunkId: String,
        @Body body: CompleteRequest,
    ): Response<CompleteResponse>
}
