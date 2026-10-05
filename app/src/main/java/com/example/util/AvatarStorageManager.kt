package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object AvatarStorageManager {
  private const val AVATAR_FILE_NAME = "user_avatar_profile.jpg"

  /**
   * Copies the chosen gallery image URI into app-private internal storage,
   * downsampling to a maximum dimension of 1024x1024 to ensure fast rendering and zero OOMs.
   * Returns the canonical absolute file path.
   */
  suspend fun saveImageToInternalStorage(context: Context, sourceUri: Uri): String? = withContext(Dispatchers.IO) {
    try {
      val destinationFile = File(context.filesDir, AVATAR_FILE_NAME)
      context.contentResolver.openInputStream(sourceUri)?.use { input ->
        val tempBytes = input.readBytes()
        if (tempBytes.isEmpty()) return@withContext null

        // Decode bounds first to calculate inSampleSize
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(tempBytes, 0, tempBytes.size, boundsOptions)

        var inSampleSize = 1
        val maxDim = 1024
        if (boundsOptions.outHeight > maxDim || boundsOptions.outWidth > maxDim) {
          val halfHeight = boundsOptions.outHeight / 2
          val halfWidth = boundsOptions.outWidth / 2
          while ((halfHeight / inSampleSize) >= maxDim && (halfWidth / inSampleSize) >= maxDim) {
            inSampleSize *= 2
          }
        }

        val decodeOptions = BitmapFactory.Options().apply {
          this.inSampleSize = inSampleSize
          inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val bitmap = BitmapFactory.decodeByteArray(tempBytes, 0, tempBytes.size, decodeOptions)
          ?: return@withContext null

        FileOutputStream(destinationFile).use { out ->
          bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
          out.flush()
        }
      }
      destinationFile.absolutePath
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun getAvatarFile(context: Context): File? {
    val file = File(context.filesDir, AVATAR_FILE_NAME)
    return if (file.exists() && file.length() > 0) file else null
  }

  fun loadBitmapFromPath(filePath: String?): Bitmap? {
    if (filePath.isNullOrBlank()) return null
    return try {
      val file = File(filePath)
      if (file.exists() && file.length() > 0) {
        BitmapFactory.decodeFile(file.absolutePath)
      } else null
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun deleteInternalAvatar(context: Context): Boolean {
    val file = File(context.filesDir, AVATAR_FILE_NAME)
    return if (file.exists()) file.delete() else true
  }
}
