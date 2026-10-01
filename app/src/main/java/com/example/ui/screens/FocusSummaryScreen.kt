package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OliveSecondary
import com.example.ui.theme.OliveSecondaryContainer

@Composable
fun FocusSummaryScreen(
  onReturnHome: () -> Unit,
  onTakeBreak: () -> Unit,
  onContinueSprint: () -> Unit,
  session: com.example.data.model.FocusSessionState? = null,
  onSaveReflection: ((energy: String, note: String) -> Unit)? = null,
  modifier: Modifier = Modifier,
  onShareMilestoneClick: () -> Unit = {}
) {
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var selectedEnergy by remember { mutableStateOf("⚡ High Flow") }
  var noteText by remember { mutableStateOf("") }

  val minutesSpent = if (session != null && session.totalSeconds > 0) {
    val logged = session.totalSeconds - session.remainingSeconds
    if (logged > 0) logged / 60 else session.totalSeconds / 60
  } else 25
  val taskTitle = session?.taskTitle ?: "Sprint Completed"
  val stepTarget = session?.targetSubtask ?: "Active Objective Accomplished"

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFAF9F7))
      .padding(horizontal = 20.dp),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(
      top = topInset + 16.dp,
      bottom = 40.dp
    ),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Success Icon & Celebration Title
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Surface(
          modifier = Modifier.size(64.dp),
          shape = CircleShape,
          color = OliveSecondaryContainer
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Success",
              tint = Color(0xFF596751),
              modifier = Modifier.size(32.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Sprint Finished. Great Flow!",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF060607)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "+120 XP earned • $taskTitle",
          fontSize = 13.sp,
          color = OliveSecondary,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1
        )
      }
    }

    // 2. 2x2 Architectural Metrics Grid
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricBox(
            title = "Focus Time",
            value = "$minutesSpent min",
            icon = Icons.Default.Schedule,
            modifier = Modifier.weight(1f)
          )
          MetricBox(
            title = "Flow Score",
            value = "${session?.flowScore ?: 92}%",
            icon = Icons.Default.Speed,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricBox(
            title = "Soundscape",
            value = session?.soundscape ?: "Calm Focus",
            icon = Icons.Default.Bolt,
            modifier = Modifier.weight(1f)
          )
          MetricBox(
            title = "Status",
            value = "Sprint Done",
            icon = Icons.Default.Check,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 3. Accomplished in this Block
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFEFEF))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "ACCOMPLISHED IN THIS BLOCK",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF747878),
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = OliveSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = stepTarget,
              fontSize = 12.sp,
              color = Color(0xFF060607)
            )
          }
        }
      }
    }

    // 4. Energy Check Selector
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "HOW IS YOUR ENERGY?",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF747878),
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("⚡ High Flow", "☕ Balanced", "💤 Fatigued").forEach { opt ->
            val isSelected = (opt == selectedEnergy)
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable { selectedEnergy = opt },
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) Color(0xFF060607) else Color(0xFFFFFFFF),
              border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)) else null
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = opt,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else Color(0xFF444748)
                )
              }
            }
          }
        }
      }
    }

    // 5. Reflection Note
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFEFEF))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "QUICK REFLECTION (OPTIONAL)",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF747878),
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = {
              Text(
                text = "What worked well? Any friction points?",
                fontSize = 12.sp,
                color = Color(0xFF747878)
              )
            },
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // 6. Action Buttons
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = {
            onSaveReflection?.invoke(selectedEnergy, noteText)
            onContinueSprint()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060607))
        ) {
          Text(
            text = "Continue Next Sprint (15m)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        Button(
          onClick = {
            onSaveReflection?.invoke(selectedEnergy, noteText)
            onShareMilestoneClick()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF384639))
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Share Milestone to Socials ✦",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }

        OutlinedButton(
          onClick = {
            onSaveReflection?.invoke(selectedEnergy, noteText)
            onTakeBreak()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "Take a 5-Min Break",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF060607)
          )
        }

        OutlinedButton(
          onClick = {
            onSaveReflection?.invoke(selectedEnergy, noteText)
            onReturnHome()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "Return to Dashboard",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF060607)
          )
        }
      }
    }
  }
}

@Composable
fun MetricBox(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFFFFFFFF),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFEFEF))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          color = Color(0xFF747878)
        )
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = OliveSecondary,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF060607)
      )
    }
  }
}
