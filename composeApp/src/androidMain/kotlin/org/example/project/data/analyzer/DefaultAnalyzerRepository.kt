package org.example.project.data.analyzer

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.example.project.model.AnalysisJob
import org.example.project.model.MeetingSummary
import org.example.project.model.Phrase
import org.example.project.model.SignUpUserRequest
import org.example.project.network.AnalyzerApiService
import retrofit2.Retrofit
import java.io.File
import kotlin.time.ExperimentalTime

class DefaultAnalyzerRepository : AnalyzerRepository {

    private val baseAnalyzerUrl = "http://127.0.0.1:8000/"

    @OptIn(ExperimentalTime::class)
    private val serializersModule = SerializersModule {
        contextual(Instant::class, InstantSerializer)
    }

    private val json = Json {
        serializersModule = serializersModule
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    val contentType = "application/json".toMediaType()
    private val analyzerRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(contentType))
        .baseUrl(baseAnalyzerUrl)
        .build()

    private val analyzerApiService: AnalyzerApiService by lazy {
        analyzerRetrofit.create(AnalyzerApiService::class.java)
    }


    override suspend fun transcribeAudio(
        userId: String,
        audioPath: String
    ): Boolean {
        return try {
            val file = File(audioPath)
            val requestBody = file.asRequestBody("application/octet-stream".toMediaType())
            val response = analyzerApiService.transcribe(userId, requestBody)

            if (response.isSuccessful) {
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun getAllJobs(
        userId: String
    ): List<AnalysisJob>? {
        return try {
            val response = analyzerApiService.getAllJobs(userId)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getTranscribingResult(jobId: String): List<Phrase>? {
        return try {
            val response = analyzerApiService.getAudioJobResult(jobId)

            if (response.isSuccessful) {
                return response.body()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun analyzeText(userId: String, text: String, hints: String): Boolean {
        return try {
            val requestMessage = "$hints $text"

            val file = File.createTempFile("analyzed_text", ".txt")
            file.writeText(requestMessage)
            val requestBody = file.asRequestBody("text/plain".toMediaType())

            val response = analyzerApiService.analyze(userId, requestBody)

            if (response.isSuccessful) {
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun getAnalysisResult(jobId: String): MeetingSummary? {
        return try {
            val response = analyzerApiService.getTaskJobResult(jobId)

            if (response.isSuccessful) {
                return response.body()
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }


    @OptIn(ExperimentalTime::class)
    companion object InstantSerializer : KSerializer<Instant> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Instant) {
            encoder.encodeString(value.toString()) // ISO-8601
        }

        override fun deserialize(decoder: Decoder): Instant {
            return Instant.parse(decoder.decodeString())
        }
    }
}