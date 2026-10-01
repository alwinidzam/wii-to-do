package com.example.ui.screens

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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FocusSessionState
import com.example.ui.theme.OliveSecondary
import com.example.ui.theme.OliveSecondaryContainer

@Composable
fun ActiveFocusScreen(
  session: FocusSessionState,
  soundscapePlaying: Boolean,
  onClose: () -> Unit,
  onToggleTimer: () -> Unit,
  onAddFiveMinutes: () -> Unit,
  onCompleteSprint: () -> Unit,
  onToggleSoundscape: () -> Unit,
  modifier: Modifier = Modifier
) {
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  val mins = session.remainingSeconds / 60
  val secs = session.remainingSeconds % 60
  val timeString = String.format("%02d:%02d", mins, secs)

  val progress = if (session.totalSeconds > 0) {
    (session.totalSeconds - session.remainingSeconds).toFloat() / session.totalSeconds.toFloat()
  } else 0f

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFAF9F7))
      .padding(horizontal = 20.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(topInset + 12.dp))

    // 1. Top Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(10.dp))
          .clickable { onClose() },
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFEFEF))
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Exit Focus",
            tint = Color(0xFF060607),
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Soundscape controller pill
      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .clickable { onToggleSoundscape() },
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE3E2E0))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (soundscapePlaying) Icons.Default.GraphicEq else Icons.Default.Headphones,
            contentDescription = null,
            tint = if (soundscapePlaying) OliveSecondary else Color(0xFF747878),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = session.soundscape,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF060607)
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = OliveSecondaryContainer
      ) {
        Text(
          text = "SPRINT #2",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF596751),
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 2. Main Objective
    Text(
      text = "Active Objective",
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      color = Color(0xFF747878),
      letterSpacing = 0.5.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = session.taskTitle,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF060607),
      maxLines = 2
    )

    Spacer(modifier = Modifier.height(28.dp))

    // 3. Circular Countdown Timer
    Box(
      modifier = Modifier.size(220.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.size(200.dp)) {
        val strokeWidth = 8.dp.toPx()
        // Background track
        drawCircle(
          color = Color(0xFFE9E8E6),
          radius = (size.minDimension - strokeWidth) / 2,
          style = Stroke(width = strokeWidth)
        )
        // Animated progress arc
        drawArc(
          color = Color(0xFF060607),
          startAngle = -90f,
          sweepAngle = progress * 360f,
          useCenter = false,
          style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = timeString,
          fontSize = 44.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF060607),
          letterSpacing = (-0.02).sp
        )
        Text(
          text = if (session.isRunning) "DEEP FLOW" else "PAUSED",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = if (session.isRunning) OliveSecondary else Color(0xFF747878),
          letterSpacing = 1.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 4. Target Subtask Card
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      color = Color(0xFFFFFFFF),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFEFEF)),
      shadowElevation = 2.dp
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CURRENT TARGET STEP",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF747878),
            letterSpacing = 0.8.sp
          )
          Text(
            text = "~10m effort",
            fontSize = 11.sp,
            color = Color(0xFF747878)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(20.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFFF4F3F1))
              .border(1.5.dp, Color(0xFFD8D8D9), RoundedCornerShape(4.dp))
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = session.targetSubtask,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF060607),
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Next: Export Figma token matrix JSON (~5m)",
          fontSize = 11.sp,
          color = Color(0xFF747878)
        )
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // 5. Bottom Controls (+5m, Play/Pause, Done)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 36.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedButton(
        onClick = { onAddFiveMinutes() },
        modifier = Modifier
          .weight(1f)
          .height(52.dp),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          tint = Color(0xFF060607),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "+5 min",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF060607)
        )
      }

      Surface(
        modifier = Modifier
          .size(52.dp)
          .clip(RoundedCornerShape(12.dp))
          .clickable { onToggleTimer() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF060607)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = if (session.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = "Toggle",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Button(
        onClick = { onCompleteSprint() },
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
          .testTag("complete_focus_sprint_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = OliveSecondary)
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Finish",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}
