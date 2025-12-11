package org.example.project.data.analyzer

import org.example.project.model.AnalysisJob
import org.example.project.model.MeetingSummary
import org.example.project.model.PublicSpeakerUtterance
import org.example.project.model.TaskRequest

interface AnalyzerRepository {
    suspend fun transcribeAudio(userId: String, audioPath: String, onProgress: (Float) -> Unit): Boolean
    suspend fun getAllJobs(userId: String): List<AnalysisJob>?
    suspend fun getTranscribingResult(jobId: String): List<PublicSpeakerUtterance>?
    suspend fun analyzeText(userId: String, task: TaskRequest): Boolean

    suspend fun getAnalysisResult(jobId: String): MeetingSummary?
}
