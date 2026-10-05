package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttachmentItem
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandOliveBg
import com.example.ui.theme.BrandSecondary
import com.example.util.AttachmentStorageManager

enum class AttachmentFileCategory(
  val icon: ImageVector,
  val color: Color,
  val label: String
) {
  PDF(Icons.Default.PictureAsPdf, Color(0xFFEF4444), "PDF"),
  DOC(Icons.Default.Description, Color(0xFF3B82F6), "DOC"),
  SHEET(Icons.Default.TableChart, Color(0xFF10B981), "XLS"),
  IMAGE(Icons.Default.Image, Color(0xFF8B5CF6), "IMG"),
  VIDEO(Icons.Default.Movie, Color(0xFF06B6D4), "VID"),
  ARCHIVE(Icons.Default.Archive, Color(0xFFF59E0B), "ZIP");

  companion object {
    fun fromMimeOrName(mimeType: String, fileName: String): AttachmentFileCategory {
      val ext = fileName.substringAfterLast('.', "").lowercase()
      return when {
        mimeType.contains("pdf", ignoreCase = true) || ext == "pdf" -> PDF
        mimeType.startsWith("image/", ignoreCase = true) || ext in listOf("jpg", "jpeg", "png", "webp", "gif") -> IMAGE
        mimeType.startsWith("video/", ignoreCase = true) || ext in listOf("mp4", "mkv", "mov", "avi") -> VIDEO
        mimeType.contains("excel", ignoreCase = true) || ext in listOf("xls", "xlsx", "csv") -> SHEET
        mimeType.contains("zip", ignoreCase = true) || ext in listOf("zip", "rar", "7z", "tar") -> ARCHIVE
        else -> DOC
      }
    }
  }
}

/**
 * Super High-End Local File Attachment Section.
 * Designed directly after the user's reference (Image 2):
 * - Clean dashed dropzone container with animated arrow up button
 * - Supports any document, PDF, video, image, archive
 * - Visual preview cards with file type icon, name, formatted size, and remove button
 * - Tap to open with Android FileProvider
 */
@Composable
fun FileAttachmentSection(
  attachments: List<AttachmentItem>,
  onAddAttachmentUri: (Uri) -> Unit,
  onRemoveAttachment: (AttachmentItem) -> Unit,
  modifier: Modifier = Modifier,
  title: String = "Berkas & Lampiran",
  subtitle: String = "Simpan berkas penunjang, materi, atau tugas di perangkat lokal",
  canAttach: Boolean = true
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current

  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenMultipleDocuments()
  ) { uris: List<Uri> ->
    if (uris.isNotEmpty()) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      uris.forEach { uri ->
        onAddAttachmentUri(uri)
      }
    }
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = BrandCharcoal
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = BrandSecondary
        )
      }
      if (attachments.isNotEmpty()) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = BrandOliveBg
        ) {
          Text(
            text = "${attachments.size} file",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = BrandOlive,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
          )
        }
      }
    }

    // 1. Dashed Dropzone Card (matching Image 2)
    if (canAttach) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White)
          .drawBehind {
            val stroke = Stroke(
              width = 1.5.dp.toPx(),
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f)
            )
            drawRoundRect(
              color = BrandBorder,
              style = stroke,
              cornerRadius = CornerRadius(16.dp.toPx())
            )
          }
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            filePickerLauncher.launch(
              arrayOf(
                "application/pdf",
                "image/*",
                "video/*",
                "application/zip",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "text/plain",
                "*/*"
              )
            )
          }
          .padding(vertical = 18.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          // Circular upload icon button with gradient
          Surface(
            modifier = Modifier.size(54.dp),
            shape = CircleShape,
            color = Color(0xFFEBF5FF),
            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Upload",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(24.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Ketuk untuk Memilih Berkas",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal
          )
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = "Mendukung PDF, DOCX, XLSX, MP4, Gambar, ZIP • Disimpan lokal",
            fontSize = 11.sp,
            color = BrandSecondary,
            textAlign = TextAlign.Center
          )
        }
      }
    }

    // 2. Attached Files Preview Cards List
    if (attachments.isNotEmpty()) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        attachments.forEach { item ->
          val category = AttachmentFileCategory.fromMimeOrName(item.mimeType, item.fileName)
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                AttachmentStorageManager.openAttachmentInExternalApp(context, item)
              },
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BrandBorder)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // File type icon
              Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = category.color.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, category.color.copy(alpha = 0.25f))
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              // File Metadata
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.fileName,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandCharcoal,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = BrandCanvas
                  ) {
                    Text(
                      text = category.label,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = BrandSecondary,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                  Text(
                    text = "• ${item.fileSizeFormatted} • Ketuk untuk buka",
                    fontSize = 10.5.sp,
                    color = BrandSecondary
                  )
                }
              }

              // Remove button
              IconButton(
                onClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  onRemoveAttachment(item)
                },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Hapus",
                  tint = BrandSecondary.copy(alpha = 0.6f),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
