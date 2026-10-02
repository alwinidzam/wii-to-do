package com.example.ui.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskPriority
import com.example.ui.theme.OliveSecondary
import com.example.ui.theme.TerracottaAccent

@Composable
fun AddTaskBottomSheet(
  onDismiss: () -> Unit,
  onTaskCreated: (
    title: String,
    description: String,
    category: String,
    project: String,
    priority: TaskPriority,
    dueTime: String,
    dueDate: String
  ) -> Unit,
  modifier: Modifier = Modifier
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Work") }
  var selectedPriority by remember { mutableStateOf(TaskPriority.HIGH) }
  var selectedTime by remember { mutableStateOf("Today 16:00") }

  val categories = listOf("College", "Work", "Personal", "Engineering", "Design")
  val priorities = listOf(
    TaskPriority.LOW,
    TaskPriority.MED,
    TaskPriority.HIGH,
    TaskPriority.BLOCKER
  )
  val bottomInset = androidx.compose.foundation.layout.WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    color = Color(0xFFFFFFFF),
    shadowElevation = 24.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 20.dp + bottomInset)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Top Drag Handle
      Box(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .width(36.dp)
          .height(4.dp)
          .background(Color(0xFFE2DFD9), RoundedCornerShape(2.dp))
      )

      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "New Task",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F1F1F)
          )
        }

        Surface(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onDismiss() },
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFF7F6F3)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color(0xFF1F1F1F),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Title Input
      Column {
        Text(
          text = "TASK TITLE",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF8A8884),
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          placeholder = {
            Text(
              text = "What needs to get done?",
              fontSize = 14.sp,
              color = Color(0xFF8A8884)
            )
          },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFFAF9F7),
            unfocusedContainerColor = Color(0xFFFAF9F7),
            focusedIndicatorColor = Color(0xFF1F1F1F),
            unfocusedIndicatorColor = Color(0xFFF0EEE9)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("add_task_sheet_title_input")
        )
      }

      // Category Tags
      Column {
        Text(
          text = "CATEGORY TAG",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF8A8884),
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = (cat == selectedCategory)
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { selectedCategory = cat },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) Color(0xFF1F1F1F) else Color(0xFFFAF9F7),
              border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0EEE9)) else null
            ) {
              Text(
                text = cat,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF75736E),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      // Priority Level
      Column {
        Text(
          text = "PRIORITY LEVEL",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF8A8884),
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          priorities.forEach { p ->
            val isSelected = (p == selectedPriority)
            val isHighOrBlocker = p == TaskPriority.HIGH || p == TaskPriority.BLOCKER
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { selectedPriority = p },
              shape = RoundedCornerShape(8.dp),
              color = when {
                isSelected && isHighOrBlocker -> Color(0xFFFAECE8)
                isSelected -> Color(0xFF1F1F1F)
                else -> Color(0xFFFAF9F7)
              },
              border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0EEE9)) else null
            ) {
              Box(
                modifier = Modifier.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  if (isHighOrBlocker) {
                    Box(
                      modifier = Modifier
                        .size(6.dp)
                        .background(TerracottaAccent, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                  }
                  Text(
                    text = p.label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = when {
                      isSelected && isHighOrBlocker -> TerracottaAccent
                      isSelected -> Color.White
                      else -> Color(0xFF75736E)
                    }
                  )
                }
              }
            }
          }
        }
      }

      // Notes / Details
      Column {
        Text(
          text = "NOTES & CONTEXT",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF8A8884),
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          placeholder = {
            Text(
              text = "Add reference notes, sub-goals, or links...",
              fontSize = 12.sp,
              color = Color(0xFF8A8884)
            )
          },
          minLines = 2,
          colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFFAF9F7),
            unfocusedContainerColor = Color(0xFFFAF9F7),
            focusedIndicatorColor = Color(0xFF1F1F1F),
            unfocusedIndicatorColor = Color(0xFFF0EEE9)
          ),
          modifier = Modifier.fillMaxWidth()
        )
      }

      // Submit Button
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onTaskCreated(
              title,
              description,
              selectedCategory,
              when (selectedCategory) {
                "College" -> "Database Design Final"
                "Personal" -> "Apartment & Moving Checklist"
                else -> "Design System Migration"
              },
              selectedPriority,
              selectedTime,
              "Today (${java.text.SimpleDateFormat("MMM d", java.util.Locale.ENGLISH).format(java.util.Date())})"
            )
          }
        },
        enabled = title.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("confirm_create_task_bottom_sheet_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF1F1F1F),
          disabledContainerColor = Color(0xFFE2DFD9)
        )
      ) {
        Text(
          text = "Add Task",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}
