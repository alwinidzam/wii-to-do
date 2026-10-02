package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionState
import com.example.ui.theme.AppleLargeTitle
import com.example.ui.theme.AppleSystemBlue
import com.example.ui.theme.AppleSystemGreen
import com.example.ui.theme.AppleSystemIndigo
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandOliveBg
import com.example.ui.theme.BrandSecondary

/**
 * Apple HIG Compliant Focus Timer:
 * - Pure, distraction-free environment (no audio/music noise bloat)
 * - Tactile Haptic Engine feedback on all interactions
 * - 44x44 minimum touch targets
 * - Smooth Apple Watch / iOS Timer circular ring & typography
 */
@Composable
fun ActiveFocusScreen(
  session: FocusSessionState,
  onClose: () -> Unit,
  onToggleTimer: () -> Unit,
  onAddFiveMinutes: () -> Unit,
  onCompleteSprint: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  val mins = session.remainingSeconds / 60
  val secs = session.remainingSeconds % 60
  val timeString = String.format("%02d:%02d", mins, secs)

  val rawProgress = if (session.totalSeconds > 0) {
    (session.totalSeconds - session.remainingSeconds).toFloat() / session.totalSeconds.toFloat()
  } else 0f

  val animatedProgress by animateFloatAsState(
    targetValue = rawProgress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 500),
    label = "focus_progress"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
      .padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(topInset + 12.dp))

    // 1. Apple-style Navigation Bar
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
        color = AppleSystemIndigo.copy(alpha = 0.10f)
      ) {
        Text(
          text = "DEEP FOCUS SPRINT",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = AppleSystemIndigo,
          letterSpacing = 0.6.sp,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
      }

      // Invisible spacer to balance close button
      Spacer(modifier = Modifier.size(44.dp))
    }

    Spacer(modifier = Modifier.height(32.dp))

    // 2. Focused Task Objective
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
      Text(
        text = "ACTIVE SPRINT OBJECTIVE",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = BrandSecondary,
        letterSpacing = 0.8.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = session.taskTitle,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = BrandCharcoal,
        maxLines = 2,
        lineHeight = 28.sp
      )
    }

    Spacer(modifier = Modifier.height(36.dp))

    // 3. Apple-style Circular Countdown Ring
    Box(
      modifier = Modifier.size(240.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(220.dp)) {
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
          fontSize = 48.sp,
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

    Spacer(modifier = Modifier.height(36.dp))

    // 4. Inset Grouped Target Step Card
    if (session.targetSubtask.isNotBlank()) {
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

    Spacer(modifier = Modifier.weight(1f))

    // 5. Apple HIG Ergonomic Bottom Controls (+5 min, Play/Pause, Finish)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 36.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
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
          .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBorder),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = BrandCard)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          tint = BrandCharcoal,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "+5 min",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = BrandCharcoal
        )
      }

      // Play/Pause Action
      Surface(
        modifier = Modifier
          .size(54.dp)
          .clip(RoundedCornerShape(16.dp))
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onToggleTimer()
          },
        shape = RoundedCornerShape(16.dp),
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

      // Finish Sprint Button
      Button(
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onCompleteSprint()
        },
        modifier = Modifier
          .weight(1f)
          .height(54.dp)
          .testTag("complete_focus_sprint_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppleSystemGreen)
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Finish",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}
