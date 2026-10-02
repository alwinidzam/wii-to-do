package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandSecondary

/**
 * Unified, Apple HIG / Linear-standard Top App Bar.
 * Ensures 100% mathematical consistency across all screens (Home, Schedule, Projects, Profile):
 * - Identical status bar top-padding calculation
 * - Identical horizontal padding (20dp)
 * - Identical logo size (32dp)
 * - Identical typography baseline and line heights
 * - Identical hairline bottom divider (0.75dp)
 * Eliminates any header jumpiness or layout shifting across tab switches.
 */
@Composable
fun UnifiedTopAppBar(
  modifier: Modifier = Modifier,
  title: String = "wii to do",
  subtitle: String? = null,
  showVerifiedBadge: Boolean = false,
  subtitleSuffix: String? = null,
  leadingLogoSize: Dp = 32.dp,
  customLeading: (@Composable () -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {}
) {
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = BrandCanvas,
    border = BorderStroke(0.75.dp, BrandBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          start = 20.dp,
          end = 20.dp,
          top = if (topInset > 0.dp) topInset + 8.dp else 12.dp,
          bottom = 12.dp
        ),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Leading: Brand Logo + Titles
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        if (customLeading != null) {
          customLeading()
        } else {
          WiiBrandLogo(size = leadingLogoSize)
        }

        Column(
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal,
            letterSpacing = (-0.02).sp,
            lineHeight = 20.sp
          )

          if (subtitle != null) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = BrandSecondary,
                letterSpacing = 0.2.sp,
                lineHeight = 14.sp
              )
              if (showVerifiedBadge) {
                VerifiedBadgeIcon(size = 13.5.dp)
              }
              if (subtitleSuffix != null) {
                Text(
                  text = subtitleSuffix,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = BrandSecondary,
                  letterSpacing = 0.2.sp,
                  lineHeight = 14.sp
                )
              }
            }
          }
        }
      }

      // Trailing Actions
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        actions()
      }
    }
  }
}
