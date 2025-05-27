package com.example.fitcoach.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object CloudinaryUploader {
    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    fun uploadImageToCloudinary(
        fileUri: Uri,
        context: android.content.Context,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val file = File(FileUtils.getPath(context, fileUri) ?: return)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create("image/*".toMediaTypeOrNull(), file))
            .addFormDataPart("upload_preset", "user_photo_profile") // créé depuis Cloudinary
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/dggdjppqz/image/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val url = JSONObject(json!!).getString("secure_url")
                    onSuccess(url)
                } else {
                    onFailure(Exception("Upload failed: ${response.message}"))
                }
            }
        })
    }
}

object FileUtils {
    fun getPath(context: Context, uri: Uri): String? {
        val projection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
        val filePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return filePath
    }
}

