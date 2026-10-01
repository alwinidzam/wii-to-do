package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun CreateTaskScreen(
  onClose: () -> Unit,
  onTaskCreated: (
    title: String,
    description: String,
    category: String,
    project: String,
    priority: TaskPriority,
    dueTime: String,
    dueDate: String,
    withAiBreakdown: Boolean
  ) -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var autoBreakdownWithAi by remember { mutableStateOf(true) }
  var selectedCategory by remember { mutableStateOf("Work") }
  var selectedProject by remember { mutableStateOf("Design System Migration") }
  var selectedPriority by remember { mutableStateOf(TaskPriority.HIGH) }
  var selectedDate by remember { mutableStateOf("Today") }
  var selectedTime by remember { mutableStateOf("10:30 AM") }

  val speechRecognizerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
  ) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
      val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
      if (!spokenText.isNullOrBlank()) {
        title = if (title.isBlank()) spokenText else "$title $spokenText"
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
      }
    }
  }

  val startVoiceInput = {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
      putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
      )
      putExtra(RecognizerIntent.EXTRA_PROMPT, "Diktekan judul tugas...")
    }
    try {
      speechRecognizerLauncher.launch(intent)
    } catch (_: Exception) {
      // Speech recognition not available
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
      .padding(horizontal = 20.dp),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(
      top = topInset + 12.dp,
      bottom = 50.dp
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
              onClose()
            },
          shape = RoundedCornerShape(10.dp),
          color = Color.White,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = BrandCharcoal,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "New Task",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal,
            letterSpacing = (-0.02).sp
          )
          Text(
            text = "College • Work • Personal",
            fontSize = 11.sp,
            color = BrandSecondary
          )
        }

        Surface(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              startVoiceInput()
            },
          shape = RoundedCornerShape(10.dp),
          color = BrandPillBg,
          border = BorderStroke(1.dp, BrandBorderLight)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "Voice input",
              tint = BrandCharcoal,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // 2. Title Input
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "TASK TITLE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BrandSecondary,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = {
              Text(
                text = "e.g., Draft dark mode contrast matrix",
                fontSize = 14.sp,
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
            modifier = Modifier
              .fillMaxWidth()
              .testTag("task_title_input")
          )

          if (title.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  autoBreakdownWithAi = !autoBreakdownWithAi
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
              shape = RoundedCornerShape(8.dp),
              color = if (autoBreakdownWithAi) BrandOlive.copy(alpha = 0.12f) else BrandPillBg,
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (autoBreakdownWithAi) BrandOlive.copy(alpha = 0.4f) else BrandBorderLight
              )
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = if (autoBreakdownWithAi) BrandOlive else BrandSecondary,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (autoBreakdownWithAi) "✨ AI Magic Breakdown Enabled" else "Generate subtasks with AI",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (autoBreakdownWithAi) BrandOlive else BrandSecondary
                )
              }
            }
          }
        }
      }
    }

    // 3. Notes / Context
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "CONTEXT & OBJECTIVE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BrandSecondary,
            letterSpacing = 0.8.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = {
              Text(
                text = "Add reference notes, criteria, links, or sub-goals...",
                fontSize = 13.sp,
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
            minLines = 3,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("task_desc_input")
          )
        }
      }
    }

    // 4. Category / Scope Selector
    item {
      Column {
        Text(
          text = "CATEGORY SCOPE",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("College", "Work", "Personal").forEach { cat ->
            val isSelected = (cat == selectedCategory)
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  selectedCategory = cat
                  selectedProject = when (cat) {
                    "College" -> "Database Design Final"
                    "Work" -> "Design System Migration"
                    else -> "Apartment & Moving Checklist"
                  }
                },
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) BrandCharcoal else Color.White,
              border = if (!isSelected) BorderStroke(1.dp, BrandBorder) else null
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = cat,
                  fontSize = 13.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else BrandSecondary
                )
              }
            }
          }
        }
      }
    }

    // 5. Project Workspace
    item {
      Column {
        Text(
          text = "PROJECT WORKSPACE",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        val projectOptions = listOf(
          "Design System Migration",
          "Database Design Final",
          "Fintech Client Brand & Web",
          "Macroeconomics Research Paper",
          "Apartment & Moving Checklist"
        )
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(projectOptions) { proj ->
            val isSelected = (proj == selectedProject)
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  selectedProject = proj
                },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) BrandCharcoal else Color.White,
              border = if (!isSelected) BorderStroke(1.dp, BrandBorder) else null
            ) {
              Text(
                text = proj,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) Color.White else BrandCharcoal,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
              )
            }
          }
        }
      }
    }

    // 6. Priority Selector
    item {
      Column {
        Text(
          text = "PRIORITY LEVEL",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(TaskPriority.LOW, TaskPriority.MED, TaskPriority.HIGH, TaskPriority.BLOCKER).forEach { p ->
            val isSelected = (p == selectedPriority)
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  selectedPriority = p
                },
              shape = RoundedCornerShape(8.dp),
              color = when {
                isSelected && p == TaskPriority.HIGH -> BrandTerracottaBg
                isSelected -> BrandCharcoal
                else -> Color.White
              },
              border = if (isSelected && p == TaskPriority.HIGH) BorderStroke(1.dp, BrandTerracotta)
                       else if (!isSelected) BorderStroke(1.dp, BrandBorder) else null
            ) {
              Box(
                modifier = Modifier.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = p.label,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = when {
                    isSelected && p == TaskPriority.HIGH -> BrandTerracotta
                    isSelected -> Color.White
                    else -> BrandSecondary
                  }
                )
              }
            }
          }
        }
      }
    }

    // 7. Schedule Date & Time
    item {
      val todayChip = remember { "Today (${java.text.SimpleDateFormat("MMM d", java.util.Locale.ENGLISH).format(java.util.Date())})" }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(todayChip, "Tomorrow", "Next Week").forEach { d ->
          val isSelected = (d == selectedDate)
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                selectedDate = d
              },
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) BrandPillBg else Color.White,
            border = BorderStroke(1.dp, if (isSelected) BrandCharcoal else BrandBorder)
          ) {
            Text(
              text = d,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = BrandCharcoal,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }
    }

    // 8. Submit Button
    item {
      val interactionSource = remember { MutableInteractionSource() }
      val isPressed by interactionSource.collectIsPressedAsState()
      val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "submit_scale"
      )

      Button(
        onClick = {
          if (title.isNotBlank()) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onTaskCreated(
              title,
              description,
              selectedCategory,
              selectedProject,
              selectedPriority,
              selectedTime,
              selectedDate,
              autoBreakdownWithAi
            )
          }
        },
        enabled = title.isNotBlank(),
        interactionSource = interactionSource,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .graphicsLayer {
            scaleX = scale
            scaleY = scale
          }
          .testTag("submit_create_task_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = BrandCharcoal,
          disabledContainerColor = BrandBorder
        )
      ) {
        Text(
          text = "Create Task",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}
