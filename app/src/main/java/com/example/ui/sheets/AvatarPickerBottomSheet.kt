package com.example.ui.sheets

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PIXEL_AVATAR_LIST
import com.example.ui.components.PixelArtAvatar
import com.example.ui.components.UserAvatarDisplay
import com.example.ui.theme.AppleSystemBlue
import com.example.ui.theme.AppleSystemGreen
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandOliveBg
import com.example.ui.theme.BrandOliveBorder
import com.example.ui.theme.BrandPillBg
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta
import com.example.util.AvatarStorageManager
import kotlinx.coroutines.launch

val AVATAR_PALETTE = listOf(
  0xFF0F172A, // Obsidian Navy
  0xFF064E3B, // Emerald Forest
  0xFF2E1065, // Cosmic Violet
  0xFF1E293B, // Slate Dark
  0xFF1C1917, // Charcoal Warm
  0xFF1E1B4B, // Deep Indigo
  0xFF0B1329, // Midnight Shadow
  0xFF831843  // Crimson Rose
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarPickerBottomSheet(
  currentAvatarUri: String?,
  currentPresetId: String?,
  currentColorHex: Long,
  initials: String,
  onDismiss: () -> Unit,
  onSaveAvatar: (uri: String?, presetId: String?, colorHex: Long) -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val haptic = LocalHapticFeedback.current
  val scrollState = rememberScrollState()

  var selectedUri by remember { mutableStateOf(currentAvatarUri) }
  var selectedPresetId by remember { mutableStateOf(currentPresetId ?: "pixel_hacker") }
  var selectedColorHex by remember { mutableStateOf(currentColorHex) }
  var isSavingPhoto by remember { mutableStateOf(false) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      isSavingPhoto = true
      coroutineScope.launch {
        val savedPath = AvatarStorageManager.saveImageToInternalStorage(context, uri)
        isSavingPhoto = false
        if (savedPath != null) {
          selectedUri = savedPath
          selectedPresetId = ""
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = Color.White,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 10.dp, bottom = 6.dp)
          .width(36.dp)
          .height(4.dp)
          .background(BrandBorder, RoundedCornerShape(2.dp))
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "FOTO PROFIL & AVATAR 8-BIT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = BrandOlive,
            letterSpacing = 0.8.sp
          )
          Text(
            text = "Kustomisasi Avatar Anda",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal
          )
        }

        // Live Real-Time Preview
        Box(
          modifier = Modifier.size(56.dp),
          contentAlignment = Alignment.Center
        ) {
          if (isSavingPhoto) {
            CircularProgressIndicator(
              modifier = Modifier.size(28.dp),
              color = BrandOlive,
              strokeWidth = 2.5.dp
            )
          } else {
            UserAvatarDisplay(
              avatarUri = selectedUri,
              presetId = if (selectedUri.isNullOrBlank()) selectedPresetId else null,
              colorHex = selectedColorHex,
              initials = initials,
              modifier = Modifier.size(56.dp),
              borderWidth = 2.dp,
              borderColor = Color.White,
              fontSize = 20.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section 1: Gallery / Device Upload
      Text(
        text = "UNGGAH FOTO DARI PERANGKAT",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = BrandSecondary,
        letterSpacing = 0.6.sp
      )
      Spacer(modifier = Modifier.height(8.dp))

      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            photoPickerLauncher.launch("image/*")
          },
        shape = RoundedCornerShape(12.dp),
        color = BrandPillBg,
        border = BorderStroke(1.dp, BrandBorderLight)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (!selectedUri.isNullOrBlank()) {
            UserAvatarDisplay(
              avatarUri = selectedUri,
              presetId = null,
              colorHex = null,
              initials = initials,
              modifier = Modifier.size(38.dp),
              borderWidth = 1.dp,
              borderColor = AppleSystemGreen
            )
          } else {
            Surface(
              modifier = Modifier.size(38.dp),
              shape = CircleShape,
              color = Color.White,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.PhotoLibrary,
                  contentDescription = null,
                  tint = BrandCharcoal,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (!selectedUri.isNullOrBlank()) "Ganti Foto Galeri Terpilih" else "Pilih Foto dari Galeri / Kamera",
              fontSize = 13.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = BrandCharcoal
            )
            Text(
              text = if (!selectedUri.isNullOrBlank()) "Foto tersimpan di memori lokal internal" else "Format JPG / PNG berkualitas tinggi",
              fontSize = 11.sp,
              color = BrandSecondary
            )
          }

          if (!selectedUri.isNullOrBlank()) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Foto Terpilih",
              tint = AppleSystemGreen,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Option to clear uploaded photo and revert to 8-bit avatar
      if (!selectedUri.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              AvatarStorageManager.deleteInternalAvatar(context)
              selectedUri = null
              selectedPresetId = "pixel_hacker"
            }
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = null,
            tint = BrandTerracotta,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Hapus Foto Galeri & Gunakan Template 8-Bit",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandTerracotta
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))
      HorizontalDivider(color = BrandBorderLight, thickness = 1.dp)
      Spacer(modifier = Modifier.height(14.dp))

      // Section 2: 8-Bit Pixel Art Avatar Archetypes
      Text(
        text = "TEMPLATE AVATAR 8-BIT RETRO",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = BrandSecondary,
        letterSpacing = 0.6.sp
      )
      Spacer(modifier = Modifier.height(10.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(PIXEL_AVATAR_LIST) { avatar ->
          val isSelected = selectedUri.isNullOrBlank() && selectedPresetId == avatar.id
          Surface(
            modifier = Modifier
              .width(76.dp)
              .clip(RoundedCornerShape(14.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                selectedUri = null
                selectedPresetId = avatar.id
                selectedColorHex = avatar.defaultBgColorHex
              },
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) BrandOliveBg else Color.White,
            border = BorderStroke(
              if (isSelected) 2.dp else 1.dp,
              if (isSelected) BrandOlive else BrandBorder
            )
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              PixelArtAvatar(
                avatar = avatar,
                modifier = Modifier.size(44.dp),
                backgroundColor = Color(avatar.defaultBgColorHex)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = avatar.name,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) BrandOlive else BrandCharcoal,
                maxLines = 1
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section 3: Background Monogram Palette
      Text(
        text = "WARNA LATAR AVATAR",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = BrandSecondary,
        letterSpacing = 0.6.sp
      )
      Spacer(modifier = Modifier.height(10.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(AVATAR_PALETTE) { hex ->
          val isSelected = selectedColorHex == hex
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(hex))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                selectedColorHex = hex
              }
              .border(
                width = if (isSelected) 2.5.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected Color",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, BrandBorder),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
        ) {
          Text(
            text = "Batal",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandCharcoal
          )
        }

        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onSaveAvatar(selectedUri, selectedPresetId, selectedColorHex)
            onDismiss()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
        ) {
          Text(
            text = "Terapkan Avatar",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
