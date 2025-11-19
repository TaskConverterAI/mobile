package org.example.project.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.example.project.model.AnalysisJob
import org.example.project.model.JobResponse
import org.example.project.model.MeetingSummary
import org.example.project.model.PublicSpeakerUtterance
import org.example.project.model.TaskRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AnalyzerApiService {
    @Multipart
    @POST("audio")
    suspend fun transcribe(
        @Query("userID") userID: String,
        @Part audio: MultipartBody.Part
    ): Response<JobResponse>

    @POST("task")
    suspend fun analyze(
        @Query("userID") userID: String,
        @Body task: TaskRequest
    ): Response<JobResponse>

    @GET("jobs")
    suspend fun getAllJobs(
        @Query("userID") userID: String
    ): Response<List<AnalysisJob>>

    @GET("jobs/{jobId}")
    suspend fun getJobStatus(
        @Path("jobId") jobId: String,
    ): Response<AnalysisJob>

    @GET("jobs/{jobId}/result")
    suspend fun getAudioJobResult(
        @Path("jobId") jobId: String
    ): Response<List<PublicSpeakerUtterance>>

    @GET("jobs/{jobId}/result")
    suspend fun getTaskJobResult(
        @Path("jobId") jobId: String
    ): Response<MeetingSummary>
}