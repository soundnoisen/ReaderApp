package com.example.core.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.core.domain.model.profile.UpdateProfileError
import com.example.core.domain.model.profile.UpdateProfileResult
import com.example.core.domain.repository.CloudinaryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class CloudinaryRepositoryImpl @Inject constructor(
    private val client: OkHttpClient,
    @ApplicationContext private val context: Context,
    @Named("cloudinaryCloudName") private val cloudName: String,
    @Named("cloudinaryUploadPreset") private val uploadPreset: String
): CloudinaryRepository {

    override suspend fun uploadPhoto(uri: Uri): UpdateProfileResult = withContext(Dispatchers.IO) {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return@withContext UpdateProfileResult.Error(UpdateProfileError.InvalidFile)

        val mimeType = context.contentResolver.getType(uri)
            ?: return@withContext UpdateProfileResult.Error(UpdateProfileError.InvalidFile)

        if (!mimeType.startsWith("image/")) {
            return@withContext UpdateProfileResult.Error(UpdateProfileError.InvalidFile)
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.Companion.FORM)
            .addFormDataPart("file", "avatar.jpg", bytes.toRequestBody(mimeType.toMediaType()))
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            response.use {
                if (!it.isSuccessful) {
                    return@withContext UpdateProfileResult.Error(UpdateProfileError.Network)
                }
                val json = JSONObject(it.body?.string().orEmpty())
                val url = json.optString("secure_url", "")
                if (url.isNotEmpty()) {
                    UpdateProfileResult.Success(url, url.toUri())
                } else {
                    UpdateProfileResult.Error(UpdateProfileError.FailedToGetImage)
                }
            }
        } catch (e: IOException) {
            UpdateProfileResult.Error(UpdateProfileError.Network)
        } catch (e: Exception) {
            UpdateProfileResult.Error(UpdateProfileError.Unknow(e.message.toString()))
        }
    }
}