package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.UnifiedTopAppBar
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.components.WiiBrandLogo
import com.example.ui.theme.HapticEngine
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LanguageSwitchPill
import com.example.ui.i18n.Translations
import com.example.ui.theme.BrandAvatarBg
import com.example.ui.theme.BrandAvatarBorder
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandOliveBg
import com.example.ui.theme.BrandOliveBorder
import com.example.ui.theme.BrandPillBg
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTagBg
import com.example.ui.theme.BrandTerracotta
import com.example.ui.theme.BrandTerracottaBg
import com.example.ui.theme.AppleSystemGreen

@Composable
fun ProfileScreen(
  profile: UserProfile,
  onToggleHaptics: () -> Unit,
  onToggleCalendarSync: () -> Unit,
  onToggleMorningBriefing: () -> Unit,
  onToggleAutoFocus: () -> Unit,
  onViewLevelCelebration: () -> Unit,
  onSignOut: () -> Unit,
  modifier: Modifier = Modifier,
  onToggleSounds: (() -> Unit)? = null,
  onOpenMilestoneJourney: () -> Unit = onViewLevelCelebration,
  currentLanguage: AppLanguage = AppLanguage.ID,
  onLanguageSelected: (AppLanguage) -> Unit = {}
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  val avatarInitials = remember(profile.name) {
    val words = profile.name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    when {
      words.size >= 2 -> "${words[0].first().uppercase()}${words[1].first().uppercase()}"
      words.size == 1 && words[0].length >= 2 -> words[0].take(2).uppercase()
      words.size == 1 -> words[0].take(1).uppercase()
      else -> "W"
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
  ) {
    // 1. Unified Fixed Top Bar (Rock-solid consistency with other tabs)
    UnifiedTopAppBar(
      title = "wii to do",
      subtitle = if (currentLanguage == AppLanguage.ID) "pengaturan & profil" else "settings & profile",
      showVerifiedBadge = false,
      actions = {
        // Language Toggle Pill
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color.White,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Row(modifier = Modifier.padding(3.dp)) {
            listOf(AppLanguage.ID to "ID", AppLanguage.EN to "EN").forEach { (lang, label) ->
              val isSelected = (currentLanguage == lang)
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    HapticEngine.selection(context, haptic)
                    onLanguageSelected(lang)
                  },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) BrandCharcoal else Color.Transparent
              ) {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else BrandSecondary,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }
    )

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      contentPadding = androidx.compose.foundation.layout.PaddingValues(
        top = 4.dp,
        bottom = 150.dp
      ),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

    // 2. Profile Header Card
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = BrandAvatarBg,
            border = BorderStroke(1.5.dp, BrandAvatarBorder)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = avatarInitials,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal
              )
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = profile.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal,
                letterSpacing = (-0.01).sp
              )
              if (profile.isVerified) {
                Spacer(modifier = Modifier.width(6.dp))
                VerifiedBadgeIcon(size = 18.dp)
              }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = profile.program,
              fontSize = 12.sp,
              color = BrandSecondary
            )

            Text(
              text = profile.email,
              fontSize = 11.sp,
              color = BrandSecondary.copy(alpha = 0.8f)
            )
          }
        }
      }
    }

    // 3. Level 5 Focus Architect Card (Milestone Banner)
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onOpenMilestoneJourney()
          },
        shape = RoundedCornerShape(16.dp),
        color = BrandCharcoal,
        shadowElevation = 4.dp
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(32.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Level ${profile.level} • ${profile.levelTitle}",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = "Mastery in Deep Flow & Architecture",
                  fontSize = 11.sp,
                  color = Color.White.copy(alpha = 0.7f)
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color.White.copy(alpha = 0.2f)
            ) {
              Text(
                text = "MILESTONE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // XP Progress Bar
          val xpPct = (profile.currentXp.toFloat() / profile.targetXp.toFloat()).coerceIn(0f, 1f)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "${profile.currentXp} / ${profile.targetXp} XP",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color.White.copy(alpha = 0.9f)
            )
            Text(
              text = "Tap to view perks",
              fontSize = 11.sp,
              color = Color.White.copy(alpha = 0.75f)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { xpPct },
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(2.5.dp)),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.2f)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "✓ Unlocked: Focus Streaks, Architectural Matrix Tags, 1.25x Multiplier",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.75f)
          )
        }
      }
    }

    // 4. Productivity Metric 3-Column Badges
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Triple("Tasks Done", "${profile.tasksDoneCount}", "tasks"),
          Triple("On-Time Rate", profile.onTimeRate, "rate"),
          Triple("Focus Time", profile.focusHoursLogged, "logged")
        ).forEach { (title, stat, _) ->
          Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BrandBorder)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = title,
                fontSize = 11.sp,
                color = BrandSecondary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = stat,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal
              )
            }
          }
        }
      }
    }

    // 5. Academic Workspace Section
    item {
      Text(
        text = "ACADEMIC WORKSPACE",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = BrandSecondary,
        letterSpacing = 0.8.sp
      )
    }

    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
          SettingToggleRow(
            icon = Icons.Default.School,
            title = "Campus Calendar Sync",
            subtitle = "Sync with University Canvas & Google Calendar",
            checked = profile.campusSyncEnabled,
            onCheckedChange = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleCalendarSync()
            }
          )
        }
      }
    }

    // 6. Preferences & Flow Section
    item {
      Text(
        text = "PREFERENCES & FLOW",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = BrandSecondary,
        letterSpacing = 0.8.sp
      )
    }

    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
          SettingToggleRow(
            icon = Icons.Default.Notifications,
            title = "Morning Briefing Notification",
            subtitle = "Daily 07:30 agenda summary",
            checked = profile.morningBriefingEnabled,
            onCheckedChange = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleMorningBriefing()
            }
          )
          HorizontalDivider(color = BrandBorderLight, thickness = 1.dp)
          SettingToggleRow(
            icon = Icons.Default.Headphones,
            title = "Auto Focus Mode",
            subtitle = "Activate DND when sprint starts",
            checked = profile.autoFocusMode,
            onCheckedChange = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleAutoFocus()
            }
          )
        }
      }
    }

    // 7. System & Interface Section
    item {
      Text(
        text = "SYSTEM & INTERFACE",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = BrandSecondary,
        letterSpacing = 0.8.sp
      )
    }

    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
          SettingToggleRow(
            icon = Icons.Default.Vibration,
            title = "Haptic Tactile Feedback",
            subtitle = "Subtle tactile haptic on task checkbox and actions",
            checked = profile.hapticFeedback,
            onCheckedChange = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleHaptics()
            }
          )
          HorizontalDivider(color = BrandBorderLight, thickness = 1.dp)
          // Display Language Row (Segmented ID / EN)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(BrandPillBg, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Translate,
                  contentDescription = null,
                  tint = BrandCharcoal,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = if (currentLanguage == AppLanguage.ID) "Bahasa Tampilan" else "Display Language",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandCharcoal
                )
                Text(
                  text = if (currentLanguage == AppLanguage.ID) "Pilih bahasa antarmuka aplikasi" else "Choose application interface language",
                  fontSize = 11.sp,
                  color = BrandSecondary
                )
              }
            }
            LanguageSwitchPill(
              currentLanguage = currentLanguage,
              onLanguageSelected = onLanguageSelected
            )
          }
        }
      }
    }

    // 8. Sign Out Row
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onSignOut()
          },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ExitToApp,
              contentDescription = null,
              tint = BrandTerracotta,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = if (currentLanguage == AppLanguage.ID) "Keluar Akun" else "Sign Out & Switch Account",
              fontSize = 14.sp,
              fontWeight = FontWeight.Medium,
              color = BrandTerracotta
            )
          }
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = BrandSecondary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
}

@Composable
fun SettingToggleRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = BrandCharcoal,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = BrandCharcoal
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = BrandSecondary
        )
      }
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.White,
        checkedTrackColor = AppleSystemGreen,
        uncheckedThumbColor = Color.White,
        uncheckedTrackColor = BrandBorder
      )
    )
  }
}
