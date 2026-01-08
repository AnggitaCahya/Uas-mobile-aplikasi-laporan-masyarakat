package com.example.firebaseprojek

import android.graphics.Bitmap
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

object CloudinaryHelper {
    const val CLOUD_NAME = "dtdzkenjm"          // pastikan sama dengan di Cloudinary
    const val UPLOAD_PRESET = "laporan_kebakaran" // pastikan preset ini mode-nya UNSIGNED dan di-whitelist [web:1147][web:1150]
    const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"

    fun uploadImageToCloudinary(
        imageBitmap: Bitmap,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val file = bitmapToFile(imageBitmap)

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaType())
                )
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onError("Upload gagal: ${e.message ?: "Unknown error"}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onError("Upload gagal: ${response.code}")
                        return
                    }

                    val responseBody = response.body?.string()
                    val fotoUrl = extractUrlFromResponse(responseBody)

                    if (fotoUrl.isNotEmpty()) {
                        onSuccess(fotoUrl)
                    } else {
                        onError("Upload berhasil tapi URL kosong")
                    }
                }
            })
        } catch (e: Exception) {
            onError("Error: ${e.message ?: "Unknown error"}")
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File.createTempFile("laporan_", ".jpg")
        file.outputStream().use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
        }
        return file
    }

    // Lebih aman pakai JSON parser
    private fun extractUrlFromResponse(response: String?): String {
        if (response.isNullOrEmpty()) return ""
        return try {
            val json = JSONObject(response)
            json.optString("secure_url", "")
        } catch (e: Exception) {
            ""
        }
    }
}
