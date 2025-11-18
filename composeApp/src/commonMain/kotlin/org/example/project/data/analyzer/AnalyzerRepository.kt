package org.example.project.data.analyzer

import org.example.project.model.AnalysisJob
import org.example.project.model.MeetingSummary
import org.example.project.model.Phrase

interface AnalyzerRepository {
    suspend fun transcribeAudio(userId: String, audioPath: String): Boolean
    suspend fun getAllJobs(userId: String): List<AnalysisJob>?
    suspend fun getTranscribingResult(jobId: String): List<Phrase>?
    suspend fun analyzeText(userId: String, text: String, hints: String): Boolean

    suspend fun getAnalysisResult(jobId: String): MeetingSummary?
}
