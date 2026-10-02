package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Official Wii To Do Verified Badge Component.
 * Displays the authentic verified rosette checkmark badge from `badge verified.svg`.
 */
@Composable
fun VerifiedBadgeIcon(
  modifier: Modifier = Modifier,
  size: Dp = 16.dp
) {
  Image(
    painter = painterResource(id = R.drawable.ic_badge_verified),
    contentDescription = "Verified Badge",
    modifier = modifier.size(size)
  )
}
