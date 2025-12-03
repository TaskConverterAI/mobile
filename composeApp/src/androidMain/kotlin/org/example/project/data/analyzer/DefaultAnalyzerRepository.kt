package org.example.project.data.analyzer

import android.content.Context
import android.net.Uri
import android.util.Log
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
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.project.model.AnalysisJob
import org.example.project.model.MeetingSummary
import org.example.project.model.PublicSpeakerUtterance
import org.example.project.model.TaskRequest
import org.example.project.network.AnalyzerApiService
import retrofit2.Retrofit
import java.io.File
import kotlin.time.ExperimentalTime
import androidx.core.net.toUri

private var appContext: Context? = null

fun initAnalyzerRepository(context: Context) {
    appContext = context.applicationContext
}

class DefaultAnalyzerRepository() : AnalyzerRepository {

    private val baseAnalyzerUrl = "http://192.168.31.79:8080/"

    @OptIn(ExperimentalTime::class)
    private val serializerModule = SerializersModule {
        contextual(Instant::class, InstantSerializer)
    }

    private val json = Json {
        this.serializersModule = serializerModule
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
        audioPath: String,
        onProgress: (Float) ->Unit
    ): Boolean {
        return try {
            onProgress(0.00F)
            val fileUri = audioPath.toUri()
            val fileInfo = getFileInfoFromUri(appContext!!, fileUri)
            var fileData = ByteArray(0)
            if (fileInfo.second == "video/mp4") {
                val converter = Mp4ToMp3Converter()
                converter.convertMp4ToMp3(audioPath, onProgress)
            }
            else{
                val inputStream = appContext!!.contentResolver.openInputStream(fileUri)
                inputStream?.use { stream ->

                    fileData = stream.readBytes()
                }
            }

            val fileName = fileInfo.first ?: "audio_file"
            val mimeType = fileInfo.second ?: "audio/*"

            val requestBody = fileData.toRequestBody(mimeType.toMediaType())
            val audioPart = MultipartBody.Part.createFormData(
                "audio",
                fileName,
                requestBody
            )
            analyzerApiService.getAllJobs(userId)
            val response = analyzerApiService.transcribe(userId, audioPart)

            if (response.isSuccessful)
            {
                onProgress(1F)
                return true
            } else {
                return false
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun getFileInfoFromUri(context: Context, uri: Uri): Pair<String?, String?> {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    val mimeType = context.contentResolver.getType(uri)

                    val fileName = if (nameIndex >= 0) cursor.getString(nameIndex) else null
                    Pair(fileName, mimeType)
                } else {
                    Pair(null, null)
                }
            } ?: Pair(null, null)
        } catch (_: Exception) {
            Pair(null, null)
        }
    }

    private fun createTempAudioFile(context: Context, fileName: String, mimeType: String): File {
        val storageDir = context.externalCacheDir ?: context.cacheDir
        val extension = getFileExtensionFromMimeType(mimeType) ?: getFileExtension(fileName) ?: "mp3"
        val prefix = "audio_upload_"

        return File.createTempFile(prefix, ".$extension", storageDir)
    }

    private fun getFileExtensionFromMimeType(mimeType: String?): String? {
        return when (mimeType) {
            "audio/mpeg" -> "mp3"
            "audio/wav" -> "wav"
            "audio/ogg" -> "ogg"
            "audio/mp4" -> "m4a"
            "audio/aac" -> "aac"
            else -> null
        }
    }

    private fun getFileExtension(fileName: String?): String? {
        return fileName?.substringAfterLast('.', missingDelimiterValue = "")
            ?.takeIf { it.isNotEmpty() }
    }
    private fun getAudioMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "wav" -> "audio/wav"
            "mp3" -> "audio/mpeg"
            "ogg" -> "audio/ogg"
            "m4a" -> "audio/mp4"
            else -> "audio/*"
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

    override suspend fun getTranscribingResult(jobId: String): List<PublicSpeakerUtterance>? {
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

    override suspend fun analyzeText(
        userId: String,
        task: TaskRequest
    ): Boolean {
        return try {
            val response = analyzerApiService.analyze(userId, task)

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
