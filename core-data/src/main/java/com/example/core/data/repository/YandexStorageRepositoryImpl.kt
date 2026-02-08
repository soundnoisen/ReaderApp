package com.example.core.data.repository

import com.example.core.data.util.ProgressRequestBody
import com.example.core.domain.model.book.DeleteError
import com.example.core.domain.model.book.DeleteResult
import com.example.core.domain.model.book.DownloadError
import com.example.core.domain.model.book.DownloadProgress
import com.example.core.domain.model.book.UploadError
import com.example.core.domain.model.book.UploadProgress
import com.example.core.domain.repository.YandexStorageRepository
import jakarta.inject.Named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class YandexStorageRepositoryImpl @Inject constructor(
    private val client: OkHttpClient,
    @Named("yandexIamToken") private val token: String,
    @Named("yandexBaseUrl") private val baseUrl: String,
    @Named("yandexBucket") private val bucket: String
): YandexStorageRepository {

    override fun uploadFile(file: File, objectKey: String): Flow<UploadProgress> =
        callbackFlow {

            val url = "$baseUrl/$bucket/$objectKey"

            val requestBody = ProgressRequestBody(file) { percent ->
                trySend(UploadProgress.Uploading(percent)).isSuccess
            }

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            val call = client.newCall(request)

            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    trySend(UploadProgress.Error(UploadError.Network))
                    channel.close()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            trySend(UploadProgress.Success(url))
                        } else {
                            trySend(UploadProgress.Error(UploadError.Unknown))
                        }
                        channel.close()
                    }
                }
            })
            awaitClose { call.cancel() }
        }


    override fun downloadFile(fileUrl: String, destinationFile: File): Flow<DownloadProgress> =
        callbackFlow {

            val request = Request.Builder()
                .url(fileUrl)
                .addHeader("Authorization", "Bearer $token")
                .build()

            val call = client.newCall(request)

            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    trySend(DownloadProgress.Error(DownloadError.Network))
                    close()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!it.isSuccessful) {
                            trySend(DownloadProgress.Error(DownloadError.Unknown))
                            channel.close()
                            return
                        }

                        val body = it.body

                        try {
                            writeStreamToFile(
                                body.byteStream(),
                                destinationFile,
                                body.contentLength()
                            ) { percent ->
                                trySend(DownloadProgress.Downloading(percent)).isSuccess
                            }
                            trySend(DownloadProgress.Success)
                        } catch (e: Exception) {
                            trySend(DownloadProgress.Error(DownloadError.Unknown))
                        } finally {
                            channel.close()
                        }
                    }
                }
            })
            awaitClose { call.cancel() }
        }



    override suspend fun deleteFile(fileUrl: String): DeleteResult = withContext(Dispatchers.IO) {

        val request = Request.Builder()
            .url(fileUrl)
            .delete()
            .addHeader("Authorization", "Bearer $token")
            .build()

        try {
            val response = client.newCall(request).execute()
            response.use {
                if (!it.isSuccessful) {
                    return@withContext DeleteResult.Error(DeleteError.Unknown(it.message))
                }
                DeleteResult.Success
            }
        } catch (e: IOException) {
            DeleteResult.Error(DeleteError.Network)
        }
    }

    private fun writeStreamToFile(
        inputStream: InputStream,
        destinationFile: File,
        contentLength: Long,
        onProgress: (Int) -> Unit
    ) {
        inputStream.use { input ->
            destinationFile.outputStream().use { output ->
                val buffer = ByteArray(8 * 1024)
                var totalBytesRead = 0L
                var bytesRead = input.read(buffer)

                while (bytesRead >= 0) {
                    output.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    val percent = if (contentLength > 0)
                        (100 * totalBytesRead / contentLength).toInt()
                    else 0
                    onProgress(percent)
                    bytesRead = input.read(buffer)
                }
            }
        }
    }
}