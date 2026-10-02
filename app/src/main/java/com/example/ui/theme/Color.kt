package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Apple Human Interface Guidelines (HIG) System Palette
val AppleSystemBlue = Color(0xFF007AFF)
val AppleSystemGreen = Color(0xFF34C759)
val AppleSystemIndigo = Color(0xFF5856D6)
val AppleSystemOrange = Color(0xFFFF9500)
val AppleSystemPink = Color(0xFFFF2D55)
val AppleSystemPurple = Color(0xFFAF52DE)
val AppleSystemRed = Color(0xFFFF3B30)
val AppleSystemTeal = Color(0xFF5AC8FA)
val AppleSystemYellow = Color(0xFFFFCC00)

// Apple System Grays
val AppleSystemGray = Color(0xFF8E8E93)
val AppleSystemGray2 = Color(0xFFAEAEB2)
val AppleSystemGray3 = Color(0xFFC7C7CC)
val AppleSystemGray4 = Color(0xFFD1D1D6)
val AppleSystemGray5 = Color(0xFFE5E5EA)
val AppleSystemGray6 = Color(0xFFF2F2F7)

// Apple Semantic Backgrounds
val AppleGroupedBackground = Color(0xFFF2F2F7) // Standard iOS Inset Grouped Table View Background
val AppleCardBackground = Color(0xFFFFFFFF)
val AppleSeparator = Color(0x333C3C43) // 20% opacity separator

// WII To-Do Refined Apple HIG Tokens
val BrandCharcoal = Color(0xFF1C1C1E)
val BrandSecondary = Color(0xFF8E8E93)
val BrandBorder = Color(0xFFE5E5EA)
val BrandBorderLight = Color(0xFFF2F2F7)
val BrandCanvas = Color(0xFFF2F2F7)
val BrandCard = Color(0xFFFFFFFF)
val BrandTerracotta = Color(0xFFFF3B30)
val BrandTerracottaBg = Color(0xFFFFEBEA)
val BrandOlive = Color(0xFF34C759)
val BrandOliveBg = Color(0xFFEBF9EE)
val BrandPillBg = Color(0xFFE5E5EA)
val BrandTagBg = Color(0xFFF2F2F7)
val BrandCheckboxBorder = Color(0xFFC7C7CC)
val BrandDotNeutral = Color(0xFF8E8E93)
val BrandAvatarBg = Color(0xFFE5E5EA)
val BrandAvatarBorder = Color(0xFFD1D1D6)
val BrandOliveBorder = Color(0xFFD1F2D6)

// Backward-compatible mappings
val PrimaryBackground = BrandCanvas
val WarmSurface = BrandCanvas
val WarmSurfaceDim = Color(0xFFDBDAD8)
val WarmSurfaceBright = BrandCanvas
val ContentSurface = BrandCard
val ElevatedSurface = BrandCard
val WarmSurfaceContainerLowest = BrandCard
val WarmSurfaceContainerLow = BrandTagBg
val WarmSurfaceContainer = BrandPillBg
val WarmSurfaceContainerHigh = BrandBorderLight
val WarmSurfaceContainerHighest = BrandBorder
val SecondarySurface = BrandTagBg

val PrimaryText = BrandCharcoal
val TextOnSurface = BrandCharcoal
val CharcoalPrimary = BrandCharcoal
val CharcoalPrimaryContainer = BrandCharcoal
val CharcoalOnPrimary = Color.White
val CharcoalOnPrimaryContainer = Color.White

val SecondaryText = BrandSecondary
val TextOnSurfaceVariant = BrandSecondary
val MutedText = BrandSecondary
val TextMuted = BrandSecondary

val PrimaryAccent = BrandCharcoal
val OliveSecondary = BrandOlive
val OliveSecondaryContainer = BrandOliveBg
val OliveOnSecondary = Color.White
val OliveOnSecondaryContainer = BrandOlive

val SecondaryAccent = BrandTerracotta
val TerracottaAccent = BrandTerracotta
val TerracottaFixed = BrandTerracottaBg
val TerracottaContainer = BrandTerracottaBg
val TerracottaOnFixed = BrandTerracotta

val HighPriorityDot = BrandTerracotta
val MediumPriorityDot = BrandOlive
val NormalPriorityDot = BrandDotNeutral

val WarmBorderSubtle = BrandBorder
val WarmBorderMedium = BrandBorder
val OutlineBorder = BrandSecondary
val OutlineVariantBorder = BrandBorderLight
val ListDivider = BrandBorderLight
