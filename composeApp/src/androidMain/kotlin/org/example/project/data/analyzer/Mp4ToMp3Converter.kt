package org.example.project.data.analyzer

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import androidx.media3.common.util.UnstableApi


@OptIn(UnstableApi::class)
class Mp4ToMp3Converter {

    suspend fun extractAndEncodeToAac(
        context: Context?,
        inputUri: Uri,
        onProgress: (Float) -> Unit = {}
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        var extractor: MediaExtractor? = null
        var decoder: MediaCodec? = null
        var encoder: MediaCodec? = null
        var muxer: MediaMuxer? = null
        var tempOutputFile: File? = null

        try {
            // 1. Получаем реальный путь из Uri
            val actualPath = getRealPathFromUri(context!!, inputUri)
            if (actualPath == null) {
                return@withContext Result.failure(IllegalStateException("Не удалось получить путь к файлу"))
            }

            // 2. Настраиваем Extractor для поиска аудиодорожки
            extractor = MediaExtractor()
            extractor.setDataSource(actualPath)

            val trackCount = extractor.trackCount
            var audioTrackIndex = -1
            var inputFormat: MediaFormat? = null

            for (i in 0 until trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("audio/") == true) {
                    audioTrackIndex = i
                    inputFormat = format
                    Log.i("AAC_CONVERTER", "Найдена аудиодорожка: $mime")
                    break
                }
            }

            if (audioTrackIndex == -1) {
                return@withContext Result.failure(IllegalStateException("В файле не найдена аудиодорожка"))
            }
            extractor.selectTrack(audioTrackIndex)

            // 3. Создаем временный файл для вывода
            tempOutputFile = File.createTempFile("audio_output", ".m4a")
            muxer = MediaMuxer(tempOutputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            // 4. Создаем декодер для исходного аудио
            val inputMime = inputFormat!!.getString(MediaFormat.KEY_MIME)!!
            decoder = MediaCodec.createDecoderByType(inputMime)
            decoder.configure(inputFormat, null, null, 0)
            decoder.start()

            // 5. Создаем кодировщик для AAC
            // Формат для кодировщика AAC
            val outputFormat = MediaFormat.createAudioFormat(
                MediaFormat.MIMETYPE_AUDIO_AAC,
                inputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE),
                inputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            ).apply {
                setInteger(MediaFormat.KEY_BIT_RATE, 128000) // Битрейт 128 кбит/с
                setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            }

            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            encoder.start()

            // 6. Настраиваем Muxer и запускаем процесс
            var muxerTrackIndex = -1
            var muxerStarted = false

            // Буферы для обработки данных
            val decoderInputBuffers = decoder.inputBuffers
            val decoderOutputBuffers = decoder.outputBuffers
            val encoderInputBuffers = encoder.inputBuffers
            val encoderOutputBuffers = encoder.outputBuffers

            val info = MediaCodec.BufferInfo()
            var sawInputEOS = false
            var sawOutputEOS = false
            var totalBytesRead: Long = 0
            val fileSize = File(actualPath).length()

            // 7. Основной цикл обработки данных
            while (!sawOutputEOS) {
                // Декодирование
                if (!sawInputEOS) {
                    val inIndex = decoder.dequeueInputBuffer(10000)
                    if (inIndex >= 0) {
                        val buffer = decoderInputBuffers[inIndex]
                        val sampleSize = extractor.readSampleData(buffer, 0)
                        if (sampleSize < 0) {
                            decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            sawInputEOS = true
                        } else {
                            val presentationTimeUs = extractor.sampleTime
                            decoder.queueInputBuffer(inIndex, 0, sampleSize, presentationTimeUs, 0)
                            extractor.advance()
                            totalBytesRead += sampleSize

                            // Обновление прогресса
                            withContext(Dispatchers.Main) {
                                onProgress(totalBytesRead.toFloat() / fileSize)
                            }
                        }
                    }
                }

                // Обработка вывода декодера / ввода энкодера
                var decoderOutputIndex = decoder.dequeueOutputBuffer(info, 10000)
                while (decoderOutputIndex >= 0) {
                    val buffer = decoderOutputBuffers[decoderOutputIndex]
                    // ... (здесь должна быть логика передачи декодированных данных в энкодер)
                    decoder.releaseOutputBuffer(decoderOutputIndex, false)
                    decoderOutputIndex = decoder.dequeueOutputBuffer(info, 0)
                }

                // Обработка вывода энкодера и запись в Muxer
                var encoderOutputIndex = encoder.dequeueOutputBuffer(info, 10000)
                while (encoderOutputIndex >= 0) {
                    val buffer = encoderOutputBuffers[encoderOutputIndex]
                    if ((info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        // Конфигурационные данные - сохраняем для Muxer
                        if (muxerStarted) {
                            Log.w("AAC_CONVERTER", "Конфигурационные данные пришли после старта Muxer")
                        }
                    } else if (info.size > 0) {
                        if (!muxerStarted) {
                            // Получаем итоговый формат от энкодера и стартуем Muxer
                            val actualOutputFormat = encoder.outputFormat
                            muxerTrackIndex = muxer.addTrack(actualOutputFormat)
                            muxer.start()
                            muxerStarted = true
                        }
                        // Записываем закодированные данные в Muxer
                        buffer.position(info.offset)
                        buffer.limit(info.offset + info.size)
                        muxer.writeSampleData(muxerTrackIndex, buffer, info)
                    }

                    encoder.releaseOutputBuffer(encoderOutputIndex, false)

                    if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        sawOutputEOS = true
                    }
                    encoderOutputIndex = encoder.dequeueOutputBuffer(info, 0)
                }
                yield() // Даем возможность работать другим корутинам
            }

            // 8. Завершаем работу и читаем результат
            muxer?.stop()
            val resultBytes = tempOutputFile.readBytes()
            tempOutputFile.delete()

            return@withContext Result.success(resultBytes)

        } catch (e: Exception) {
            e.printStackTrace()
            tempOutputFile?.delete()
            return@withContext Result.failure(e)
        } finally {
            // 9. Освобождаем ресурсы
            extractor?.release()
            decoder?.stop()
            decoder?.release()
            encoder?.stop()
            encoder?.release()
            muxer?.release()
        }
    }


    suspend fun convertMp4ToMp3(
        context: Context?,
        inputPath: Uri,
        onProgress: (Float) -> Unit = {}
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        var extractor: MediaExtractor? = null
        var tempFile: File? = null

        try {
            val actualPath = getRealPathFromUri(context!!, inputPath)
            if (actualPath == null) {
                return@withContext Result.failure(IllegalStateException("Путь до файла некорректен"))
            }
            extractor = MediaExtractor()
            extractor.setDataSource(actualPath)

            val trackCount = extractor.trackCount
            var audioTrackIndex = -1
            var audioFormat: MediaFormat? = null

            for (i in 0 until trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)

                if (mime?.startsWith("audio/") == true) {
                    audioTrackIndex = i
                    audioFormat = format
                    break
                }
            }

            if (audioTrackIndex == -1) {
                Log.i("MY_APP_TAG", "Аудио дорожка не найдена")
                return@withContext Result.failure(IllegalStateException("Аудио дорожка не найдена"))
            }

            tempFile = File.createTempFile("audio_temp", ".mp3")

            val muxer =
                MediaMuxer(tempFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            extractor.selectTrack(audioTrackIndex)

            audioFormat?.setString(MediaFormat.KEY_MIME, "audio/mpeg")
            val writeAudioTrack = muxer.addTrack(audioFormat!!)

            muxer.start()

            val bufferSize = 1024 * 1024
            val buffer = ByteBuffer.allocate(bufferSize)
            val bufferInfo = android.media.MediaCodec.BufferInfo()

            var totalSize = 0L
            val fileSize = File(actualPath).length()

            while (true) {
                buffer.clear()
                val sampleSize = extractor.readSampleData(buffer, 0)

                if (sampleSize < 0) {
                    break
                }

                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME

                muxer.writeSampleData(writeAudioTrack, buffer, bufferInfo)
                extractor.advance()

                totalSize += sampleSize

                withContext(Dispatchers.Main) {
                    onProgress(totalSize.toFloat() / fileSize)
                }

                yield()
            }

            muxer.stop()
            muxer.release()

            val resultBytes = tempFile.readBytes()

            tempFile.delete()

            return@withContext Result.success(resultBytes)

        } catch (e: Exception) {
            e.printStackTrace()
            tempFile?.delete()
            Log.i("MY_APP_TAG", "${e.message}")
            return@withContext Result.failure(e)
        } finally {
            extractor?.release()
        }
    }

    @SuppressLint("Range")
    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        return when {
            uri.scheme == null || uri.scheme == ContentResolver.SCHEME_FILE -> {
                uri.path
            }

            uri.scheme == ContentResolver.SCHEME_CONTENT -> {
                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(OpenableColumns.DISPLAY_NAME),
                    null, null, null
                )

                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val displayName =
                            c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        val tempFile = File(context.cacheDir, displayName ?: "temp_audio.mp4")

                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                input.copyTo(output)
                            }
                        }

                        tempFile.absolutePath
                    } else {
                        null
                    }
                }
            }

            else -> {
                println("Неподдерживаемая схема URI: ${uri.scheme}")
                null
            }
        }
    }
}