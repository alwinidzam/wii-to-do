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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import com.example.data.auth.AuthResult
import com.example.ui.sheets.AvatarPickerBottomSheet
import com.example.ui.sheets.ChangePasswordBottomSheet
import com.example.ui.sheets.EditProfileBottomSheet
import com.example.ui.sheets.PRESET_AVATARS
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedBadgeTier
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
import com.example.ui.theme.AppleSystemBlue

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
  onOpenAcademicManager: (() -> Unit)? = null,
  onUpdatePersonalTargets: ((Double, Int, Double) -> Unit)? = null,
  currentLanguage: AppLanguage = AppLanguage.ID,
  onLanguageSelected: (AppLanguage) -> Unit = {},
  onUpdateProfile: ((name: String, university: String, program: String, studentId: String, focusGoal: String) -> Unit)? = null,
  onUpdateAvatar: ((avatarUri: String?, presetId: String?, colorHex: Long?) -> Unit)? = null,
  onChangePassword: (suspend (currentPassword: String, newPassword: String) -> AuthResult)? = null
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var showTargetDialog by remember { mutableStateOf(false) }
  var showAvatarPickerSheet by remember { mutableStateOf(false) }
  var showEditProfileSheet by remember { mutableStateOf(false) }
  var showChangePasswordSheet by remember { mutableStateOf(false) }

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
      showVerifiedBadge = (profile.resolvedBadgeTier != VerifiedBadgeTier.NONE),
      verifiedTier = profile.resolvedBadgeTier,
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
          // Interactive Avatar with Camera Badge
          val avatarBgColor = profile.avatarColorHex?.let { Color(it) } ?: BrandAvatarBg
          val preset = PRESET_AVATARS.find { it.id == profile.avatarPresetId }

          Box(
            modifier = Modifier
              .size(62.dp)
              .clip(CircleShape)
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                showAvatarPickerSheet = true
              }
          ) {
            Surface(
              modifier = Modifier.fillMaxSize(),
              shape = CircleShape,
              color = avatarBgColor,
              border = BorderStroke(1.5.dp, BrandAvatarBorder)
            ) {
              Box(contentAlignment = Alignment.Center) {
                if (preset != null) {
                  Text(
                    text = preset.emoji,
                    fontSize = 26.sp
                  )
                } else if (!profile.avatarUri.isNullOrEmpty()) {
                  Text(
                    text = "📷",
                    fontSize = 24.sp
                  )
                } else {
                  Text(
                    text = avatarInitials,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (profile.avatarColorHex != null) Color.White else BrandCharcoal
                  )
                }
              }
            }

            // Edit Camera Badge Overlay (Apple HIG style)
            Surface(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(22.dp),
              shape = CircleShape,
              color = BrandCharcoal,
              border = BorderStroke(1.5.dp, Color.White),
              shadowElevation = 2.dp
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.PhotoCamera,
                  contentDescription = "Ganti Avatar",
                  tint = Color.White,
                  modifier = Modifier.size(11.dp)
                )
              }
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
              if (profile.resolvedBadgeTier != VerifiedBadgeTier.NONE) {
                Spacer(modifier = Modifier.width(6.dp))
                VerifiedBadgeIcon(size = 18.dp, tier = profile.resolvedBadgeTier)
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Edit Profile Button (Apple HIG / Linear style Pill)
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = BrandPillBg,
                border = BorderStroke(1.dp, BrandBorderLight),
                modifier = Modifier.clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  showEditProfileSheet = true
                }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = BrandCharcoal,
                    modifier = Modifier.size(11.dp)
                  )
                  Text(
                    text = if (currentLanguage == AppLanguage.ID) "Edit Profil" else "Edit Profile",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandCharcoal
                  )
                }
              }

              if (profile.resolvedBadgeTier != VerifiedBadgeTier.NONE) {
                val tierColor = Color(profile.resolvedBadgeTier.primaryColorHex)
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = tierColor.copy(alpha = 0.10f),
                  border = BorderStroke(0.75.dp, tierColor.copy(alpha = 0.35f))
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    VerifiedBadgeIcon(size = 12.dp, tier = profile.resolvedBadgeTier)
                    Text(
                      text = profile.resolvedBadgeTier.title,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = tierColor
                    )
                  }
                }
              }
            }
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

    // 5. Academic Workspace & Semester Management Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "MANAJEMEN AKADEMIK & SEMESTER",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )
        if (onOpenAcademicManager != null) {
          Text(
            text = "Kelola Matkul",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = BrandOlive,
            modifier = Modifier.clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onOpenAcademicManager()
            }
          )
        }
      }
    }

    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onOpenAcademicManager?.invoke()
          },
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Semester Aktif: Semester ${profile.activeSemesterId.replace("sem_", "")}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal
              )
              Text(
                text = "Target IPK: ${String.format(java.util.Locale.US, "%.2f", profile.targetGpa)} • Kapasitas: ${profile.targetSks} SKS",
                fontSize = 12.sp,
                color = BrandSecondary
              )
            }
            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = null,
              tint = BrandSecondary
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = BrandBorderLight, thickness = 1.dp)
          Spacer(modifier = Modifier.height(10.dp))

          SettingToggleRow(
            icon = Icons.Default.School,
            title = "Campus Calendar Sync",
            subtitle = "Sinkronisasi otomatis tugas & jadwal kuliah Canvas/Google",
            checked = profile.campusSyncEnabled,
            onCheckedChange = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleCalendarSync()
            }
          )
        }
      }
    }

    // 5B. Personal Study & Target Focus Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (currentLanguage == AppLanguage.ID) "TARGET STUDI & FOKUS PRIBADI" else "STUDY & FOCUS TARGETS",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFF0F5FF),
          border = BorderStroke(1.dp, Color(0xFFCCE0FF)),
          modifier = Modifier.clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showTargetDialog = true
          }
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              tint = AppleSystemBlue,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (currentLanguage == AppLanguage.ID) "Ubah Target" else "Edit Targets",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = AppleSystemBlue
            )
          }
        }
      }
    }

    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showTargetDialog = true
          },
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Target Fokus Harian",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandCharcoal
              )
              Text(
                text = "Komitmen waktu fokus mendalam per hari",
                fontSize = 11.sp,
                color = BrandSecondary
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = BrandPillBg,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Text(
                text = "${profile.targetDailyFocusHours} jam / hari",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = BrandBorderLight, thickness = 1.dp)
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Target Beban SKS Semester Ini",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandCharcoal
              )
              Text(
                text = "Batas maksimum kredit mata kuliah",
                fontSize = 11.sp,
                color = BrandSecondary
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = BrandPillBg,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Text(
                text = "${profile.targetSks} SKS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = BrandBorderLight, thickness = 1.dp)
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Target IPK Kumulatif",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandCharcoal
              )
              Text(
                text = "Standar pencapaian akademik pribadi",
                fontSize = 11.sp,
                color = BrandSecondary
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = BrandPillBg,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Text(
                text = String.format(java.util.Locale.US, "%.2f", profile.targetGpa),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
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

    // 8. Security & Account Section
    item {
      Text(
        text = if (currentLanguage == AppLanguage.ID) "KEAMANAN & AKUN" else "SECURITY & ACCOUNT",
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
          // Change Password Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                showChangePasswordSheet = true
              }
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
                  imageVector = Icons.Default.Lock,
                  contentDescription = null,
                  tint = BrandCharcoal,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = if (currentLanguage == AppLanguage.ID) "Ganti Kata Sandi" else "Change Password",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandCharcoal
                )
                Text(
                  text = if (currentLanguage == AppLanguage.ID) "Amankan akun dengan kata sandi baru" else "Protect account with updated credentials",
                  fontSize = 11.sp,
                  color = BrandSecondary
                )
              }
            }
            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = null,
              tint = BrandSecondary,
              modifier = Modifier.size(18.dp)
            )
          }

          HorizontalDivider(color = BrandBorderLight, thickness = 1.dp)

          // Email Info Row
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
                  imageVector = Icons.Default.Badge,
                  contentDescription = null,
                  tint = BrandCharcoal,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = if (currentLanguage == AppLanguage.ID) "Email Terdaftar" else "Registered Email",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandCharcoal
                )
                Text(
                  text = profile.email,
                  fontSize = 11.sp,
                  color = BrandSecondary
                )
              }
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = AppleSystemGreen.copy(alpha = 0.12f),
              border = BorderStroke(0.5.dp, AppleSystemGreen.copy(alpha = 0.4f))
            ) {
              Text(
                text = "TERVERIFIKASI",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = AppleSystemGreen,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }

    // 9. Sign Out Row
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

  if (showTargetDialog) {
    EditPersonalTargetsDialog(
      initialGpa = profile.targetGpa,
      initialSks = profile.targetSks,
      initialFocusHours = profile.targetDailyFocusHours,
      currentLanguage = currentLanguage,
      onDismiss = { showTargetDialog = false },
      onSave = { gpa, sks, focusHours ->
        onUpdatePersonalTargets?.invoke(gpa, sks, focusHours)
        showTargetDialog = false
      }
    )
  }

  if (showAvatarPickerSheet) {
    AvatarPickerBottomSheet(
      currentAvatarUri = profile.avatarUri,
      currentPresetId = profile.avatarPresetId,
      currentColorHex = profile.avatarColorHex ?: 0xFF1E293B,
      initials = avatarInitials,
      onDismiss = { showAvatarPickerSheet = false },
      onSaveAvatar = { uri, presetId, colorHex ->
        onUpdateAvatar?.invoke(uri, presetId, colorHex)
        showAvatarPickerSheet = false
      }
    )
  }

  if (showEditProfileSheet) {
    EditProfileBottomSheet(
      profile = profile,
      onDismiss = { showEditProfileSheet = false },
      onSaveProfile = { name, univ, prog, nim, goal ->
        onUpdateProfile?.invoke(name, univ, prog, nim, goal)
        showEditProfileSheet = false
      }
    )
  }

  if (showChangePasswordSheet && onChangePassword != null) {
    ChangePasswordBottomSheet(
      onDismiss = { showChangePasswordSheet = false },
      onChangePassword = onChangePassword
    )
  }
}


