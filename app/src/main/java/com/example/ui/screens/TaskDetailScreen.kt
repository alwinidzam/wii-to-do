package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
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
fun TaskDetailScreen(
  task: TaskItem,
  onBackClick: () -> Unit,
  onToggleTaskComplete: () -> Unit,
  onToggleSubTask: (String) -> Unit,
  onAddSubTaskClick: () -> Unit,
  onAiBreakdownClick: () -> Unit = {},
  isAiGenerating: Boolean = false,
  onStartFocusClick: () -> Unit,
  onRescheduleClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
      .padding(horizontal = 20.dp),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(
      top = topInset + 12.dp,
      bottom = 40.dp + bottomInset
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

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggleTaskComplete()
              },
            shape = RoundedCornerShape(10.dp),
            color = if (task.isCompleted) BrandOlive else Color.White,
            border = if (!task.isCompleted) BorderStroke(1.dp, BrandBorder) else null
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (task.isCompleted) Color.White else BrandCharcoal,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (task.isCompleted) "Completed" else "Mark Done",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (task.isCompleted) Color.White else BrandCharcoal
              )
            }
          }

          Surface(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BrandBorder)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = BrandSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }

    // 2. Project & Badges
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = BrandOliveBg,
          border = BorderStroke(1.dp, BrandOliveBorder)
        ) {
          Text(
            text = task.project,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandOlive,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = BrandTagBg,
          border = BorderStroke(1.dp, BrandBorderLight)
        ) {
          Text(
            text = task.category,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = BrandCharcoal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        if (task.priority == TaskPriority.HIGH) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = BrandTerracottaBg,
            border = BorderStroke(1.dp, BrandTerracotta.copy(alpha = 0.5f))
          ) {
            Text(
              text = "High Priority",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = BrandTerracotta,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }
    }

    // 3. Task Title
    item {
      Text(
        text = task.title,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = BrandCharcoal,
        letterSpacing = (-0.02).sp,
        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
      )
    }

    // 4. Sub-Tasks Checklist Section
    item {
      val doneCount = task.subtasks.count { it.isCompleted }
      val total = task.subtasks.size
      val progress = if (total > 0) doneCount.toFloat() / total.toFloat() else 0f

      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Sub-Tasks",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "$doneCount of $total completed",
                fontSize = 12.sp,
                color = BrandSecondary
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable(enabled = !isAiGenerating) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAiBreakdownClick()
                  },
                shape = RoundedCornerShape(8.dp),
                color = if (isAiGenerating) BrandPillBg.copy(alpha = 0.5f) else BrandOlive.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, BrandOlive.copy(alpha = 0.35f))
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  if (isAiGenerating) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(12.dp),
                      strokeWidth = 1.5.dp,
                      color = BrandOlive
                    )
                  } else {
                    Icon(
                      imageVector = Icons.Default.AutoAwesome,
                      contentDescription = "AI Magic Breakdown",
                      tint = BrandOlive,
                      modifier = Modifier.size(13.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isAiGenerating) "Thinking..." else "AI Breakdown",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandOlive
                  )
                }
              }

              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddSubTaskClick()
                  },
                shape = RoundedCornerShape(8.dp),
                color = BrandPillBg,
                border = BorderStroke(1.dp, BrandBorderLight)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = BrandCharcoal,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Add",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandCharcoal
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(2.5.dp)),
            color = BrandOlive,
            trackColor = BrandBorderLight
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Subtask list
          task.subtasks.forEach { sub ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  onToggleSubTask(sub.id)
                }
                .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(RoundedCornerShape(5.dp))
                  .background(if (sub.isCompleted) BrandOlive else Color.Transparent)
                  .border(
                    width = if (sub.isCompleted) 0.dp else 1.5.dp,
                    color = if (sub.isCompleted) Color.Transparent else BrandBorder,
                    shape = RoundedCornerShape(5.dp)
                  ),
                contentAlignment = Alignment.Center
              ) {
                if (sub.isCompleted) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Text(
                text = sub.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (sub.isCompleted) BrandSecondary else BrandCharcoal,
                textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f)
              )

              if (sub.completedAt != null) {
                Text(
                  text = "Done ${sub.completedAt}",
                  fontSize = 11.sp,
                  color = BrandOlive,
                  fontWeight = FontWeight.Medium
                )
              } else {
                Text(
                  text = "~${sub.effortMinutes}m",
                  fontSize = 11.sp,
                  color = BrandSecondary
                )
              }
            }
          }
        }
      }
    }

    // 5. Context & Notes Section
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Context & Objective",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = task.description.ifEmpty { "No extra notes specified for this task." },
            fontSize = 13.sp,
            color = BrandCharcoal.copy(alpha = 0.8f),
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // External References
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = BrandPillBg,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Link,
                  contentDescription = null,
                  tint = BrandCharcoal,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Figma Design Token Matrix",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = BrandCharcoal
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = BrandPillBg,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.OpenInNew,
                  contentDescription = null,
                  tint = BrandCharcoal,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "WCAG 2.1 Contrast Tool",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = BrandCharcoal
                )
              }
            }
          }
        }
      }
    }

    // 6. Schedule & Calendar Info Banner
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = BrandPillBg,
        border = BorderStroke(1.dp, BrandBorderLight)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = BrandCharcoal,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Scheduled today at 16:00 – 16:30",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = BrandCharcoal
            )
            Text(
              text = "Google Calendar Synced • Reminder 10m before",
              fontSize = 11.sp,
              color = BrandSecondary
            )
          }
        }
      }
    }

    // 7. Action Footer Buttons
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onRescheduleClick()
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, BrandBorder),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandCharcoal)
        ) {
          Text(
            text = "Reschedule",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onStartFocusClick()
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("start_focus_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal)
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Start Focus (15m)",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
          )
        }
      }
    }
  }
}
