package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionState
import com.example.data.model.FocusTimerMode
import com.example.data.model.SubTask
import com.example.ui.theme.AppleSystemBlue
import com.example.ui.theme.AppleSystemGreen
import com.example.ui.theme.AppleSystemIndigo
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandSecondary

/**
 * High-End Apple HIG & Linear-Compliant Focus Timer:
 * - Pure, distraction-free environment (no audio/music noise bloat)
 * - Live interactive subtask checklist linked to active task
 * - 1-tap mode switcher: Pomodoro (25m), Deep Sprint (50m), Open Flow (Count-up)
 * - Tactile Haptic Engine feedback on all interactions
 * - Dual finish options: Finish Sprint vs Selesaikan Tugas & Sprint
 * - 44x44 minimum touch targets & smooth circular ring typography
 */
@Composable
fun ActiveFocusScreen(
  session: FocusSessionState,
  subtasks: List<SubTask> = emptyList(),
  onToggleSubTask: ((String) -> Unit)? = null,
  onClose: () -> Unit,
  onToggleTimer: () -> Unit,
  onAddFiveMinutes: () -> Unit,
  onSwitchMode: ((FocusTimerMode) -> Unit)? = null,
  onCompleteSprint: (markTaskDone: Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  val scrollState = rememberScrollState()

  val mins = session.remainingSeconds / 60
  val secs = session.remainingSeconds % 60
  val timeString = String.format("%02d:%02d", mins, secs)

  val rawProgress = if (session.mode == FocusTimerMode.FLOW_OPEN) {
    1f
  } else if (session.totalSeconds > 0) {
    (session.totalSeconds - session.remainingSeconds).toFloat() / session.totalSeconds.toFloat()
  } else 0f

  val animatedProgress by animateFloatAsState(
    targetValue = rawProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 500),
    label = "focus_progress"
  )

  val completedSubtasks = subtasks.count { it.isCompleted }
  val totalSubtasks = subtasks.size
  val subtaskProgressFraction = if (totalSubtasks > 0) completedSubtasks.toFloat() / totalSubtasks.toFloat() else 0f

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(topInset + 12.dp))

    // 1. Apple-style Navigation Bar & Mode Switcher
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Close button with 44x44 minimum touch target
      Surface(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClose()
          },
        shape = CircleShape,
        color = BrandCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Exit Focus",
            tint = BrandCharcoal,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Minimalist Focus Pill Badge
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = AppleSystemIndigo.copy(alpha = 0.10f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppleSystemIndigo.copy(alpha = 0.20f))
      ) {
        Text(
          text = session.mode.label.uppercase(),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = AppleSystemIndigo,
          letterSpacing = 0.6.sp,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
      }

      // Spacer balancing close button
      Spacer(modifier = Modifier.size(44.dp))
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Mode Selector Segmented Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(BrandCard, RoundedCornerShape(12.dp))
        .border(1.dp, BrandBorder, RoundedCornerShape(12.dp))
        .padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      FocusTimerMode.entries.forEach { mode ->
        val isSelected = session.mode == mode
        val chipBg by animateColorAsState(
          targetValue = if (isSelected) BrandCharcoal else Color.Transparent,
          label = "mode_chip_bg"
        )
        val textColor by animateColorAsState(
          targetValue = if (isSelected) Color.White else BrandSecondary,
          label = "mode_chip_text"
        )

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(chipBg)
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onSwitchMode?.invoke(mode)
            }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = when (mode) {
              FocusTimerMode.POMODORO_25 -> "25m"
              FocusTimerMode.SPRINT_50 -> "50m"
              FocusTimerMode.FLOW_OPEN -> "Flow"
            },
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // 2. Focused Task Objective & Course Pill
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
    ) {
      if (!session.courseBadge.isNullOrBlank()) {
        val courseColor = session.courseColorHex?.let { Color(it) } ?: AppleSystemIndigo
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = courseColor.copy(alpha = 0.12f),
          border = androidx.compose.foundation.BorderStroke(1.dp, courseColor.copy(alpha = 0.25f)),
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          Text(
            text = session.courseBadge,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = courseColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      } else {
        Text(
          text = "ACTIVE SPRINT OBJECTIVE",
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
      }

      Text(
        text = session.taskTitle,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = BrandCharcoal,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 26.sp
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 3. Apple-style Circular Countdown Ring
    Box(
      modifier = Modifier.size(230.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(210.dp)) {
        val strokeWidth = 10.dp.toPx()
        // Background track (iOS System Gray 5)
        drawCircle(
          color = Color(0xFFE5E5EA),
          radius = (size.minDimension - strokeWidth) / 2,
          style = Stroke(width = strokeWidth)
        )
        // Active progress arc
        drawArc(
          color = if (session.isRunning) AppleSystemBlue else BrandSecondary,
          startAngle = -90f,
          sweepAngle = animatedProgress * 360f,
          useCenter = false,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = timeString,
          fontSize = 44.sp,
          fontWeight = FontWeight.Bold,
          color = BrandCharcoal,
          letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (session.isRunning) AppleSystemGreen.copy(alpha = 0.12f) else BrandBorder
        ) {
          Text(
            text = if (session.isRunning) "RUNNING" else "PAUSED",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (session.isRunning) AppleSystemGreen else BrandSecondary,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 4. Live Interactive Subtask Checklist Card
    if (subtasks.isNotEmpty()) {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BrandCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "SUBTASKS PROGRESS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = BrandSecondary,
              letterSpacing = 0.8.sp
            )
            Text(
              text = "$completedSubtasks / $totalSubtasks",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          LinearProgressIndicator(
            progress = { subtaskProgressFraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = AppleSystemGreen,
            trackColor = BrandBorder
          )

          Spacer(modifier = Modifier.height(12.dp))

          subtasks.forEach { sub ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  onToggleSubTask?.invoke(sub.id)
                }
                .padding(vertical = 8.dp, horizontal = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (sub.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (sub.isCompleted) AppleSystemGreen else BrandSecondary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = sub.title,
                fontSize = 13.sp,
                fontWeight = if (sub.isCompleted) FontWeight.Normal else FontWeight.Medium,
                color = if (sub.isCompleted) BrandSecondary else BrandCharcoal,
                textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
              )
              if (sub.effortMinutes > 0) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = BrandCanvas,
                  modifier = Modifier.padding(start = 6.dp)
                ) {
                  Text(
                    text = "${sub.effortMinutes}m",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }
    } else if (session.targetSubtask.isNotBlank()) {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = BrandCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "CURRENT MILESTONE STEP",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BrandSecondary,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .background(AppleSystemBlue, CircleShape)
            )
            Text(
              text = session.targetSubtask,
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = BrandCharcoal,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // 5. Apple HIG Ergonomic Controls
    // Row 1: +5 min, Play/Pause, Selesai Sprint
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // +5 min Pill
      OutlinedButton(
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onAddFiveMinutes()
        },
        modifier = Modifier
          .weight(1f)
          .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = BrandCard)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          tint = BrandCharcoal,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "+5 min",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = BrandCharcoal
        )
      }

      // Play/Pause Action
      Surface(
        modifier = Modifier
          .size(52.dp)
          .clip(RoundedCornerShape(14.dp))
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onToggleTimer()
          },
        shape = RoundedCornerShape(14.dp),
        color = BrandCharcoal
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = if (session.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = "Toggle Timer",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      // Finish Sprint Button (logs sprint without completing task)
      OutlinedButton(
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onCompleteSprint(false)
        },
        modifier = Modifier
          .weight(1f)
          .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = BrandCard)
      ) {
        Text(
          text = "Selesai Sprint",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = BrandCharcoal
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Row 2: Direct "Selesaikan Tugas Sekarang" Button (Completes both task & sprint)
    Button(
      onClick = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onCompleteSprint(true)
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("complete_focus_sprint_button"),
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(containerColor = AppleSystemGreen)
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Selesaikan Tugas Sekarang",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.height(36.dp))
  }
}
