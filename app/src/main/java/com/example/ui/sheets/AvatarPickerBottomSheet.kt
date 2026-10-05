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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleSystemBlue
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandOliveBg
import com.example.ui.theme.BrandOliveBorder
import com.example.ui.theme.BrandPillBg
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta

data class AvatarPreset(
  val id: String,
  val emoji: String,
  val label: String,
  val colorHex: Long
)

val PRESET_AVATARS = listOf(
  AvatarPreset("architect_1", "🏛️", "Architect", 0xFF1E293B),
  AvatarPreset("drafter_2", "📐", "Drafter", 0xFF0F766E),
  AvatarPreset("flow_3", "⚡", "Flow", 0xFFD97706),
  AvatarPreset("scholar_4", "🌿", "Scholar", 0xFF059669),
  AvatarPreset("researcher_5", "🔬", "Research", 0xFF2563EB),
  AvatarPreset("master_6", "💎", "Master", 0xFF7C3AED)
)

val MONOGRAM_PALETTE = listOf(
  0xFF1E293B, // Charcoal Navy
  0xFF0F766E, // Deep Teal
  0xFF2563EB, // Electric Cobalt
  0xFF059669, // Emerald Green
  0xFFD97706, // Warm Amber
  0xFF7C3AED, // Royal Violet
  0xFFC026D3, // Magenta
  0xFFE11D48  // Crimson Rose
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
  val haptic = LocalHapticFeedback.current
  var selectedUri by remember { mutableStateOf(currentAvatarUri) }
  var selectedPresetId by remember { mutableStateOf(currentPresetId ?: "architect_1") }
  var selectedColorHex by remember { mutableStateOf(currentColorHex) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedUri = uri.toString()
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
            text = "FOTO PROFIL & AVATAR",
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

        // Live Preview Avatar
        Surface(
          modifier = Modifier.size(54.dp),
          shape = CircleShape,
          color = Color(selectedColorHex),
          border = BorderStroke(2.dp, Color.White),
          shadowElevation = 3.dp
        ) {
          Box(contentAlignment = Alignment.Center) {
            val preset = PRESET_AVATARS.find { it.id == selectedPresetId }
            Text(
              text = if (selectedUri != null) "📷" else preset?.emoji ?: initials,
              fontSize = 22.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

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
          Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, BrandBorderLight)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                tint = AppleSystemBlue,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (selectedUri != null) "Ganti Foto Galeri Terpilih" else "Pilih Foto dari Galeri",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = BrandCharcoal
            )
            Text(
              text = if (selectedUri != null) "Foto tersimpan di perangkat" else "Format JPG, PNG, atau WEBP",
              fontSize = 11.sp,
              color = BrandSecondary
            )
          }
          if (selectedUri != null) {
            Surface(
              shape = CircleShape,
              color = BrandOliveBg,
              border = BorderStroke(1.dp, BrandOliveBorder)
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = BrandOlive,
                modifier = Modifier.size(16.dp).padding(2.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))
      HorizontalDivider(color = BrandBorderLight, thickness = 1.dp)
      Spacer(modifier = Modifier.height(18.dp))

      // Section 2: Curated Architectural Presets
      Text(
        text = "PRESET ARSITEK MINIMALIS",
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
        items(PRESET_AVATARS) { preset ->
          val isSelected = selectedUri == null && selectedPresetId == preset.id
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                selectedUri = null
                selectedPresetId = preset.id
                selectedColorHex = preset.colorHex
              },
            shape = RoundedCornerShape(14.dp),
            color = if (isSelected) BrandPillBg else Color.White,
            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) BrandCharcoal else BrandBorder)
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(text = preset.emoji, fontSize = 24.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = preset.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = BrandCharcoal
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))
      HorizontalDivider(color = BrandBorderLight, thickness = 1.dp)
      Spacer(modifier = Modifier.height(18.dp))

      // Section 3: Monogram Palette Customizer
      Text(
        text = "WARNA MONOGRAM INISIAL",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = BrandSecondary,
        letterSpacing = 0.6.sp
      )
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        MONOGRAM_PALETTE.forEach { colorVal ->
          val isSelected = selectedColorHex == colorVal
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(colorVal))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                selectedColorHex = colorVal
              }
              .border(
                width = if (isSelected) 2.5.dp else 0.dp,
                color = if (isSelected) BrandCharcoal else Color.Transparent,
                shape = CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      // Option to reset photo
      if (selectedUri != null) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              selectedUri = null
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = null,
            tint = BrandTerracotta,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Hapus Foto Galeri & Gunakan Preset",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandTerracotta
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Bottom Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, BrandBorder),
          modifier = Modifier.weight(1f).height(48.dp)
        ) {
          Text("Batal", color = BrandCharcoal, fontWeight = FontWeight.SemiBold)
        }

        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onSaveAvatar(selectedUri, selectedPresetId, selectedColorHex)
            onDismiss()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal),
          modifier = Modifier.weight(1f).height(48.dp)
        ) {
          Text("Terapkan Avatar", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
