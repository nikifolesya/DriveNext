package ru.nikiforova.drivenext.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException

object FileUtils {
    fun saveProfileImageToInternalStorage(context: Context, email: String, uri: Uri): String? {
        val userDir = File(context.filesDir, "user_profile_images")
        if (!userDir.exists()) {
            userDir.mkdirs()
        }

        val fileName = "${email}_profile_image.jpg"
        val profileImageFile = File(userDir, fileName)

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                profileImageFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return profileImageFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}
