package com.example.ui.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.components.LinearBorderHairline
import com.example.ui.components.LinearDoneGreen
import com.example.ui.components.LinearUrgentRed
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta
import com.example.ui.theme.BrandTerracottaBg
import com.example.ui.theme.HapticEngine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * High-End Apple HIG / Linear-style Deleted Tasks & Restore Trash Sheet.
 * Provides complete peace of mind with instant task restoration ("Pulihkan Task")
 * and permanent purge options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletedTasksBottomSheet(
  deletedTasks: List<TaskItem>,
  onRestoreTask: (String) -> Unit,
  onPermanentlyDelete: (String) -> Unit,
  onEmptyTrash: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val coroutineScope = rememberCoroutineScope()
  var showEmptyConfirm by remember { mutableStateOf(false) }

  val dismissSheet = {
    coroutineScope.launch {
      sheetState.hide()
      onDismiss()
    }
  }

  ModalBottomSheet(
    onDismissRequest = { dismissSheet() },
    sheetState = sheetState,
    containerColor = Color.White,
    scrimColor = Color(0x66000000),
    dragHandle = {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
              if (dragAmount > 20f) {
                dismissSheet()
              }
            }
          }
          .padding(top = 10.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .width(36.dp)
            .height(4.5.dp)
            .clip(RoundedCornerShape(2.25.dp))
            .background(Color(0xFFD1D5DB))
        )
      }
    },
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .imePadding()
        .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
    ) {
      // 1. Header Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = BrandTerracottaBg,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = BrandTerracotta,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Column {
            Text(
              text = "Riwayat Terhapus",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal
            )
            Text(
              text = "${deletedTasks.size} tugas di kotak sampah",
              fontSize = 11.5.sp,
              color = BrandSecondary
            )
          }
        }

        IconButton(
          onClick = {
            HapticEngine.selection(context, haptic)
            dismissSheet()
          },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Tutup",
            tint = BrandSecondary,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 2. Empty Confirmation or Content
      if (deletedTasks.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          horizontalAlignment = Alignment.CenterAlignmentLine(Alignment.CenterHorizontally),
          verticalArrangement = Arrangement.Center
        ) {
          Surface(
            shape = CircleShape,
            color = BrandCanvas,
            modifier = Modifier.size(56.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = BrandSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Kotak Sampah Kosong",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandCharcoal
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Tugas yang Anda hapus akan tersimpan di sini dan dapat dipulihkan kapan saja.",
            fontSize = 12.sp,
            color = BrandSecondary,
            modifier = Modifier.padding(horizontal = 24.dp)
          )
        }
      } else {
        // List of deleted items
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(deletedTasks, key = { it.id }) { task ->
            val dateStr = remember(task.deletedAt) {
              if (task.deletedAt != null) {
                SimpleDateFormat("d MMM, HH:mm", Locale.getDefault()).format(Date(task.deletedAt))
              } else "Baru saja"
            }

            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              color = Color.White,
              border = BorderStroke(1.dp, LinearBorderHairline)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = task.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandCharcoal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(3.dp))
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = task.category,
                      fontSize = 10.5.sp,
                      fontWeight = FontWeight.Medium,
                      color = BrandSecondary
                    )
                    Text(
                      text = "•",
                      fontSize = 10.sp,
                      color = BrandSecondary.copy(alpha = 0.5f)
                    )
                    Text(
                      text = "Dihapus $dateStr",
                      fontSize = 10.5.sp,
                      color = BrandSecondary
                    )
                  }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Actions: Restore & Permanent Delete
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  // Pulihkan Button
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LinearDoneGreen.copy(alpha = 0.12f),
                    border = BorderStroke(0.75.dp, LinearDoneGreen.copy(alpha = 0.3f)),
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .clickable {
                        HapticEngine.success(context, haptic)
                        onRestoreTask(task.id)
                      }
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Pulihkan",
                        tint = LinearDoneGreen,
                        modifier = Modifier.size(13.dp)
                      )
                      Text(
                        text = "Pulihkan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LinearDoneGreen
                      )
                    }
                  }

                  // Permanent Delete Button
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandCanvas,
                    modifier = Modifier
                      .size(28.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .clickable {
                        HapticEngine.warning(context, haptic)
                        onPermanentlyDelete(task.id)
                      }
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Hapus Permanen",
                        tint = LinearUrgentRed,
                        modifier = Modifier.size(15.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Empty Trash Action Button
        if (showEmptyConfirm) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = { showEmptyConfirm = false },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Batal", color = BrandSecondary, fontSize = 12.sp)
            }
            Button(
              onClick = {
                HapticEngine.heavy(context, haptic)
                showEmptyConfirm = false
                onEmptyTrash()
              },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = LinearUrgentRed),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Yakin, Hapus Semua", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        } else {
          OutlinedButton(
            onClick = {
              HapticEngine.selection(context, haptic)
              showEmptyConfirm = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, LinearUrgentRed.copy(alpha = 0.35f))
          ) {
            Icon(
              imageVector = Icons.Default.DeleteForever,
              contentDescription = null,
              tint = LinearUrgentRed,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Kosongkan Kotak Sampah",
              color = LinearUrgentRed,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
  }
}
