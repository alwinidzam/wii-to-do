package com.example.ui.i18n

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandSecondary

enum class AppLanguage(val code: String, val label: String) {
  ID("id", "ID"),
  EN("en", "EN")
}

data class AppStrings(
  val appName: String,
  val workspaceSubtitle: String,
  val navHome: String,
  val navSchedule: String,
  val navProjects: String,
  val navProfile: String,
  val greetingMorning: String,
  val greetingAfternoon: String,
  val greetingEvening: String,
  val greetingNight: String,
  val tasksTodayBadge: String,
  val todaysTasks: String,
  val remaining: String,
  val completed: String,
  val searchPlaceholder: String,
  val categoryAll: String,
  val categoryCollege: String,
  val categoryWork: String,
  val categoryPersonal: String,
  val resetFilters: String,
  val emptyTasks: String,
  val footerDone: String,
  val scheduleTitle: String,
  val day: String,
  val week: String,
  val commitments: String,
  val projectsTitle: String,
  val profileTitle: String,
  val displayLanguage: String,
  val displayLanguageSub: String,
  val signOut: String
)

object Translations {
  val ID = AppStrings(
    appName = "wii to do",
    workspaceSubtitle = "workspace",
    navHome = "Beranda",
    navSchedule = "Jadwal",
    navProjects = "Proyek",
    navProfile = "Profil",
    greetingMorning = "Selamat pagi",
    greetingAfternoon = "Selamat siang",
    greetingEvening = "Selamat sore",
    greetingNight = "Selamat malam",
    tasksTodayBadge = "tugas hari ini",
    todaysTasks = "TUGAS HARI INI",
    remaining = "tersisa",
    completed = "SELESAI",
    searchPlaceholder = "Cari tugas, tag, atau proyek...",
    categoryAll = "Semua",
    categoryCollege = "Kuliah",
    categoryWork = "Kerja",
    categoryPersonal = "Pribadi",
    resetFilters = "Reset filter",
    emptyTasks = "Semua tugas hari ini selesai. Istirahatlah sejenak.",
    footerDone = "Semua tugas hari ini sudah tercatat",
    scheduleTitle = "jadwal",
    day = "Hari",
    week = "Minggu",
    commitments = "agenda",
    projectsTitle = "proyek",
    profileTitle = "pengaturan",
    displayLanguage = "Bahasa Tampilan",
    displayLanguageSub = "Pilih bahasa antarmuka aplikasi",
    signOut = "Keluar Akun"
  )

  val EN = AppStrings(
    appName = "wii to do",
    workspaceSubtitle = "workspace",
    navHome = "Home",
    navSchedule = "Schedule",
    navProjects = "Projects",
    navProfile = "Profile",
    greetingMorning = "Good morning",
    greetingAfternoon = "Good afternoon",
    greetingEvening = "Good evening",
    greetingNight = "Good night",
    tasksTodayBadge = "tasks today",
    todaysTasks = "TODAY'S TASKS",
    remaining = "remaining",
    completed = "COMPLETED",
    searchPlaceholder = "Search tasks, tags, or projects...",
    categoryAll = "All",
    categoryCollege = "College",
    categoryWork = "Work",
    categoryPersonal = "Personal",
    resetFilters = "Reset filters",
    emptyTasks = "All tasks for today are completed. Take a breath.",
    footerDone = "That's everything for today",
    scheduleTitle = "schedule",
    day = "Day",
    week = "Week",
    commitments = "commitments",
    projectsTitle = "projects",
    profileTitle = "settings",
    displayLanguage = "Display Language",
    displayLanguageSub = "Choose application interface language",
    signOut = "Sign Out"
  )

  fun get(lang: AppLanguage): AppStrings = when (lang) {
    AppLanguage.ID -> ID
    AppLanguage.EN -> EN
  }
}

/**
 * High-end tactile Language Switcher Pill [ ID | EN ]
 * Designed for mobile thumb reach with 44dp minimum touch target ergonomics.
 */
@Composable
fun LanguageSwitchPill(
  currentLanguage: AppLanguage,
  onLanguageSelected: (AppLanguage) -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = Color.White,
    border = BorderStroke(1.dp, BrandBorder)
  ) {
    Row(
      modifier = Modifier.padding(2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      listOf(AppLanguage.ID, AppLanguage.EN).forEach { lang ->
        val isSelected = lang == currentLanguage
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) BrandCharcoal else Color.Transparent)
            .clickable {
              if (!isSelected) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onLanguageSelected(lang)
              }
            }
            .padding(horizontal = 9.dp, vertical = 5.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = lang.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else BrandSecondary
          )
        }
      }
    }
  }
}
