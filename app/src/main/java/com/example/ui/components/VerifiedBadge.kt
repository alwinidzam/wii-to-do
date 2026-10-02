package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.VerifiedBadgeTier

/**
 * Official Wii To Do Multi-Tier Verified Badge Component.
 * Supports:
 * - GOLD: Developer / Founder / System Authority (khusus akun resmi alwinizam0405@gmail.com)
 * - BLUE: Productivity Achiever (Level 5+ via XP dan tugas selesai)
 * - GREEN: Mahasiswa Terverifikasi (Domain kampus & semester aktif)
 */
@Composable
fun VerifiedBadgeIcon(
  modifier: Modifier = Modifier,
  tier: VerifiedBadgeTier = VerifiedBadgeTier.BLUE,
  size: Dp = 16.dp
) {
  if (tier == VerifiedBadgeTier.NONE) return

  val drawableRes = when (tier) {
    VerifiedBadgeTier.GOLD -> R.drawable.ic_badge_verified_gold
    VerifiedBadgeTier.GREEN -> R.drawable.ic_badge_verified_green
    VerifiedBadgeTier.BLUE,
    VerifiedBadgeTier.NONE -> R.drawable.ic_badge_verified_blue
  }

  Image(
    painter = painterResource(id = drawableRes),
    contentDescription = "Verified ${tier.title}",
    modifier = modifier.size(size)
  )
}

