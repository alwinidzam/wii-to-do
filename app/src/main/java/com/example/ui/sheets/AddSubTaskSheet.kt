package com.example.ui.sheets

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.ui.theme.OliveSecondary
import com.example.ui.theme.OliveSecondaryContainer

@Composable
fun AddSubTaskSheet(
  parentTaskTitle: String,
  onClose: () -> Unit,
  onAddSubTask: (title: String, effortMinutes: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var subtaskTitle by remember { mutableStateOf("") }
  var selectedEffort by remember { mutableStateOf(30) }

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    color = Color(0xFFFFFFFF),
    shadowElevation = 16.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Drag pill
      Box(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .width(36.dp)
          .height(4.dp)
          .background(Color(0xFFE3E2E0), RoundedCornerShape(2.dp))
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Add Sub-Task",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF060607)
          )
          Text(
            text = "Parent: $parentTaskTitle",
            fontSize = 11.sp,
            color = Color(0xFF747878),
            maxLines = 1
          )
        }

        Surface(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClose() },
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFFF4F3F1)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color(0xFF060607),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Title input
      OutlinedTextField(
        value = subtaskTitle,
        onValueChange = { subtaskTitle = it },
        placeholder = {
          Text(
            text = "e.g., Define semantic token palette for inverted surfaces",
            fontSize = 13.sp,
            color = Color(0xFF747878)
          )
        },
        colors = TextFieldDefaults.colors(
          focusedContainerColor = Color(0xFFFAF9F7),
          unfocusedContainerColor = Color(0xFFFAF9F7),
          focusedIndicatorColor = Color(0xFF060607),
          unfocusedIndicatorColor = Color(0xFFE9E8E6)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("subtask_title_input")
      )

      // Effort estimation chips
      Column {
        Text(
          text = "ESTIMATED EFFORT",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF747878),
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(15, 30, 45, 60).forEach { mins ->
            val isSelected = (mins == selectedEffort)
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { selectedEffort = mins },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) Color(0xFF060607) else Color(0xFFFAF9F7),
              border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9E8E6)) else null
            ) {
              Box(
                modifier = Modifier.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${mins}m",
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else Color(0xFF444748)
                )
              }
            }
          }
        }
      }

      // Action button
      Button(
        onClick = {
          if (subtaskTitle.isNotBlank()) {
            onAddSubTask(subtaskTitle, selectedEffort)
          }
        },
        enabled = subtaskTitle.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("confirm_add_subtask_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF060607),
          disabledContainerColor = Color(0xFFE3E2E0)
        )
      ) {
        Text(
          text = "Add to Task",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}
