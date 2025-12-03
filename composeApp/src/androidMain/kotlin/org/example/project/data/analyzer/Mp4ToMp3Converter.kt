package org.example.project.data.analyzer

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.File
import java.nio.ByteBuffer

class Mp4ToMp3Converter {

    suspend fun convertMp4ToMp3(
        inputPath: String,
        onProgress: (Float) -> Unit = {}
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        var extractor: MediaExtractor? = null
        var tempFile: File? = null

        try {
            extractor = MediaExtractor()
            extractor.setDataSource(inputPath)

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
            val fileSize = File(inputPath).length()

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
            return@withContext Result.failure(e)
        } finally {
            extractor?.release()
        }
    }
}