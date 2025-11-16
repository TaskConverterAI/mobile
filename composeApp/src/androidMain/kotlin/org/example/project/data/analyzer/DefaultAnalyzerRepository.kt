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
import org.example.project.model.Phrase
import org.example.project.model.SignUpUserRequest
import org.example.project.network.AnalyzerApiService
import retrofit2.Retrofit
import java.io.File
import kotlin.time.ExperimentalTime

class DefaultAnalyzerRepository: AnalyzerRepository {

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
                val signInResponse = response.body()
                //TODO save in database
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun getAllJobs(): List<AnalysisJob> {
        TODO("Not yet implemented")
    }

    override suspend fun getJobResult(jobId: Int): List<Phrase> {
        TODO("Not yet implemented")
    }

    override suspend fun analyzeText(userId: Int, text: String): Boolean {
        TODO("Not yet implemented")
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