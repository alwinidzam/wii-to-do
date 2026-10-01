package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = WarmSurfaceContainerLowest,
    onPrimary = CharcoalPrimary,
    primaryContainer = CharcoalPrimaryContainer,
    onPrimaryContainer = WarmSurfaceContainerLow,
    secondary = OliveSecondaryContainer,
    onSecondary = OliveOnSecondaryContainer,
    secondaryContainer = OliveSecondary,
    onSecondaryContainer = OliveSecondaryContainer,
    background = Color(0xFF121212),
    onBackground = WarmSurface,
    surface = Color(0xFF1E1E1E),
    onSurface = WarmSurface,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = WarmSurfaceDim,
    outline = OutlineBorder,
    outlineVariant = Color(0xFF3E3E3E),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = CharcoalPrimary,
    onPrimary = CharcoalOnPrimary,
    primaryContainer = CharcoalPrimaryContainer,
    onPrimaryContainer = CharcoalOnPrimaryContainer,
    secondary = OliveSecondary,
    onSecondary = OliveOnSecondary,
    secondaryContainer = OliveSecondaryContainer,
    onSecondaryContainer = OliveOnSecondaryContainer,
    tertiary = TerracottaAccent,
    onTertiary = Color.White,
    tertiaryContainer = TerracottaFixed,
    onTertiaryContainer = TerracottaOnFixed,
    background = WarmSurface,
    onBackground = TextOnSurface,
    surface = WarmSurface,
    onSurface = TextOnSurface,
    surfaceVariant = WarmSurfaceContainerHighest,
    onSurfaceVariant = TextOnSurfaceVariant,
    outline = OutlineBorder,
    outlineVariant = OutlineVariantBorder,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
