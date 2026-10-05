package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandAvatarBg
import com.example.ui.theme.BrandAvatarBorder
import com.example.ui.theme.BrandCharcoal
import com.example.util.AvatarStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun UserAvatarDisplay(
  avatarUri: String?,
  presetId: String?,
  colorHex: Long?,
  initials: String,
  modifier: Modifier = Modifier,
  borderWidth: Dp = 1.5.dp,
  borderColor: Color = BrandAvatarBorder,
  fontSize: TextUnit = 18.sp
) {
  var loadedBitmap by remember(avatarUri) { mutableStateOf<Bitmap?>(null) }

  LaunchedEffect(avatarUri) {
    if (!avatarUri.isNullOrBlank()) {
      withContext(Dispatchers.IO) {
        loadedBitmap = AvatarStorageManager.loadBitmapFromPath(avatarUri)
      }
    } else {
      loadedBitmap = null
    }
  }

  val pixelPreset = remember(presetId) {
    PIXEL_AVATAR_LIST.find { it.id == presetId }
  }

  Surface(
    modifier = modifier,
    shape = CircleShape,
    color = colorHex?.let { Color(it) } ?: BrandAvatarBg,
    border = BorderStroke(borderWidth, borderColor)
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      when {
        loadedBitmap != null -> {
          Image(
            bitmap = loadedBitmap!!.asImageBitmap(),
            contentDescription = "Foto Profil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape)
          )
        }
        pixelPreset != null -> {
          PixelArtAvatar(
            avatar = pixelPreset,
            modifier = Modifier.fillMaxSize(),
            backgroundColor = colorHex?.let { Color(it) } ?: Color(pixelPreset.defaultBgColorHex)
          )
        }
        else -> {
          Text(
            text = initials,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = if (colorHex != null) Color.White else BrandCharcoal
          )
        }
      }
    }
  }
}
