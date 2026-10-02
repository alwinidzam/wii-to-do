package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicCourse
import com.example.data.model.AcademicCourseDefaults
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandOliveBg
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta
import com.example.ui.theme.BrandTerracottaBg
import com.example.ui.theme.HapticEngine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * High-End Academic Course Selector for College Tasks.
 * Allows instant 1-tap course tagging with color badge & custom course creation.
 */
@Composable
fun CourseSelectionStrip(
  courses: List<AcademicCourse>,
  selectedCourseName: String?,
  onSelectCourse: (String?) -> Unit,
  onAddNewCourse: (AcademicCourse) -> Unit,
  modifier: Modifier = Modifier,
  onOpenCourseManager: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  var isAddingNew by remember { mutableStateOf(false) }
  var newCourseInput by remember { mutableStateOf("") }

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "MATA KULIAH (COURSE)",
          fontSize = 10.5.sp,
          fontWeight = FontWeight.Bold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )
        if (onOpenCourseManager != null) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = BrandCanvas,
            border = BorderStroke(0.75.dp, LinearBorderHairline),
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .clickable {
                HapticEngine.selection(context, haptic)
                onOpenCourseManager()
              }
          ) {
            Text(
              text = "Kelola Matkul",
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold,
              color = BrandOlive,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
      if (selectedCourseName != null) {
        Text(
          text = "Hapus Pilihan",
          fontSize = 10.5.sp,
          fontWeight = FontWeight.Medium,
          color = BrandSecondary,
          modifier = Modifier.clickable {
            HapticEngine.selection(context, haptic)
            onSelectCourse(null)
          }
        )
      }
    }

    LazyRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      items(courses) { course ->
        val isSelected = course.name.equals(selectedCourseName, ignoreCase = true)
        val courseColor = Color(course.colorHex)

        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              HapticEngine.selection(context, haptic)
              onSelectCourse(if (isSelected) null else course.name)
            },
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) courseColor else courseColor.copy(alpha = 0.10f),
          border = BorderStroke(0.75.dp, if (isSelected) courseColor else courseColor.copy(alpha = 0.35f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (isSelected) Color.White.copy(alpha = 0.25f) else courseColor.copy(alpha = 0.20f)
            ) {
              Text(
                text = course.code,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) Color.White else courseColor,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
            Text(
              text = course.name,
              fontSize = 11.5.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else BrandCharcoal
            )
          }
        }
      }

      // 1-Tap Add Custom Course Chip / Inline Input
      item {
        if (!isAddingNew) {
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable {
                HapticEngine.selection(context, haptic)
                isAddingNew = true
              },
            shape = RoundedCornerShape(8.dp),
            color = BrandCanvas,
            border = BorderStroke(0.75.dp, LinearBorderHairline)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(13.dp))
              Text(text = "+ Matkul Baru", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = BrandSecondary)
            }
          }
        } else {
          // Inline expandable input
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BrandCharcoal),
            modifier = Modifier.height(32.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              BasicTextField(
                value = newCourseInput,
                onValueChange = { newCourseInput = it },
                singleLine = true,
                textStyle = TextStyle(fontSize = 11.5.sp, color = BrandCharcoal, fontWeight = FontWeight.Medium),
                decorationBox = { innerTextField ->
                  if (newCourseInput.isEmpty()) {
                    Text("Nama matkul...", fontSize = 11.5.sp, color = BrandSecondary.copy(alpha = 0.6f))
                  }
                  innerTextField()
                },
                modifier = Modifier.width(100.dp)
              )
              IconButton(
                onClick = {
                  if (newCourseInput.isNotBlank()) {
                    val code = AcademicCourseDefaults.generateCode(newCourseInput)
                    val colorHex = AcademicCourseDefaults.PALETTE[courses.size % AcademicCourseDefaults.PALETTE.size]
                    val newCourse = AcademicCourse(
                      id = "course_${System.currentTimeMillis()}",
                      code = code,
                      name = newCourseInput.trim(),
                      colorHex = colorHex
                    )
                    onAddNewCourse(newCourse)
                    onSelectCourse(newCourse.name)
                    newCourseInput = ""
                    isAddingNew = false
                    HapticEngine.success(context, haptic)
                  }
                },
                modifier = Modifier.size(20.dp)
              ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Simpan", tint = LinearDoneGreen, modifier = Modifier.size(15.dp))
              }
              IconButton(
                onClick = { isAddingNew = false; newCourseInput = "" },
                modifier = Modifier.size(20.dp)
              ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Batal", tint = BrandSecondary, modifier = Modifier.size(13.dp))
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Super High-End Apple HIG & Linear-Style Deadline & Due-Date Selector.
 * Offers 1-tap relative dates, universal 23:59 assignment deadline chip, and full calendar picker.
 */
@Composable
fun FluidDeadlineComposer(
  selectedDateLabel: String,
  selectedTimeStr: String,
  onDateSelected: (label: String, formattedDate: String, isTomorrow: Boolean) -> Unit,
  onTimeSelected: (timeStr: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current

  // Date calculation presets
  val dateOptions = remember {
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("MMM d", Locale.ENGLISH)
    val todayShort = "Hari Ini"
    val todayFull = "Today (${sdf.format(cal.time)})"

    cal.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowShort = "Besok"
    val tomorrowFull = "Tomorrow (${sdf.format(cal.time)})"

    val nextMonCal = Calendar.getInstance()
    val dayOfWeek = nextMonCal.get(Calendar.DAY_OF_WEEK)
    val daysUntilMonday = if (dayOfWeek == Calendar.SUNDAY) 1 else (Calendar.MONDAY + 7 - dayOfWeek)
    nextMonCal.add(Calendar.DAY_OF_YEAR, daysUntilMonday)
    val nextMonShort = "Senin Depan"
    val nextMonFull = "Mon (${sdf.format(nextMonCal.time)})"

    listOf(
      Triple(todayShort, todayFull, false),
      Triple(tomorrowShort, tomorrowFull, true),
      Triple(nextMonShort, nextMonFull, false)
    )
  }

  val timePresets = listOf(
    Pair("09:00 Pagi", "09:00"),
    Pair("13:00 Siang", "13:00"),
    Pair("17:00 Sore", "17:00"),
    Pair("23:59 Malam (Deadline)", "23:59")
  )

  Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
    // 1. Header with live deadline badge
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "TENGGAT WAKTU (DEADLINE)",
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        color = BrandSecondary,
        letterSpacing = 0.8.sp
      )

      // Live Apple HIG Pill
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFFEF3C7),
        border = BorderStroke(0.5.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = Color(0xFFB45309),
            modifier = Modifier.size(11.dp)
          )
          Text(
            text = "$selectedDateLabel • $selectedTimeStr",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFB45309)
          )
        }
      }
    }

    // 2. Tier 1: Relative Date Chips + Custom Calendar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      dateOptions.forEach { (shortLabel, fullStr, isTom) ->
        val isSelected = selectedDateLabel.contains(shortLabel, ignoreCase = true) ||
            selectedDateLabel.contains(fullStr, ignoreCase = true) ||
            (shortLabel == "Hari Ini" && selectedDateLabel.contains("Today", ignoreCase = true)) ||
            (shortLabel == "Besok" && selectedDateLabel.contains("Tomorrow", ignoreCase = true))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) BrandCharcoal else Color.White,
          border = if (isSelected) null else BorderStroke(0.75.dp, LinearBorderHairline),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              HapticEngine.selection(context, haptic)
              onDateSelected(shortLabel, fullStr, isTom)
            }
        ) {
          Text(
            text = shortLabel,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else BrandCharcoal,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
          )
        }
      }

      // Custom Calendar Picker
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = BrandCanvas,
        border = BorderStroke(0.75.dp, LinearBorderHairline),
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable {
            HapticEngine.selection(context, haptic)
            val cal = Calendar.getInstance()
            DatePickerDialog(
              context,
              { _, year, month, dayOfMonth ->
                val chosenCal = Calendar.getInstance().apply {
                  set(year, month, dayOfMonth)
                }
                val sdfShort = SimpleDateFormat("d MMM", Locale("id", "ID"))
                val sdfFull = SimpleDateFormat("MMM d", Locale.ENGLISH)
                val label = sdfShort.format(chosenCal.time)
                val full = sdfFull.format(chosenCal.time)
                onDateSelected(label, full, false)
                HapticEngine.success(context, haptic)
              },
              cal.get(Calendar.YEAR),
              cal.get(Calendar.MONTH),
              cal.get(Calendar.DAY_OF_MONTH)
            ).show()
          }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(imageVector = Icons.Outlined.CalendarToday, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(11.dp))
          Text(text = "Kalender...", fontSize = 11.sp, color = BrandSecondary, fontWeight = FontWeight.Medium)
        }
      }
    }

    // 3. Tier 2: Time Presets + Custom Time Picker
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      timePresets.forEach { (label, rawTime) ->
        val isSelected = selectedTimeStr == rawTime
        val isEndOfDay = rawTime == "23:59"

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = when {
            isSelected -> BrandCharcoal
            isEndOfDay -> Color(0xFFFEF3C7)
            else -> Color.White
          },
          border = when {
            isSelected -> null
            isEndOfDay -> BorderStroke(0.75.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
            else -> BorderStroke(0.75.dp, LinearBorderHairline)
          },
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              HapticEngine.selection(context, haptic)
              onTimeSelected(rawTime)
            }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = when {
                isSelected -> Color.White
                isEndOfDay -> Color(0xFFB45309)
                else -> BrandSecondary
              }
            )
          }
        }
      }

      // Custom Time Picker
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = BrandCanvas,
        border = BorderStroke(0.75.dp, LinearBorderHairline),
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable {
            HapticEngine.selection(context, haptic)
            val parts = selectedTimeStr.split(":")
            val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 23
            val initialMin = parts.getOrNull(1)?.toIntOrNull() ?: 59

            TimePickerDialog(
              context,
              { _, hourOfDay, minute ->
                val hourStr = hourOfDay.toString().padStart(2, '0')
                val minStr = minute.toString().padStart(2, '0')
                onTimeSelected("$hourStr:$minStr")
                HapticEngine.success(context, haptic)
              },
              initialHour,
              initialMin,
              true
            ).show()
          }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.5.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(imageVector = Icons.Outlined.Schedule, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(11.dp))
          Text(text = "Jam...", fontSize = 11.sp, color = BrandSecondary, fontWeight = FontWeight.Medium)
        }
      }
    }
  }
}
