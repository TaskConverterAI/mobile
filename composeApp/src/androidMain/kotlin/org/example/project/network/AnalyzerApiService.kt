package org.example.project.network

import okhttp3.RequestBody
import org.example.project.model.AnalysisJob
import org.example.project.model.JobResponse
import org.example.project.model.MeetingSummary
import org.example.project.model.Phrase

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AnalyzerApiService {
    @POST("audio")
    suspend fun transcribe(
        @Query("userId") userId: String,
        @Body file: RequestBody
    ): Response<JobResponse>

    @POST("task")
    suspend fun analyze(
        @Query("userId") userId: String,
        @Body text: RequestBody
    ): Response<JobResponse>

    @GET("jobs/{jobId}")
    suspend fun getJobStatus(
        @Path("jobId") jobId: String,
    ): Response<AnalysisJob>

    @GET("jobs/{jobId}/result")
    suspend fun getAudioJobResult(
        @Path("jobId") jobId: String
    ): Response<List<Phrase>>

    @GET("jobs/{jobId}/result")
    suspend fun getTaskJobResult(
        @Path("jobId") jobId: String
    ): Response<MeetingSummary>
}