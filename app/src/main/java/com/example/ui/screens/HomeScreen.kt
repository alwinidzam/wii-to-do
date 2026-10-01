package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MilestoneTierLevel
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.example.ui.components.DateStripSelector
import com.example.ui.components.TaskCardItem
import com.example.ui.components.WiiBrandLogo
import com.example.ui.components.getTodayIndex
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LanguageSwitchPill
import com.example.ui.i18n.Translations
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta

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
  onLanguageSelected: (AppLanguage) -> Unit = {}
) {
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var hasUnreadNotification by remember { mutableStateOf(true) }

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

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
  ) {
    // 1. Fixed App Header (Pinned at top, does not scroll)
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = BrandCanvas
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(
            start = 20.dp,
            end = 20.dp,
            top = topInset + 12.dp,
            bottom = 8.dp
          ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Logo & Title
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          WiiBrandLogo(size = 32.dp)
          Column {
            Text(
              text = "WII To-Do",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal,
              letterSpacing = (-0.02).sp,
              lineHeight = 18.sp
            )
            Text(
              text = "FOCUS WORKSPACE",
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium,
              color = BrandSecondary,
              letterSpacing = 0.5.sp,
              lineHeight = 12.sp
            )
          }
        }

        // Quick Actions: Language Switcher + Notification bell + AP Avatar
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Language Switcher [ ID | EN ]
          LanguageSwitchPill(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected
          )
          // Notification Bell Button
          Surface(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                hasUnreadNotification = false
              },
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, BrandBorder)
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

          // User Profile Avatar with Online Status
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                  text = "AP",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandCharcoal
                )
              }
            }
            // Online status indicator dot
            Box(
              modifier = Modifier
                .size(10.dp)
                .align(Alignment.BottomEnd)
                .background(BrandOlive, CircleShape)
                .border(BorderStroke(2.dp, Color.White), CircleShape)
            )
          }
        }
      }
    }

    // Scrollable Task and Timeline Feed
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(
        top = 4.dp,
        bottom = 140.dp // Ensures navbar and FAB NEVER cover tasks
      ),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 2. Date & Greeting Header
      item {
      val locale = remember(currentLanguage) {
        if (currentLanguage == AppLanguage.ID) Locale("id", "ID") else Locale.ENGLISH
      }
      val dayHeader = remember(selectedDayIndex, currentLanguage) {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - Calendar.MONDAY
        calendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday + selectedDayIndex)
        val sdf = SimpleDateFormat("EEEE, MMM d", locale)
        sdf.format(calendar.time).uppercase()
      }
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
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = dayHeader,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandSecondary,
            letterSpacing = 0.8.sp
          )

          // Dynamic tasks today pill
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = BrandOliveBg,
            border = BorderStroke(1.dp, BrandOliveBorder)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .background(BrandOlive, CircleShape)
              )
              Text(
                text = "$totalRemaining ${strings.tasksTodayBadge}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = BrandOlive
              )
            }
          }
        }

        Text(
          text = "$greeting, $userName",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = BrandCharcoal,
          letterSpacing = (-0.02).sp
        )
      }
    }

    // 3. Architectural Milestone Banner ("Architect of Focus")
    val profile = userProfile ?: UserProfile()
    val tier = MilestoneTierLevel.fromXp(profile.totalXpAllTime)
    val tierSpan = (tier.maxXp - tier.minXp).toFloat().coerceAtLeast(1f)
    val progressInTier = (profile.totalXpAllTime - tier.minXp).toFloat().coerceIn(0f, tierSpan)
    val tierFraction = (progressInTier / tierSpan).coerceIn(0f, 1f)

    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(
            elevation = 1.dp,
            shape = RoundedCornerShape(14.dp),
            ambientColor = Color(0x06000000),
            spotColor = Color(0x05000000)
          )
          .clip(RoundedCornerShape(14.dp))
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onMilestoneClick()
          },
        shape = RoundedCornerShape(14.dp),
        color = BrandCharcoal
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.White.copy(alpha = 0.18f)
              ) {
                Text(
                  text = tier.badgeCode,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  letterSpacing = 0.8.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Text(
                text = "${tier.title} • Level ${profile.level}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  onMilestoneClick()
                },
              shape = RoundedCornerShape(6.dp),
              color = Color.White.copy(alpha = 0.15f)
            ) {
              Text(
                text = "Journey & Share →",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Checkpoint XP progress
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${profile.totalXpAllTime} Total XP",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color.White.copy(alpha = 0.85f)
            )
            Text(
              text = "Target: ${tier.maxXp} XP",
              fontSize = 10.sp,
              color = Color.White.copy(alpha = 0.65f)
            )
          }

          Spacer(modifier = Modifier.height(5.dp))

          LinearProgressIndicator(
            progress = { tierFraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp)),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.25f)
          )
        }
      }
    }

    // 4. Daily Progress Pulse Card (Super High-End Minimal Metric)
    if (totalTasks > 0) {
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .shadow(
              elevation = 1.dp,
              shape = RoundedCornerShape(14.dp),
              ambientColor = Color(0x06000000),
              spotColor = Color(0x05000000)
            ),
          shape = RoundedCornerShape(14.dp),
          color = Color.White,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.TaskAlt,
                  contentDescription = "Daily Progress",
                  tint = if (progressPercent == 100) BrandOlive else BrandCharcoal,
                  modifier = Modifier.size(15.dp)
                )
                Text(
                  text = if (progressPercent == 100) "All tasks completed!" else "Daily Completion",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandCharcoal
                )
              }

              Text(
                text = "$completedCount of $totalTasks ($progressPercent%)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (progressPercent == 100) BrandOlive else BrandSecondary
              )
            }

            // Minimalist Progress Track
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(BrandBorderLight)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxHeight()
                  .fillMaxWidth(progressFraction)
                  .clip(RoundedCornerShape(2.5.dp))
                  .background(if (progressPercent == 100) BrandOlive else BrandCharcoal)
              )
            }
          }
        }
      }
    }

    // 4. Micro Search Bar
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .shadow(
            elevation = 1.dp,
            shape = RoundedCornerShape(12.dp),
            ambientColor = Color(0x08000000),
            spotColor = Color(0x05000000)
          ),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "Search",
            tint = BrandSecondary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = {
              Text(
                text = strings.searchPlaceholder,
                fontSize = 12.sp,
                color = BrandSecondary.copy(alpha = 0.6f)
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
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSearchQueryChanged("")
              },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                tint = BrandSecondary,
                modifier = Modifier.size(14.dp)
              )
            }
          } else {
            // ⌘K Shortcut Chip
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = BrandCanvas,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Text(
                text = "⌘K",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = BrandSecondary.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }

    // 5. Interactive Micro-Calendar (Horizontal Week Strip)
    item {
      DateStripSelector(
        selectedIndex = selectedDayIndex,
        onDaySelected = onDaySelected
      )
    }

    // 6. Category Filter Pills with Counter Tags
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        categories.forEach { (catId, count, label) ->
          val isSelected = (catId == selectedCategory)
          val interactionSource = remember { MutableInteractionSource() }
          val isPressed by interactionSource.collectIsPressedAsState()
          val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.94f else 1.0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
            label = "cat_scale"
          )

          Surface(
            modifier = Modifier
              .testTag("filter_chip_${catId.lowercase()}")
              .graphicsLayer {
                scaleX = scale
                scaleY = scale
              }
              .clip(RoundedCornerShape(20.dp))
              .clickable(
                interactionSource = interactionSource,
                indication = null
              ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onCategorySelected(catId)
              },
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) BrandCharcoal else Color.White,
            border = if (isSelected) null else BorderStroke(1.dp, BrandBorder),
            shadowElevation = if (isSelected) 2.dp else 0.dp
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else BrandSecondary
              )

              // Counter badge
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) Color.White.copy(alpha = 0.2f) else BrandCanvas,
                border = if (isSelected) null else BorderStroke(1.dp, BrandBorderLight)
              ) {
                Text(
                  text = count.toString(),
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                  color = if (isSelected) Color.White else BrandSecondary,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
              }
            }
          }
        }
      }
    }

    // 7. Section Header with Meta Info
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .background(BrandCharcoal, CircleShape)
          )
          Text(
            text = strings.todaysTasks,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal,
            letterSpacing = 0.8.sp
          )
        }

        Text(
          text = "$totalRemaining ${strings.remaining}",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = BrandSecondary
        )
      }
    }

    // 8. Tasks List & High-End Empty States
    if (activeTasks.isEmpty() && completedTasks.isEmpty()) {
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .background(BrandCanvas, CircleShape)
                .border(1.dp, BrandBorder, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (searchQuery.isNotBlank()) Icons.Outlined.Search else Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = BrandSecondary,
                modifier = Modifier.size(24.dp)
              )
            }

            Text(
              text = if (searchQuery.isNotBlank()) "No matching tasks found"
              else if (selectedCategory != "All") "No tasks in $selectedCategory"
              else "All clear for today!",
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = BrandCharcoal
            )

            Text(
              text = if (searchQuery.isNotBlank()) "Try searching for a different keyword or clear the search field."
              else if (selectedCategory != "All") "Switch categories or tap + to create a task in $selectedCategory."
              else "Great work! You have completed all scheduled commitments.",
              fontSize = 12.sp,
              color = BrandSecondary,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              lineHeight = 16.sp
            )

            if (searchQuery.isNotBlank() || selectedCategory != "All") {
              Spacer(modifier = Modifier.height(4.dp))
              OutlinedButton(
                onClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  onSearchQueryChanged("")
                  onCategorySelected("All")
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BrandBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = BrandCharcoal
                )
              ) {
                Text(
                  text = strings.resetFilters,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }
    } else {
      // Active tasks
      items(
        items = activeTasks,
        key = { it.id }
      ) { task ->
        TaskCardItem(
          task = task,
          onToggleComplete = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onToggleTaskComplete(task.id)
          },
          onClick = { onTaskClick(task.id) },
          onDelete = { onDeleteTask(task.id) }
        )
      }

      // Completed tasks
      if (completedTasks.isNotEmpty()) {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .background(BrandSecondary.copy(alpha = 0.5f), CircleShape)
            )
            Text(
              text = "${strings.completed} (${completedTasks.size})",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = BrandSecondary,
              letterSpacing = 0.8.sp
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
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onToggleTaskComplete(task.id)
            },
            onClick = { onTaskClick(task.id) },
            onDelete = { onDeleteTask(task.id) }
          )
        }
      }
    }

    // 9. Footer: Dash Divider + "That's everything for today"
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(width = 24.dp, height = 2.5.dp)
            .background(BrandBorder, CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = strings.footerDone,
          fontSize = 12.sp,
          color = BrandSecondary.copy(alpha = 0.8f)
        )
      }
    }
  }
}
}


