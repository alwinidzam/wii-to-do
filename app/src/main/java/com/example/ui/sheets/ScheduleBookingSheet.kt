package com.example.ui.sheets

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OliveSecondary
import com.example.ui.theme.OliveSecondaryContainer

@Composable
fun ScheduleBookingSheet(
  startTime: String,
  endTime: String,
  onClose: () -> Unit,
  onBookSlot: (title: String, category: String, isDeepWork: Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  var title by remember { mutableStateOf("Database Migration Planning") }
  var category by remember { mutableStateOf("Engineering") }
  var isDeepWork by remember { mutableStateOf(true) }

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
            text = "Schedule Time Block",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF060607)
          )
          Text(
            text = "$startTime – $endTime • Free Gap Available",
            fontSize = 12.sp,
            color = OliveSecondary,
            fontWeight = FontWeight.Medium
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

      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        placeholder = { Text("What will you work on?", color = Color(0xFF747878)) },
        colors = TextFieldDefaults.colors(
          focusedContainerColor = Color(0xFFFAF9F7),
          unfocusedContainerColor = Color(0xFFFAF9F7),
          focusedIndicatorColor = Color(0xFF060607),
          unfocusedIndicatorColor = Color(0xFFE9E8E6)
        ),
        modifier = Modifier.fillMaxWidth()
      )

      // Category selector
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("College", "Work Studio", "Engineering", "Personal").forEach { cat ->
          val isSelected = (cat == category)
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .clickable { category = cat },
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) Color(0xFF060607) else Color(0xFFFAF9F7),
            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9E8E6)) else null
          ) {
            Box(
              modifier = Modifier.padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = cat,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF444748)
              )
            }
          }
        }
      }

      // Deep work focus block toggle
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .clickable { isDeepWork = !isDeepWork },
        shape = RoundedCornerShape(10.dp),
        color = if (isDeepWork) OliveSecondaryContainer else Color(0xFFFAF9F7),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDeepWork) OliveSecondary else Color(0xFFE9E8E6))
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = if (isDeepWork) Color(0xFF596751) else Color(0xFF747878),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Designate as Deep Focus Block",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF060607)
            )
            Text(
              text = "Mutes pings, starts Pomodoro countdown on schedule",
              fontSize = 11.sp,
              color = Color(0xFF747878)
            )
          }
        }
      }

      Button(
        onClick = { onBookSlot(title, category, isDeepWork) },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060607))
      ) {
        Text(
          text = "Book $startTime – $endTime",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }
}
