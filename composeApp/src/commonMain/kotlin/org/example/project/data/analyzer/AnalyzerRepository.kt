package org.example.project.data.analyzer

import kotlinx.datetime.LocalDateTime
import org.example.project.model.AnalysisJob
import org.example.project.model.Phrase
import kotlin.time.Instant

interface AnalyzerRepository {
    suspend fun transcribeAudio(userId: String, audioPath: String): Boolean
    suspend fun getAllJobs(): List<AnalysisJob>
    suspend fun getJobResult(jobId: Int): List<Phrase>
    suspend fun analyzeText(userId: Int, text: String): Boolean
}
