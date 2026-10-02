package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionState
import com.example.data.model.ScheduleCommitment
import com.example.ui.components.DateStripSelector
import com.example.ui.components.WiiBrandLogo
import com.example.ui.components.getTodayIndex
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

@Composable
fun ScheduleScreen(
  scheduleItems: List<ScheduleCommitment>,
  viewMode: String, // "Day" vs "Week"
  selectedDayIndex: Int,
  activeFocusSession: FocusSessionState?,
  onViewModeChanged: (String) -> Unit,
  onDaySelected: (Int) -> Unit,
  onScheduleSlotClick: (String, String) -> Unit,
  onFocusMiniPlayerClick: () -> Unit,
  onToggleTimer: () -> Unit,
  modifier: Modifier = Modifier,
  onCompleteSprint: () -> Unit = {}
) {
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var deepWorkOnly by remember { mutableStateOf(false) }

  val todayIdx = remember { getTodayIndex() }
  val currentDayFormatted = remember(selectedDayIndex) {
    val calendar = Calendar.getInstance()
    val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysFromMonday = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - Calendar.MONDAY
    calendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday + selectedDayIndex)
    val sdf = SimpleDateFormat("EEEE, MMM d", Locale.ENGLISH)
    sdf.format(calendar.time)
  }
  val dayTitle = if (selectedDayIndex == todayIdx) "Today" else currentDayFormatted.split(",").firstOrNull() ?: "Day"
  val displayedSchedule = scheduleItems.filter { !deepWorkOnly || it.isDeepWork }
  val dateSubtitle = "$currentDayFormatted • ${displayedSchedule.size} commitments"

  Box(modifier = modifier.fillMaxSize().background(BrandCanvas)) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Fixed Header with Logo & Segmented Control (pinned at top)
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BrandCanvas
      ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 20.dp, end = 20.dp, top = topInset + 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          WiiBrandLogo(size = 30.dp)
          Column {
            Text(
              text = "wii to do",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal,
              letterSpacing = (-0.02).sp
            )
            Text(
              text = "schedule",
              fontSize = 10.sp,
              color = BrandSecondary,
              fontWeight = FontWeight.Medium,
              letterSpacing = 0.5.sp
            )
          }
        }

        // Segmented Day/Week Toggle
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color.White,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Row(modifier = Modifier.padding(3.dp)) {
            val modes = listOf("Day", "Week")
            modes.forEach { mode ->
              val isSelected = (mode == viewMode)
              Surface(
                modifier = Modifier
                  .testTag("schedule_mode_$mode")
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onViewModeChanged(mode)
                  },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) BrandCharcoal else Color.Transparent
              ) {
                Text(
                  text = mode,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                  color = if (isSelected) Color.White else BrandSecondary,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    }

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

      // 2. Day Header & Title
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = dayTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal,
                letterSpacing = (-0.02).sp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Box(modifier = Modifier.size(5.dp).background(BrandOlive, CircleShape))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (deepWorkOnly) "Deep Flow Filter" else "Sprint Day",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandOlive,
                letterSpacing = 0.5.sp
              )
            }
            Text(
              text = dateSubtitle,
              fontSize = 12.sp,
              color = BrandSecondary
            )
          }

          Surface(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                deepWorkOnly = !deepWorkOnly
              },
            shape = RoundedCornerShape(10.dp),
            color = if (deepWorkOnly) BrandCharcoal else Color.White,
            border = BorderStroke(1.dp, BrandBorder)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Filter",
                tint = if (deepWorkOnly) Color.White else BrandCharcoal,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }

      // 3. 7-Day Horizontal Date Strip
      item {
        DateStripSelector(
          selectedIndex = selectedDayIndex,
          onDaySelected = onDaySelected
        )
      }

      if (viewMode == "Week") {
        // Weekly Flow Heatmap Card
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, BrandBorder)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = BrandOlive,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "Weekly Flow",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandCharcoal
                  )
                }

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = BrandOliveBg,
                  border = BorderStroke(1.dp, BrandOliveBorder)
                ) {
                  Text(
                    text = "64% Achieved",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandOlive,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // 3-Stat metrics
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                listOf(
                  Triple("Planned", "28", "tasks"),
                  Triple("Completed", "18", "done"),
                  Triple("Focus Hours", "23.0h", "")
                ).forEach { (label, stat, unit) ->
                  Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = BrandPillBg,
                    border = BorderStroke(1.dp, BrandBorderLight)
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandSecondary
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = if (unit.isNotEmpty()) "$stat $unit" else stat,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandCharcoal
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Bar distribution
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
              ) {
                val heights = listOf(65, 48, 85, 80, 40, 20, 15)
                val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

                dayLabels.forEachIndexed { idx, label ->
                  val isToday = (idx == 3)
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                  ) {
                    Box(
                      modifier = Modifier
                        .width(22.dp)
                        .height((heights[idx] * 0.45).dp)
                        .background(
                          if (isToday) BrandCharcoal else BrandOlive.copy(alpha = if (idx < 3) 0.85f else 0.25f),
                          RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = label,
                      fontSize = 11.sp,
                      color = if (isToday) BrandCharcoal else BrandSecondary,
                      fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 4. Momentum / Cognitive Peak Banner
      item {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          color = Color.White,
          shadowElevation = 1.dp,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(10.dp),
                color = BrandOliveBg,
                border = BorderStroke(1.dp, BrandOliveBorder)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = BrandOlive,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Today's Cognitive Peak",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandCharcoal
                )
                Text(
                  text = "Focus Sprint #2 active • 75m logged",
                  fontSize = 11.sp,
                  color = BrandSecondary
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = BrandPillBg,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Text(
                text = "84% Flow",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandCharcoal,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // 5. Timeline Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "SCHEDULE TIMELINE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = BrandSecondary,
            letterSpacing = 0.8.sp
          )
          Text(
            text = "$currentDayFormatted • Focus Track",
            fontSize = 11.sp,
            color = BrandOlive,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // 6. Timeline Items
      items(displayedSchedule, key = { it.id }) { item ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top
        ) {
          // Time label column
          Column(
            modifier = Modifier.width(52.dp),
            horizontalAlignment = Alignment.End
          ) {
            Text(
              text = item.startTime,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (item.isCurrentFocus) BrandCharcoal else BrandSecondary
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          // Vertical node line
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .background(
                  color = when {
                    item.isCurrentFocus -> BrandCharcoal
                    item.isCompleted -> BrandOlive
                    else -> BrandBorder
                  },
                  shape = CircleShape
                )
            )
            Box(
              modifier = Modifier
                .width(1.5.dp)
                .height(72.dp)
                .background(BrandBorderLight)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          // Event Card
          val cardBorder = if (item.isCurrentFocus) BorderStroke(1.dp, BrandOlive.copy(alpha = 0.5f)) else BorderStroke(1.dp, BrandBorder)
          Surface(
            modifier = Modifier
              .weight(1f)
              .shadow(if (item.isCurrentFocus) 3.dp else 1.dp, RoundedCornerShape(12.dp))
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onScheduleSlotClick(item.startTime, item.endTime)
              },
            shape = RoundedCornerShape(12.dp),
            color = if (item.isCompleted) Color.White.copy(alpha = 0.8f) else Color.White,
            border = cardBorder
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${item.startTime} - ${item.endTime}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (item.isCurrentFocus) BrandOlive else BrandSecondary
                )

                if (item.isCurrentFocus) {
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandOliveBg,
                    border = BorderStroke(1.dp, BrandOliveBorder)
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Box(modifier = Modifier.size(5.dp).background(BrandOlive, CircleShape))
                      Text(
                        text = "FOCUS SPRINT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandOlive
                      )
                    }
                  }
                } else if (item.isCompleted) {
                  Text(
                    text = "Done",
                    fontSize = 11.sp,
                    color = BrandOlive,
                    fontWeight = FontWeight.Medium
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (item.isCompleted) BrandSecondary else BrandCharcoal,
                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
              )

              if (item.subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = item.subtitle,
                  fontSize = 11.sp,
                  color = BrandSecondary,
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      // Tap-to-allocate empty slot trigger
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onScheduleSlotClick("16:00", "16:30")
            },
          shape = RoundedCornerShape(12.dp),
          color = Color.White.copy(alpha = 0.6f),
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              tint = BrandSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Schedule task at 16:00 (Free Gap)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = BrandCharcoal
            )
          }
        }
      }
    }
  }

  // 7. Persistent Floating Focus Mini-Player
    if (activeFocusSession != null) {
      Surface(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 88.dp, start = 20.dp, end = 20.dp)
          .fillMaxWidth()
          .shadow(16.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.15f))
          .clip(RoundedCornerShape(16.dp))
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onFocusMiniPlayerClick()
          },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Circular Countdown Dial
          Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Surface(
              modifier = Modifier.fillMaxSize(),
              shape = CircleShape,
              color = BrandPillBg,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {}
            val mins = activeFocusSession.remainingSeconds / 60
            val secs = activeFocusSession.remainingSeconds % 60
            Text(
              text = String.format("%02d:%02d", mins, secs),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "⚡ DEEP WORK SPRINT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BrandOlive,
                letterSpacing = 0.5.sp
              )
              Text(
                text = " • Step 2/4",
                fontSize = 10.sp,
                color = BrandSecondary
              )
            }
            Text(
              text = activeFocusSession.taskTitle,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = BrandCharcoal,
              maxLines = 1
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  onToggleTimer()
                },
              shape = RoundedCornerShape(10.dp),
              color = BrandPillBg,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = if (activeFocusSession.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = "Toggle Timer",
                  tint = BrandCharcoal,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Surface(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  onCompleteSprint()
                },
              shape = RoundedCornerShape(10.dp),
              color = BrandOliveBg,
              border = BorderStroke(1.dp, BrandOliveBorder)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Complete Sprint",
                  tint = BrandOlive,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}
