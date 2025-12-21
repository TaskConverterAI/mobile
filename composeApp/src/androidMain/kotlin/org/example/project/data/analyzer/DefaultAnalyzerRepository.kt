package org.example.project.data.analyzer

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.project.model.AnalysisJob
import org.example.project.model.MeetingSummary
import org.example.project.model.PublicSpeakerUtterance
import org.example.project.model.TaskRequest
import org.example.project.network.RetrofitClient
import java.io.File
import kotlin.time.ExperimentalTime

private var appContext: Context? = null

fun initAnalyzerRepository(context: Context) {
    appContext = context.applicationContext
}

class DefaultAnalyzerRepository() : AnalyzerRepository {
    private val analyzerApiService = RetrofitClient.createAnalyzerApiService()


    override suspend fun transcribeAudio(
        userId: String,
        audioPath: String,
        onProgress: (Float) -> Unit
    ): Boolean {
        return try {
            onProgress(0.00F)
            val fileUri = audioPath.toUri()

            val fileInfo = getFileInfoFromUri(appContext!!, fileUri)
            var fileData = ByteArray(0)
            var fileName = fileInfo.first ?: "audio_file"
            var mimeType = fileInfo.second ?: "audio/mp3"

            if (fileInfo.second == "video/mp4") {
                fileName = "audio_file.m4a"
                mimeType = "audio/m4a"
                val converter = Mp4ToMp3Converter()
                val result = converter.extractAndEncodeToAac(appContext!!, fileUri, onProgress)
                if (result.isSuccess) {
                    fileData = result.getOrThrow()
                } else {
                    Toast.makeText(appContext, "ошибка конвертации", Toast.LENGTH_SHORT).show()
                    Log.e("MY_APP_TAG", "Ошибка конвертации: ${result.exceptionOrNull()?.message}")
                    return false
                }
            } else {
                val inputStream = appContext!!.contentResolver.openInputStream(fileUri)
                if (inputStream == null) {
                    Log.e("MY_APP_TAG", "Не удалось открыть inputStream для URI: $fileUri")
                    return false
                }
                inputStream.use { stream ->
                    fileData = stream.readBytes()
                }
            }

            if (fileData.isEmpty()) {
                Log.e("MY_APP_TAG", "ОШИБКА: fileData пустой!")
                return false
            }

            val requestBody = fileData.toRequestBody(mimeType.toMediaType())
            val audioPart = MultipartBody.Part.createFormData(
                "audio",
                fileName,
                requestBody
            )

            val response = analyzerApiService.transcribe(userId, audioPart)

            if (!response.isSuccessful) {
                Log.e("MY_APP_TAG", "Response errorBody: ${response.errorBody()?.string()}")
            } else {
                Log.i("MY_APP_TAG", "Response body: ${response.body()}")
            }

            if (response.isSuccessful) {
                onProgress(1F)
                return true
            } else {
                return false
            }
        } catch (e: Exception) {
            Log.e("MY_APP_TAG", "Exception message: ${e.message}")
            Log.e("MY_APP_TAG", "Exception stacktrace:", e)
            false
        }
    }

    private fun getFileInfoFromUri(context: Context, uri: Uri): Pair<String?, String?> {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex =
                        cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    val mimeType = context.contentResolver.getType(uri)

                    val fileName = if (nameIndex >= 0) cursor.getString(nameIndex) else null
                    Pair(fileName, mimeType)
                } else {
                    Pair(null, null)
                }
            } ?: run {
                Log.w("MY_APP_TAG", "getFileInfoFromUri - query вернул null")
                Pair(null, null)
            }
        } catch (e: Exception) {
            Log.e("MY_APP_TAG", "getFileInfoFromUri - Exception: ${e.message}", e)
            Pair(null, null)
        }
    }

    private fun createTempAudioFile(context: Context, fileName: String, mimeType: String): File {
        val storageDir = context.externalCacheDir ?: context.cacheDir
        val extension =
            getFileExtensionFromMimeType(mimeType) ?: getFileExtension(fileName) ?: "mp3"
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
                Log.i("MY_APP_TAG", "getAllJobs - Jobs count: ${response.body()?.size}")
                response.body()
            } else {
                Log.e("MY_APP_TAG", "getAllJobs - errorBody: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MY_APP_TAG", "getAllJobs - Exception: ${e.message}", e)
            null
        }
    }

    override suspend fun getTranscribingResult(jobId: String): List<PublicSpeakerUtterance>? {
        return try {
            val response = analyzerApiService.getAudioJobResult(jobId)

            if (response.isSuccessful) {
                Log.i(
                    "MY_APP_TAG",
                    "getTranscribingResult - Utterances count: ${response.body()?.size}"
                )
                return response.body()
            } else {
                Log.e(
                    "MY_APP_TAG",
                    "getTranscribingResult - errorBody: ${response.errorBody()?.string()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("MY_APP_TAG", "getTranscribingResult - Exception: ${e.message}", e)
            null
        }
    }

    override suspend fun analyzeText(
        userId: String,
        task: TaskRequest
    ): Boolean {
        return try {
            val response = analyzerApiService.analyze(userId, task)

            if (!response.isSuccessful) {
                Log.e("MY_APP_TAG", "analyzeText - errorBody: ${response.errorBody()?.string()}")
            } else {
                Log.i("MY_APP_TAG", "analyzeText - Response body: ${response.body()}")
            }

            if (response.isSuccessful) {
                Log.i("MY_APP_TAG", "analyzeText successful")
                true
            } else {
                Log.e("MY_APP_TAG", "analyzeText error")
                false
            }
        } catch (e: Exception) {
            Log.e("MY_APP_TAG", "Exception: ${e.message}", e)
            false
        }
    }

    override suspend fun getAnalysisResult(jobId: String): MeetingSummary? {
        return try {
            val response = analyzerApiService.getTaskJobResult(jobId)

            if (response.isSuccessful) {
                Log.i("MY_APP_TAG", "getAnalysisResult - Summary received: ${response.body()}")
                return response.body()
            } else {
                Log.e(
                    "MY_APP_TAG",
                    "getAnalysisResult - errorBody: ${response.errorBody()?.string()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("MY_APP_TAG", "getAnalysisResult - Exception: ${e.message}", e)
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
