package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedBadgeTier
import com.example.ui.components.DateStripSelector
import com.example.ui.components.LinearBorderHairline
import com.example.ui.components.LinearDoneGreen
import com.example.ui.components.TaskCardItem
import com.example.ui.components.UnifiedTopAppBar
import com.example.ui.components.VerifiedBadgeIcon
import com.example.ui.components.WiiBrandLogo
import com.example.ui.sheets.DeletedTasksBottomSheet
import com.example.ui.components.getTodayIndex
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.Translations
import com.example.ui.theme.BrandAvatarBg
import com.example.ui.theme.BrandAvatarBorder
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandOliveBg
import com.example.ui.theme.BrandOliveBorder
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta
import com.example.ui.theme.HapticEngine
import java.util.Calendar

/**
 * Super High-End Apple HIG & Linear-Grade Home Screen.
 * - Zero awkward dead space: ultra-compact 48dp header docked right at status bar
 * - Micro 2.5dp daily progress track integrated in navigation bar
 * - Quick search toggle (no permanent 44dp obstruction)
 * - Week date strip and category filters
 * - Tasks immediately accessible into thumb reach
 * - Dual-direction swipe-to-triage gestures on all tasks
 */
@Composable
fun HomeScreen(
  tasks: List<TaskItem>,
  selectedDayIndex: Int,
  selectedCategory: String,
  searchQuery: String,
  onDaySelected: (Int) -> Unit,
  onCategorySelected: (String) -> Unit,
  onSearchQueryChanged: (String) -> Unit,
  onToggleTaskComplete: (String) -> Unit,
  onTaskClick: (String) -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier,
  userName: String = "Alwi",
  onDeleteTask: (String) -> Unit = {},
  userProfile: UserProfile? = null,
  onMilestoneClick: () -> Unit = {},
  currentLanguage: AppLanguage = AppLanguage.ID,
  onLanguageSelected: (AppLanguage) -> Unit = {},
  deletedTasks: List<TaskItem> = emptyList(),
  onRestoreTask: (String) -> Unit = {},
  onPermanentlyDeleteTask: (String) -> Unit = {},
  onEmptyTrash: () -> Unit = {},
  onStartFocusTask: ((TaskItem) -> Unit)? = null
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var hasUnreadNotification by remember { mutableStateOf(false) }
  var isSearchVisible by remember { mutableStateOf(false) }
  var selectedTaskId by remember { mutableStateOf<String?>(null) }
  var showTrashSheet by remember { mutableStateOf(false) }

  // Filter tasks
  val filteredTasks = tasks.filter { task ->
    val matchesCategory = selectedCategory == "All" ||
        task.category.equals(selectedCategory, ignoreCase = true)
    val matchesSearch = searchQuery.isBlank() ||
        task.title.contains(searchQuery, ignoreCase = true) ||
        task.category.contains(searchQuery, ignoreCase = true)
    val todayIdx = getTodayIndex()
    val tomorrowIdx = (todayIdx + 1) % 7
    val matchesDay = when (selectedDayIndex) {
      todayIdx -> !task.isTomorrow
      tomorrowIdx -> task.isTomorrow || task.dueDate.contains("Tomorrow", ignoreCase = true)
      else -> true
    }
    matchesCategory && matchesSearch && matchesDay
  }

  val activeTasks = filteredTasks.filter { !it.isCompleted }
  val completedTasks = filteredTasks.filter { it.isCompleted }
  val totalRemaining = activeTasks.size

  val strings = remember(currentLanguage) { Translations.get(currentLanguage) }

  val avatarInitials = remember(userProfile?.name, userName) {
    val nameToUse = userProfile?.name?.ifBlank { userName } ?: userName
    val words = nameToUse.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    when {
      words.size >= 2 -> "${words[0].first().uppercase()}${words[1].first().uppercase()}"
      words.size == 1 && words[0].length >= 2 -> words[0].take(2).uppercase()
      words.size == 1 -> words[0].take(1).uppercase()
      else -> "W"
    }
  }

  // Category counts
  val countAll = tasks.size
  val countCollege = tasks.count { it.category.equals("College", ignoreCase = true) }
  val countWork = tasks.count { it.category.equals("Work", ignoreCase = true) }
  val countPersonal = tasks.count { it.category.equals("Personal", ignoreCase = true) }

  val categories = listOf(
    Triple("All", countAll, strings.categoryAll),
    Triple("College", countCollege, strings.categoryCollege),
    Triple("Work", countWork, strings.categoryWork),
    Triple("Personal", countPersonal, strings.categoryPersonal)
  )

  // Overall completion progress
  val totalTasks = tasks.size
  val completedCount = tasks.count { it.isCompleted }
  val progressFraction = if (totalTasks > 0) completedCount.toFloat() / totalTasks.toFloat() else 0f
  val progressPercent = (progressFraction * 100).toInt()

  val greeting = remember(currentLanguage) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    when {
      hour < 12 -> strings.greetingMorning
      hour < 15 -> strings.greetingAfternoon
      hour < 18 -> strings.greetingEvening
      else -> strings.greetingNight
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
  ) {
    // 1. Unified Fixed Navigation Bar (100% Consistent across Schedule, Projects, Profile)
    UnifiedTopAppBar(
      title = strings.appName,
      subtitle = "$greeting, $userName",
      showVerifiedBadge = (userProfile?.resolvedBadgeTier != null && userProfile.resolvedBadgeTier != VerifiedBadgeTier.NONE),
      verifiedTier = userProfile?.resolvedBadgeTier ?: VerifiedBadgeTier.NONE,
      subtitleSuffix = " • $totalRemaining ${strings.remaining}",
      actions = {
        // Search Toggle Button (34dp)
        Surface(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable {
              HapticEngine.selection(context, haptic)
              isSearchVisible = !isSearchVisible
            },
          shape = CircleShape,
          color = if (isSearchVisible || searchQuery.isNotBlank()) BrandCharcoal else Color.White,
          border = BorderStroke(0.75.dp, LinearBorderHairline)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = if (isSearchVisible || searchQuery.isNotBlank()) Icons.Default.Close else Icons.Outlined.Search,
              contentDescription = "Search",
              tint = if (isSearchVisible || searchQuery.isNotBlank()) Color.White else BrandSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        // Trash / Riwayat Terhapus Button (34dp)
        Surface(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable {
              HapticEngine.selection(context, haptic)
              showTrashSheet = true
            },
          shape = CircleShape,
          color = Color.White,
          border = BorderStroke(0.75.dp, LinearBorderHairline)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Outlined.Delete,
              contentDescription = "Riwayat Terhapus",
              tint = if (deletedTasks.isNotEmpty()) BrandTerracotta else BrandSecondary,
              modifier = Modifier.size(16.dp)
            )
            if (deletedTasks.isNotEmpty()) {
              Box(
                modifier = Modifier
                  .size(7.dp)
                  .align(Alignment.TopEnd)
                  .padding(top = 4.dp, end = 4.dp)
                  .background(BrandTerracotta, CircleShape)
              )
            }
          }
        }

        // Notification Bell Button (34dp)
        Surface(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable {
              HapticEngine.selection(context, haptic)
              hasUnreadNotification = false
            },
          shape = CircleShape,
          color = Color.White,
          border = BorderStroke(0.75.dp, LinearBorderHairline)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Outlined.Notifications,
              contentDescription = "Notifications",
              tint = BrandSecondary,
              modifier = Modifier.size(16.dp)
            )
            if (hasUnreadNotification) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .align(Alignment.TopEnd)
                  .padding(top = 4.dp, end = 4.dp)
                  .background(BrandTerracotta, CircleShape)
              )
            }
          }
        }

        // User Profile Avatar with Online Status (34dp)
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable {
              HapticEngine.selection(context, haptic)
              onProfileClick()
            }
        ) {
          Surface(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            color = BrandAvatarBg,
            border = BorderStroke(1.dp, BrandAvatarBorder)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = avatarInitials,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandCharcoal
              )
            }
          }
          // Online status dot
          Box(
            modifier = Modifier
              .size(8.dp)
              .align(Alignment.BottomEnd)
              .background(BrandOlive, CircleShape)
              .border(BorderStroke(1.5.dp, Color.White), CircleShape)
          )
        }
      }
    )

        // Micro Progress Line (only visible when tasks exist)
        if (totalTasks > 0) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(2.5.dp)
              .background(BrandBorderLight)
          ) {
            Box(
              modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progressFraction)
                .background(if (progressPercent == 100) LinearDoneGreen else BrandCharcoal)
            )
          }
        }

    // 2. High-Efficiency Content Feed (Immediate Task Access)
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(
        top = 10.dp,
        bottom = 120.dp
      ),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Inline Search Bar (Expands smoothly when toggled)
      if (isSearchVisible || searchQuery.isNotBlank()) {
        item {
          AnimatedVisibility(
            visible = isSearchVisible || searchQuery.isNotBlank(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
          ) {
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
              shape = RoundedCornerShape(10.dp),
              color = Color.White,
              border = BorderStroke(0.75.dp, LinearBorderHairline)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Outlined.Search,
                  contentDescription = null,
                  tint = BrandSecondary,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                TextField(
                  value = searchQuery,
                  onValueChange = onSearchQueryChanged,
                  placeholder = {
                    Text(
                      text = strings.searchPlaceholder,
                      fontSize = 12.sp,
                      color = BrandSecondary.copy(alpha = 0.55f)
                    )
                  },
                  colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = BrandCharcoal,
                    unfocusedTextColor = BrandCharcoal
                  ),
                  singleLine = true,
                  modifier = Modifier.weight(1f)
                )
                if (searchQuery.isNotEmpty()) {
                  IconButton(
                    onClick = {
                      HapticEngine.selection(context, haptic)
                      onSearchQueryChanged("")
                    },
                    modifier = Modifier.size(22.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Clear",
                      tint = BrandSecondary,
                      modifier = Modifier.size(13.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Date Strip Selector (Week Strip)
      item {
        DateStripSelector(
          selectedIndex = selectedDayIndex,
          onDaySelected = {
            HapticEngine.selection(context, haptic)
            onDaySelected(it)
          }
        )
      }

      // Category Filter Chips
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          categories.forEach { (catId, count, label) ->
            val isSelected = (catId == selectedCategory)
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
              targetValue = if (isPressed) 0.95f else 1.0f,
              animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
              label = "cat_chip_scale"
            )

            Surface(
              modifier = Modifier
                .testTag("filter_chip_${catId.lowercase()}")
                .graphicsLayer {
                  scaleX = scale
                  scaleY = scale
                }
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                  interactionSource = interactionSource,
                  indication = null
                ) {
                  HapticEngine.selection(context, haptic)
                  onCategorySelected(catId)
                },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) BrandCharcoal else Color.White,
              border = if (isSelected) null else BorderStroke(0.75.dp, LinearBorderHairline)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
              ) {
                Text(
                  text = label,
                  fontSize = 11.5.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (isSelected) Color.White else BrandSecondary
                )

                // Counter badge
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (isSelected) Color.White.copy(alpha = 0.22f) else BrandCanvas,
                  border = if (isSelected) null else BorderStroke(0.5.dp, LinearBorderHairline)
                ) {
                  Text(
                    text = count.toString(),
                    fontSize = 9.5.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) Color.White else BrandSecondary,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Section Header (Tasks count)
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .background(BrandCharcoal, CircleShape)
            )
            Text(
              text = strings.todaysTasks,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal,
              letterSpacing = 0.5.sp
            )
          }

          Text(
            text = "$totalRemaining ${strings.remaining}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = BrandSecondary
          )
        }
      }

      // Tasks List & Empty States
      if (activeTasks.isEmpty() && completedTasks.isEmpty()) {
        item {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 16.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(0.75.dp, LinearBorderHairline)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .background(BrandCanvas, CircleShape)
                  .border(0.75.dp, LinearBorderHairline, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (searchQuery.isNotBlank()) Icons.Outlined.Search else Icons.Outlined.CheckCircle,
                  contentDescription = null,
                  tint = BrandSecondary,
                  modifier = Modifier.size(20.dp)
                )
              }

              val emptyTitle = when {
                searchQuery.isNotBlank() -> if (currentLanguage == AppLanguage.ID) "Tidak ada tugas yang cocok" else "No matching tasks found"
                selectedCategory != "All" -> if (currentLanguage == AppLanguage.ID) "Tidak ada tugas di kategori $selectedCategory" else "No tasks in $selectedCategory"
                tasks.isEmpty() -> if (currentLanguage == AppLanguage.ID) "Belum ada tugas" else "No tasks yet"
                else -> if (currentLanguage == AppLanguage.ID) "Semua tugas selesai!" else "All clear for today!"
              }

              val emptySubtitle = when {
                searchQuery.isNotBlank() -> if (currentLanguage == AppLanguage.ID) "Coba cari dengan kata kunci lain." else "Try searching with a different keyword."
                selectedCategory != "All" -> if (currentLanguage == AppLanguage.ID) "Ganti kategori atau ketuk tombol + untuk menambahkan tugas." else "Switch categories or tap + to create a task."
                tasks.isEmpty() -> if (currentLanguage == AppLanguage.ID) "Ruang fokus Anda bersih. Ketuk tombol + untuk menambahkan tugas." else "Your focus workspace is clean. Tap + below to add a task."
                else -> if (currentLanguage == AppLanguage.ID) "Kerja bagus! Anda telah menyelesaikan seluruh komitmen hari ini." else "Great work! You have completed all scheduled commitments."
              }

              Text(
                text = emptyTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandCharcoal
              )

              Text(
                text = emptySubtitle,
                fontSize = 11.5.sp,
                color = BrandSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 15.sp
              )

              if (searchQuery.isNotBlank() || selectedCategory != "All") {
                Spacer(modifier = Modifier.height(2.dp))
                OutlinedButton(
                  onClick = {
                    HapticEngine.selection(context, haptic)
                    onSearchQueryChanged("")
                    onCategorySelected("All")
                  },
                  shape = RoundedCornerShape(8.dp),
                  border = BorderStroke(0.75.dp, LinearBorderHairline),
                  colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandCharcoal
                  )
                ) {
                  Text(
                    text = strings.resetFilters,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      } else {
        // Active Tasks
        items(
          items = activeTasks,
          key = { it.id }
        ) { task ->
          TaskCardItem(
            task = task,
            onToggleComplete = {
              onToggleTaskComplete(task.id)
            },
            onClick = { onTaskClick(task.id) },
            onDelete = {
              selectedTaskId = null
              onDeleteTask(task.id)
            },
            isSelected = (selectedTaskId == task.id),
            onSelect = {
              selectedTaskId = if (selectedTaskId == task.id) null else task.id
            },
            onStartFocus = onStartFocusTask?.let { startFn -> { startFn(task) } }
          )
        }

        // Completed Tasks
        if (completedTasks.isNotEmpty()) {
          item {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(5.dp)
                  .background(BrandSecondary.copy(alpha = 0.5f), CircleShape)
              )
              Text(
                text = "${strings.completed} (${completedTasks.size})",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandSecondary,
                letterSpacing = 0.5.sp
              )
            }
          }

          items(
            items = completedTasks,
            key = { it.id }
          ) { task ->
            TaskCardItem(
              task = task,
              onToggleComplete = {
                onToggleTaskComplete(task.id)
              },
              onClick = { onTaskClick(task.id) },
              onDelete = {
                selectedTaskId = null
                onDeleteTask(task.id)
              },
              isSelected = (selectedTaskId == task.id),
              onSelect = {
                selectedTaskId = if (selectedTaskId == task.id) null else task.id
              }
            )
          }
        }
      }

      // Minimalist Footer
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 6.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(width = 20.dp, height = 2.dp)
              .background(LinearBorderHairline, CircleShape)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = strings.footerDone,
            fontSize = 11.sp,
            color = BrandSecondary.copy(alpha = 0.75f)
          )
        }
      }
    }

    // Kotak Sampah / Riwayat Terhapus Bottom Sheet
    if (showTrashSheet) {
      DeletedTasksBottomSheet(
        deletedTasks = deletedTasks,
        onRestoreTask = { id ->
          onRestoreTask(id)
        },
        onPermanentlyDelete = { id ->
          onPermanentlyDeleteTask(id)
        },
        onEmptyTrash = {
          onEmptyTrash()
        },
        onDismiss = { showTrashSheet = false }
      )
    }
  }
}
