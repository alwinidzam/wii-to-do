package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Official architectural WII To-Do monogram brand insignia.
 * Overlapping rounded-capsule monogram with modern charcoal depth.
 */
@Composable
fun WiiBrandLogo(
  modifier: Modifier = Modifier,
  size: Dp = 26.dp,
  primaryColor: Color? = null,
  contentDescription: String = "WII To-Do Monogram"
) {
  Image(
    painter = painterResource(id = R.drawable.wii_logo),
    contentDescription = contentDescription,
    modifier = modifier.size(size),
    contentScale = ContentScale.Fit,
    colorFilter = if (primaryColor != null && primaryColor != Color(0xFF060607)) ColorFilter.tint(primaryColor) else null
  )
}

