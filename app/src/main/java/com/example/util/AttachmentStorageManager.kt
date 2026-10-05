package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.AttachmentItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object AttachmentStorageManager {

  /**
   * Safely reads file stream from user-selected URI and saves permanently into internal storage:
   * context.filesDir/attachments/{ownerType}/{ownerId}/{timestamp}_{uuid}_{sanitizedName}
   */
  suspend fun saveAttachmentFromUri(
    context: Context,
    uri: Uri,
    ownerId: String,
    ownerType: String = "general"
  ): AttachmentItem? = withContext(Dispatchers.IO) {
    try {
      val contentResolver = context.contentResolver
      var fileName = "attachment_${System.currentTimeMillis()}"
      var fileSizeBytes: Long = 0L

      // Query display name and size from content resolver
      contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
          val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
          if (nameIndex != -1) {
            val name = cursor.getString(nameIndex)
            if (!name.isNullOrBlank()) fileName = name
          }
          if (sizeIndex != -1) {
            fileSizeBytes = cursor.getLong(sizeIndex)
          }
        }
      }

      val mimeType = contentResolver.getType(uri) ?: getMimeTypeFromExtension(fileName)

      // Prepare target directory in private app internal storage
      val subDir = File(context.filesDir, "attachments/${ownerType.lowercase()}/$ownerId")
      if (!subDir.exists()) subDir.mkdirs()

      val sanitizedName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
      val uniqueName = "${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}_$sanitizedName"
      val targetFile = File(subDir, uniqueName)

      // Copy stream
      contentResolver.openInputStream(uri)?.use { input ->
        targetFile.outputStream().use { output ->
          input.copyTo(output)
        }
      } ?: return@withContext null

      if (fileSizeBytes <= 0L) {
        fileSizeBytes = targetFile.length()
      }

      AttachmentItem(
        id = UUID.randomUUID().toString(),
        ownerId = ownerId,
        fileName = fileName,
        fileSizeFormatted = formatFileSize(fileSizeBytes),
        fileSizeBytes = fileSizeBytes,
        mimeType = mimeType,
        localFilePath = targetFile.absolutePath,
        createdAt = System.currentTimeMillis()
      )
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  /**
   * Opens the attachment using the Android FileProvider Intent.ACTION_VIEW
   */
  fun openAttachmentInExternalApp(context: Context, attachment: AttachmentItem) {
    try {
      val file = File(attachment.localFilePath)
      if (!file.exists()) {
        Toast.makeText(context, "Berkas tidak ditemukan di penyimpanan internal", Toast.LENGTH_SHORT).show()
        return
      }

      val contentUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )

      val viewIntent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(contentUri, attachment.mimeType.ifBlank { "*/*" })
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }

      val chooser = Intent.createChooser(viewIntent, "Buka ${attachment.fileName} dengan:")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (_: Exception) {
      Toast.makeText(context, "Tidak ada aplikasi untuk membuka format berkas ini", Toast.LENGTH_SHORT).show()
    }
  }

  /**
   * Deletes local file from storage
   */
  fun deleteLocalAttachmentFile(localFilePath: String) {
    try {
      val file = File(localFilePath)
      if (file.exists()) file.delete()
    } catch (_: Exception) {}
  }

  fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
      gb >= 1.0 -> String.format(java.util.Locale.US, "%.1f GB", gb)
      mb >= 1.0 -> String.format(java.util.Locale.US, "%.1f MB", mb)
      kb >= 1.0 -> String.format(java.util.Locale.US, "%.1f KB", kb)
      else -> "$bytes B"
    }
  }

  fun getMimeTypeFromExtension(fileName: String): String {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return when (ext) {
      "pdf" -> "application/pdf"
      "png" -> "image/png"
      "jpg", "jpeg" -> "image/jpeg"
      "webp" -> "image/webp"
      "mp4" -> "video/mp4"
      "mkv" -> "video/x-matroska"
      "zip" -> "application/zip"
      "rar" -> "application/x-rar-compressed"
      "doc", "docx" -> "application/msword"
      "xls", "xlsx" -> "application/vnd.ms-excel"
      "ppt", "pptx" -> "application/vnd.ms-powerpoint"
      "txt" -> "text/plain"
      else -> "*/*"
    }
  }
}
