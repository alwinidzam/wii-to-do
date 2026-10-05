package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionState
import com.example.data.model.TaskItem
import com.example.ui.theme.AppleSystemBlue
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandOliveBg
import com.example.ui.theme.BrandOliveBorder
import com.example.ui.theme.BrandPillBg
import com.example.ui.theme.BrandSecondary
import java.util.Locale

@Composable
fun FloatingFocusMiniPlayer(
  session: FocusSessionState?,
  tasks: List<TaskItem> = emptyList(),
  onExpandClick: () -> Unit,
  onToggleTimer: () -> Unit,
  onQuickComplete: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.975f else 1f,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "pill_press_scale"
  )

  AnimatedVisibility(
    visible = session != null && !session.isDismissed,
    enter = slideInVertically(
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    ) { it } + fadeIn(tween(250)),
    exit = slideOutVertically(
      animationSpec = tween(200, easing = FastOutSlowInEasing)
    ) { it } + fadeOut(tween(180)),
    modifier = modifier
  ) {
    if (session == null) return@AnimatedVisibility

    // Real dynamic subtask progress calculation
    val relatedTask = remember(session.taskId, tasks) {
      session.taskId?.let { id -> tasks.find { it.id == id } }
        ?: tasks.find { it.title.equals(session.taskTitle, ignoreCase = true) }
    }

    val (subtaskProgressText, activeSubtaskTitle) = remember(relatedTask, session) {
      if (relatedTask != null && relatedTask.subtasks.isNotEmpty()) {
        val total = relatedTask.subtasks.size
        val done = relatedTask.subtasks.count { it.isCompleted }
        val nextIncomplete = relatedTask.subtasks.firstOrNull { !it.isCompleted }?.title
        val progressLabel = "Subtugas ${done + 1}/$total"
        Pair(progressLabel, nextIncomplete ?: session.targetSubtask.ifBlank { relatedTask.title })
      } else {
        val badge = session.courseBadge ?: relatedTask?.courseName ?: session.mode.label
        Pair(badge, session.taskTitle)
      }
    }

    val progressFraction = remember(session.remainingSeconds, session.totalSeconds) {
      if (session.totalSeconds <= 0) 1f
      else (1f - (session.remainingSeconds.toFloat() / session.totalSeconds.toFloat())).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
      targetValue = progressFraction,
      animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
      label = "countdown_ring_progress"
    )

    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer {
          scaleX = scale
          scaleY = scale
        }
        .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.18f))
        .clip(RoundedCornerShape(20.dp))
        .clickable(
          interactionSource = interactionSource,
          indication = null
        ) {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onExpandClick()
        },
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      border = BorderStroke(1.25.dp, BrandBorder)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // 1. Progress Dial + Monospace Countdown
        Box(
          modifier = Modifier.size(42.dp),
          contentAlignment = Alignment.Center
        ) {
          Canvas(
            modifier = Modifier
              .fillMaxSize()
              .padding(2.dp)
          ) {
            val strokeWidth = 3.5.dp.toPx()
            // Track
            drawCircle(
              color = BrandBorderLight,
              style = Stroke(width = strokeWidth)
            )
            // Progress Arc
            drawArc(
              color = if (session.isCompleted) BrandOlive else AppleSystemBlue,
              startAngle = -90f,
              sweepAngle = 360f * animatedProgress,
              useCenter = false,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
          }

          val mins = session.remainingSeconds / 60
          val secs = session.remainingSeconds % 60
          Text(
            text = String.format(Locale.US, "%02d:%02d", mins, secs),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal,
            letterSpacing = (-0.2).sp
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 2. Info Hierarchy (Course/Subtask Badge + Active Title)
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(5.dp),
              color = BrandOliveBg,
              border = BorderStroke(0.75.dp, BrandOliveBorder)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "⚡ ${subtaskProgressText.uppercase()}",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = BrandOlive,
                  letterSpacing = 0.4.sp
                )
              }
            }
            if (relatedTask != null && !relatedTask.courseName.isNullOrBlank() && subtaskProgressText.startsWith("Subtugas")) {
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "• ${relatedTask.courseName}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = BrandSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = activeSubtaskTitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandCharcoal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3. Tactile Action Controls (Play/Pause, Complete, Dismiss)
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Play/Pause
          Surface(
            modifier = Modifier
              .size(34.dp)
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
                imageVector = if (session.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (session.isRunning) "Pause Timer" else "Resume Timer",
                tint = BrandCharcoal,
                modifier = Modifier.size(17.dp)
              )
            }
          }

          // Quick Complete (1-Tap)
          Surface(
            modifier = Modifier
              .size(34.dp)
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onQuickComplete()
              },
            shape = RoundedCornerShape(10.dp),
            color = BrandOliveBg,
            border = BorderStroke(1.dp, BrandOliveBorder)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Quick Complete",
                tint = BrandOlive,
                modifier = Modifier.size(17.dp)
              )
            }
          }

          // Clean Dismiss / Stop
          Surface(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onDismiss()
              },
            shape = CircleShape,
            color = Color.Transparent
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss Mini Player",
                tint = BrandSecondary.copy(alpha = 0.7f),
                modifier = Modifier.size(15.dp)
              )
            }
          }
        }
      }
    }
  }
}