@Composable
fun EditPersonalTargetsDialog(
  initialGpa: Double,
  initialSks: Int,
  initialFocusHours: Double,
  currentLanguage: AppLanguage,
  onDismiss: () -> Unit,
  onSave: (Double, Int, Double) -> Unit
) {
  var gpaText by remember { mutableStateOf(if (initialGpa > 0.0) String.format(java.util.Locale.US, "%.2f", initialGpa) else "3.85") }
  var sksText by remember { mutableStateOf(if (initialSks > 0) initialSks.toString() else "21") }
  var focusHoursText by remember { mutableStateOf(if (initialFocusHours > 0.0) String.format(java.util.Locale.US, "%.1f", initialFocusHours) else "4.0") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (currentLanguage == AppLanguage.ID) "Ubah Target Personal" else "Edit Personal Targets",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = BrandCharcoal
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = if (currentLanguage == AppLanguage.ID)
            "Sesuaikan target akademik dan komitmen fokus harian Anda."
          else
            "Customize your academic goals and daily focus commitments.",
          fontSize = 12.sp,
          color = BrandSecondary,
          modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
          value = gpaText,
          onValueChange = { gpaText = it },
          label = { Text(if (currentLanguage == AppLanguage.ID) "Target IPK (misal 3.85)" else "Target GPA (e.g. 3.85)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = sksText,
          onValueChange = { sksText = it },
          label = { Text(if (currentLanguage == AppLanguage.ID) "Target SKS (misal 21)" else "Target Credits / SKS (e.g. 21)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = focusHoursText,
          onValueChange = { focusHoursText = it },
          label = { Text(if (currentLanguage == AppLanguage.ID) "Target Jam Fokus/Hari (misal 4.0)" else "Daily Focus Hours (e.g. 4.0)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val gpa = gpaText.toDoubleOrNull() ?: initialGpa
          val sks = sksText.toIntOrNull() ?: initialSks
          val focusHours = focusHoursText.toDoubleOrNull() ?: initialFocusHours
          onSave(gpa, sks, focusHours)
        },
        colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal)
      ) {
        Text(if (currentLanguage == AppLanguage.ID) "Simpan Target" else "Save Targets", color = Color.White)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text(if (currentLanguage == AppLanguage.ID) "Batal" else "Cancel", color = BrandSecondary)
      }
    }
  )
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
