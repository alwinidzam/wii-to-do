package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MilestoneTierLevel
import com.example.data.model.ShareCardAspectRatio
import com.example.data.model.ShareCardConfig
import com.example.data.model.ShareCardTheme
import com.example.data.model.UserProfile
import com.example.ui.components.WiiBrandLogo
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandOliveBg
import com.example.ui.theme.BrandPillBg
import com.example.ui.theme.BrandSecondary

@Composable
fun SocialShareStudioScreen(
  userProfile: UserProfile,
  initialConfig: ShareCardConfig,
  onConfigChange: (ShareCardConfig) -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
  onShowSnackbar: (String) -> Unit = {}
) {
  val haptic = LocalHapticFeedback.current
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  var config by remember { mutableStateOf(initialConfig) }
  val currentTier = MilestoneTierLevel.fromXp(userProfile.totalXpAllTime)

  fun updateAndNotify(newConfig: ShareCardConfig) {
    config = newConfig
    onConfigChange(newConfig)
  }

  fun shareToExternal() {
    val shareText = """
      🏛️ WII To-Do • Focus Milestone Achieved!
      
      Tier: ${currentTier.title} (${currentTier.badgeCode})
      Total XP: ${userProfile.totalXpAllTime} XP • Level ${userProfile.level}
      Streak: ${userProfile.streakDays} Days unbroken
      Focus Logged: ${userProfile.focusHoursLogged}
      Motto: “${config.customQuote}”
      
      Crafted with WII To-Do Minimalist Architecture.
    """.trimIndent()

    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, shareText)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Achievement Card")
    context.startActivity(shareIntent)
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
      .padding(horizontal = 20.dp),
    contentPadding = PaddingValues(
      top = topInset + 12.dp,
      bottom = 120.dp
    ),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Top Bar
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onBackClick()
            },
          shape = RoundedCornerShape(10.dp),
          color = Color.White,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = BrandCharcoal,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "SHARE STUDIO",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "Aesthetic Milestone Card",
            fontSize = 11.sp,
            color = BrandSecondary
          )
        }

        Surface(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              shareToExternal()
            },
          shape = RoundedCornerShape(10.dp),
          color = BrandCharcoal
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
              tint = Color.White,
              modifier = Modifier.size(17.dp)
            )
          }
        }
      }
    }

    // 2. Format / Aspect Ratio Selector (Story 9:16 vs Square 1:1)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ShareCardAspectRatio.entries.forEach { ratio ->
          val isSelected = (config.aspectRatio == ratio)
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                updateAndNotify(config.copy(aspectRatio = ratio))
              },
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) BrandCharcoal else Color.White,
            border = if (!isSelected) BorderStroke(1.dp, BrandBorder) else null
          ) {
            Box(
              modifier = Modifier.padding(vertical = 9.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = ratio.label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else BrandSecondary
              )
            }
          }
        }
      }
    }

    // 3. Live Card Preview
    item {
      val cardBg = Color(config.theme.bgHex)
      val cardSurface = Color(config.theme.surfaceHex)
      val textPrimary = Color(config.theme.textPrimaryHex)
      val textSecondary = Color(config.theme.textSecondaryHex)
      val accent = Color(config.theme.accentHex)
      val cardBorder = Color(config.theme.borderHex)

      val isStory = config.aspectRatio == ShareCardAspectRatio.STORY_9_16

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Surface(
          modifier = Modifier
            .fillMaxWidth(if (isStory) 0.88f else 1.0f)
            .shadow(
              elevation = 6.dp,
              shape = RoundedCornerShape(20.dp),
              ambientColor = Color(0x14000000),
              spotColor = Color(0x10000000)
            ),
          shape = RoundedCornerShape(20.dp),
          color = cardBg,
          border = BorderStroke(1.5.dp, cardBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(if (isStory) 22.dp else 18.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            // Card Header
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                WiiBrandLogo(size = 24.dp)
                Text(
                  text = "WII TO-DO",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = textPrimary,
                  letterSpacing = 1.sp
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = cardSurface,
                border = BorderStroke(1.dp, cardBorder)
              ) {
                Text(
                  text = "ARCHITECTURAL MILESTONE",
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = accent,
                  letterSpacing = 0.8.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(if (isStory) 20.dp else 12.dp))

            // User Identity & Current Tier
            Column {
              Text(
                text = userProfile.name.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textSecondary,
                letterSpacing = 1.sp
              )
              Text(
                text = userProfile.program,
                fontSize = 10.sp,
                color = textSecondary.copy(alpha = 0.8f)
              )

              Spacer(modifier = Modifier.height(10.dp))

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = accent.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))
              ) {
                Text(
                  text = currentTier.badgeCode,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = accent,
                  letterSpacing = 1.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = currentTier.title,
                fontSize = if (isStory) 24.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                letterSpacing = (-0.02).sp
              )
              Text(
                text = currentTier.subtitle,
                fontSize = 12.sp,
                color = textSecondary
              )
            }

            Spacer(modifier = Modifier.height(if (isStory) 16.dp else 10.dp))

            // Metrics Grid
            if (config.showStreak || config.showFocusHours || config.showTasksDone) {
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = cardSurface,
                border = BorderStroke(1.dp, cardBorder)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                  horizontalArrangement = Arrangement.SpaceAround
                ) {
                  if (config.showStreak) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = "STREAK", fontSize = 9.sp, color = textSecondary, fontWeight = FontWeight.Bold)
                      Text(
                        text = "${userProfile.streakDays} Days 🔥",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (theme == ShareCardTheme.DARK_OBSIDIAN) Color(0xFFFDE68A) else Color(0xFFD97706)
                      )
                    }
                  }
                  if (config.showFocusHours) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = "FOCUS TIME", fontSize = 9.sp, color = textSecondary, fontWeight = FontWeight.Bold)
                      Text(text = userProfile.focusHoursLogged, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    }
                  }
                  if (config.showTasksDone) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = "TASKS DONE", fontSize = 9.sp, color = textSecondary, fontWeight = FontWeight.Bold)
                      Text(text = "${userProfile.tasksDoneCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    }
                  }
                }
              }
            }

            // XP Progress Bar
            if (config.showXpProgress) {
              Spacer(modifier = Modifier.height(10.dp))
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Total Experience",
                    fontSize = 10.sp,
                    color = textSecondary,
                    fontWeight = FontWeight.Medium
                  )
                  Text(
                    text = "${userProfile.totalXpAllTime} XP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                val tierSpan = (currentTier.maxXp - currentTier.minXp).toFloat().coerceAtLeast(1f)
                val progressInTier = (userProfile.totalXpAllTime - currentTier.minXp).toFloat().coerceIn(0f, tierSpan)
                val fraction = (progressInTier / tierSpan).coerceIn(0f, 1f)

                LinearProgressIndicator(
                  progress = { fraction },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(2.5.dp)),
                  color = accent,
                  trackColor = cardBorder
                )
              }
            }

            // Quote Box
            if (config.showQuote && config.customQuote.isNotBlank()) {
              Spacer(modifier = Modifier.height(if (isStory) 16.dp else 10.dp))
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = cardSurface.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, cardBorder)
              ) {
                Text(
                  text = "“${config.customQuote}”",
                  fontSize = if (isStory) 12.sp else 11.sp,
                  color = textPrimary,
                  fontStyle = FontStyle.Italic,
                  modifier = Modifier.padding(10.dp),
                  lineHeight = 15.sp,
                  textAlign = TextAlign.Center
                )
              }
            }

            Spacer(modifier = Modifier.height(if (isStory) 20.dp else 12.dp))

            // Footer Stamp
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "wii-todo.app • personal mastery",
                fontSize = 9.sp,
                color = textSecondary.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "SPEC://ARCH-V2",
                fontSize = 9.sp,
                color = textSecondary.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }

    // 4. Color Palette Theme Picker
    item {
      Column {
        Text(
          text = "ARCHITECTURAL COLOR THEME",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ShareCardTheme.entries.forEach { theme ->
            val isSelected = (config.theme == theme)
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  updateAndNotify(config.copy(theme = theme))
                },
              shape = RoundedCornerShape(12.dp),
              color = Color(theme.bgHex),
              border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) BrandCharcoal else Color(theme.borderHex))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(14.dp)
                    .background(Color(theme.accentHex), CircleShape)
                )
                Text(
                  text = theme.label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = Color(theme.textPrimaryHex)
                )
              }
            }
          }
        }
      }
    }

    // 5. Card Content Toggles
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "CARD ELEMENT TOGGLES",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BrandSecondary,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              Pair("XP Bar", config.showXpProgress),
              Pair("Streak", config.showStreak),
              Pair("Focus Time", config.showFocusHours),
              Pair("Tasks", config.showTasksDone)
            ).forEach { (label, active) ->
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    when (label) {
                      "XP Bar" -> updateAndNotify(config.copy(showXpProgress = !active))
                      "Streak" -> updateAndNotify(config.copy(showStreak = !active))
                      "Focus Time" -> updateAndNotify(config.copy(showFocusHours = !active))
                      "Tasks" -> updateAndNotify(config.copy(showTasksDone = !active))
                    }
                  },
                shape = RoundedCornerShape(8.dp),
                color = if (active) BrandCharcoal else BrandPillBg
              ) {
                Text(
                  text = if (active) "✓ $label" else label,
                  fontSize = 11.sp,
                  fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                  color = if (active) Color.White else BrandSecondary,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    }

    // 6. Editable Focus Motto
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "CUSTOM FOCUS MOTTO",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BrandSecondary,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = config.customQuote,
            onValueChange = { updateAndNotify(config.copy(customQuote = it)) },
            placeholder = { Text("Enter your personal focus motto...", fontSize = 12.sp, color = BrandSecondary) },
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent,
              focusedTextColor = BrandCharcoal,
              unfocusedTextColor = BrandCharcoal
            ),
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
          )
        }
      }
    }

    // 7. Action Hub (Share & Copy)
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            shareToExternal()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Share to Socials (IG Story / WA)",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }

        OutlinedButton(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            val summary = "🏛️ WII To-Do | ${currentTier.title} (${currentTier.badgeCode}) • ${userProfile.totalXpAllTime} XP • ${userProfile.streakDays} Days Streak • “${config.customQuote}”"
            clipboardManager.setText(AnnotatedString(summary))
            onShowSnackbar("Achievement summary copied to clipboard!")
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = null,
              tint = BrandCharcoal,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Copy Text Summary",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = BrandCharcoal
            )
          }
        }
      }
    }
  }
}
