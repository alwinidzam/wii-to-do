package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
// Apple Human Interface Guidelines (HIG) Standard Typography Scale
val AppleLargeTitle = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.Bold,
  fontSize = 34.sp,
  lineHeight = 41.sp,
  letterSpacing = (-0.5).sp
)

val AppleTitle1 = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.Bold,
  fontSize = 28.sp,
  lineHeight = 34.sp,
  letterSpacing = (-0.3).sp
)

val AppleTitle2 = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.Bold,
  fontSize = 22.sp,
  lineHeight = 28.sp,
  letterSpacing = (-0.2).sp
)

val AppleTitle3 = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.SemiBold,
  fontSize = 20.sp,
  lineHeight = 25.sp,
  letterSpacing = (-0.1).sp
)

val AppleHeadline = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.SemiBold,
  fontSize = 17.sp,
  lineHeight = 22.sp,
  letterSpacing = (-0.2).sp
)

val AppleBody = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.Normal,
  fontSize = 17.sp,
  lineHeight = 22.sp,
  letterSpacing = (-0.1).sp
)

val AppleCallout = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.Normal,
  fontSize = 16.sp,
  lineHeight = 21.sp,
  letterSpacing = (-0.1).sp
)

val AppleSubheadline = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.Normal,
  fontSize = 15.sp,
  lineHeight = 20.sp,
  letterSpacing = (-0.05).sp
)

val AppleFootnote = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.Normal,
  fontSize = 13.sp,
  lineHeight = 18.sp,
  letterSpacing = 0.sp
)

val AppleCaption1 = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.Medium,
  fontSize = 12.sp,
  lineHeight = 16.sp,
  letterSpacing = 0.sp
)

val AppleCaption2 = TextStyle(
  fontFamily = FontFamily.Default,
  fontWeight = FontWeight.Normal,
  fontSize = 11.sp,
  lineHeight = 13.sp,
  letterSpacing = 0.sp
)

val Typography =
  Typography(
    displayLarge = AppleLargeTitle,
    headlineLarge = AppleTitle1,
    headlineMedium = AppleTitle2,
    headlineSmall = AppleTitle3,
    titleMedium = AppleHeadline,
    bodyLarge = AppleBody,
    bodyMedium = AppleCallout,
    bodySmall = AppleSubheadline,
    labelLarge = AppleFootnote,
    labelMedium = AppleCaption1,
    labelSmall = AppleCaption2
  )
