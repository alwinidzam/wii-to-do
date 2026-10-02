package com.example.ui.sheets

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicCourse
import com.example.data.model.AcademicCourseDefaults
import com.example.data.model.TaskPriority
import com.example.ui.components.CourseSelectionStrip
import com.example.ui.components.LinearBorderHairline
import com.example.ui.components.LinearHighOrange
import com.example.ui.components.LinearPrioritySignal
import com.example.ui.components.LinearUrgentRed
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta
import com.example.ui.theme.HapticEngine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Super High-End Apple HIG & Linear-Grade Task Composer Sheet.
 * Features:
 * - Real ModalBottomSheet with authentic swipe-down gesture to dismiss (with spring physics)
 * - Seamless frameless Title & Notes editor (Things 3 / Linear pattern)
 * - Sleek horizontal property pill dock: [ 📅 Deadline ] [ 🎓 Matkul ] [ 📶 Priority ]
 * - Collapsible, non-intrusive inline drawers with refined, non-screaming color palettes
 * - High-End Create Task action button with tactile Taptic Engine feedback
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    dueDate: String,
    courseName: String?
  ) -> Unit,
  modifier: Modifier = Modifier,
  courses: List<AcademicCourse> = AcademicCourseDefaults.PRESET_COURSES,
  onAddNewCourse: (AcademicCourse) -> Unit = {}
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val coroutineScope = rememberCoroutineScope()

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  var selectedCategoryIndex by remember { mutableStateOf(0) }
  val categories = listOf("College", "Work", "Personal")

  var selectedCourseName by remember { mutableStateOf<String?>("Sistem Operasi") }

  var selectedPriorityIndex by remember { mutableStateOf(1) } // Default Medium
  val priorities = listOf(
    TaskPriority.LOW,
    TaskPriority.MED,
    TaskPriority.HIGH,
    TaskPriority.BLOCKER
  )

  // Deadline state
  var selectedDateLabel by remember { mutableStateOf("Hari Ini") }
  var selectedFormattedDate by remember { mutableStateOf("Today") }
  var isTomorrow by remember { mutableStateOf(false) }
  var selectedTimeStr by remember { mutableStateOf("23:59") }

  // Drawers expansion state (collapsible to avoid visual clutter)
  var isDeadlineDrawerOpen by remember { mutableStateOf(false) }
  var isCourseDrawerOpen by remember { mutableStateOf(false) }

  val focusRequester = remember { FocusRequester() }
  val isCollege = categories[selectedCategoryIndex].equals("College", ignoreCase = true)

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  val dismissSheet: () -> Unit = {
    coroutineScope.launch {
      HapticEngine.selection(context, haptic)
      try {
        sheetState.hide()
      } finally {
        onDismiss()
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White,
    dragHandle = {
      // Apple HIG Grabber with vertical swipe detector
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
              if (dragAmount > 18f) {
                dismissSheet()
              }
            }
          }
          .padding(top = 10.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .width(36.dp)
            .height(4.5.dp)
            .clip(RoundedCornerShape(2.25.dp))
            .background(Color(0xFFD1D5DB))
        )
      }
    },
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    windowInsets = WindowInsets.ime
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .padding(start = 20.dp, end = 20.dp, bottom = 18.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 1. Top Bar: Category Scope Selector + Close Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .pointerInput(Unit) {
            detectVerticalDragGestures { _, dragAmount ->
              if (dragAmount > 20f) {
                dismissSheet()
              }
            }
          },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Category Scope Pill (1-tap cycling)
        val curCat = categories[selectedCategoryIndex]
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isCollege) Color(0xFFEEF2FF) else BrandCanvas,
          border = BorderStroke(0.75.dp, if (isCollege) Color(0xFFC7D2FE) else LinearBorderHairline),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              HapticEngine.selection(context, haptic)
              selectedCategoryIndex = (selectedCategoryIndex + 1) % categories.size
            }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(
              imageVector = if (isCollege) Icons.Outlined.School else Icons.Outlined.Category,
              contentDescription = null,
              tint = if (isCollege) Color(0xFF4F46E5) else BrandSecondary,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = if (curCat == "College") "Kuliah (College) ▾" else "$curCat ▾",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (isCollege) Color(0xFF4F46E5) else BrandCharcoal
            )
          }
        }

        // Close Button
        Surface(
          modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .clickable { dismissSheet() },
          shape = CircleShape,
          color = BrandCanvas,
          border = BorderStroke(0.75.dp, LinearBorderHairline)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = BrandSecondary,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      // 2. Seamless Frameless Editor: Title & Notes
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Title Input
        BasicTextField(
          value = title,
          onValueChange = { title = it },
          textStyle = TextStyle(
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandCharcoal,
            letterSpacing = (-0.01).sp
          ),
          cursorBrush = SolidColor(BrandCharcoal),
          singleLine = true,
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
          decorationBox = { innerTextField ->
            if (title.isEmpty()) {
              Text(
                text = if (isCollege) "Nama tugas kuliah..." else "Apa yang ingin dikerjakan?",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9CA3AF),
                letterSpacing = (-0.01).sp
              )
            }
            innerTextField()
          },
          modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .testTag("quick_composer_title_input")
        )

        // Notes Input (effortless inline note-taking)
        BasicTextField(
          value = description,
          onValueChange = { description = it },
          textStyle = TextStyle(
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Normal,
            color = BrandSecondary,
            lineHeight = 18.sp
          ),
          cursorBrush = SolidColor(BrandCharcoal),
          minLines = 1,
          maxLines = 3,
          decorationBox = { innerTextField ->
            if (description.isEmpty()) {
              Text(
                text = "Tambah catatan, kriteria, link referensi...",
                fontSize = 13.5.sp,
                color = Color(0xFFA1A1AA)
              )
            }
            innerTextField()
          },
          modifier = Modifier.fillMaxWidth()
        )
      }

      // 3. Sleek Horizontal Contextual Property Dock
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Pill A: Deadline Pill (Toggles Deadline Drawer)
        val isDeadlineActive = isDeadlineDrawerOpen
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isDeadlineActive) BrandCharcoal else Color.White,
          border = BorderStroke(0.75.dp, if (isDeadlineActive) BrandCharcoal else LinearBorderHairline),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              HapticEngine.selection(context, haptic)
              isDeadlineDrawerOpen = !isDeadlineDrawerOpen
              if (isDeadlineDrawerOpen) isCourseDrawerOpen = false
            }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.Schedule,
              contentDescription = null,
              tint = if (isDeadlineActive) Color.White else if (selectedTimeStr == "23:59") Color(0xFFD97706) else BrandSecondary,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = "$selectedDateLabel • $selectedTimeStr",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium,
              color = if (isDeadlineActive) Color.White else BrandCharcoal
            )
          }
        }

        // Pill B: Course Pill (When Category == College, Toggles Course Drawer)
        if (isCollege) {
          val isCourseActive = isCourseDrawerOpen
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isCourseActive) Color(0xFF4F46E5) else Color(0xFFEEF2FF),
            border = BorderStroke(0.75.dp, if (isCourseActive) Color(0xFF4F46E5) else Color(0xFFC7D2FE)),
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable {
                HapticEngine.selection(context, haptic)
                isCourseDrawerOpen = !isCourseDrawerOpen
                if (isCourseDrawerOpen) isDeadlineDrawerOpen = false
              }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.School,
                contentDescription = null,
                tint = if (isCourseActive) Color.White else Color(0xFF4F46E5),
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = selectedCourseName ?: "Pilih Matkul",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isCourseActive) Color.White else Color(0xFF4F46E5)
              )
            }
          }
        }

        // Pill C: Priority Pill (1-tap cycling)
        val curPriority = priorities[selectedPriorityIndex]
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = when (curPriority) {
            TaskPriority.HIGH, TaskPriority.BLOCKER -> LinearUrgentRed.copy(alpha = 0.08f)
            TaskPriority.MED -> LinearHighOrange.copy(alpha = 0.08f)
            else -> BrandCanvas
          },
          border = BorderStroke(0.75.dp, LinearBorderHairline),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              HapticEngine.selection(context, haptic)
              selectedPriorityIndex = (selectedPriorityIndex + 1) % priorities.size
            }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            LinearPrioritySignal(priority = curPriority)
            Text(
              text = when (curPriority) {
                TaskPriority.LOW -> "Low"
                TaskPriority.MED -> "Medium"
                TaskPriority.HIGH -> "High"
                TaskPriority.BLOCKER -> "Urgent"
                TaskPriority.NONE -> "Normal"
              },
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Medium,
              color = when (curPriority) {
                TaskPriority.HIGH, TaskPriority.BLOCKER -> LinearUrgentRed
                TaskPriority.MED -> LinearHighOrange
                else -> BrandSecondary
              }
            )
          }
        }
      }

      // 4. Collapsible Contextual Drawers
      // 4A: Course Drawer (When College & Open)
      AnimatedVisibility(
        visible = isCollege && isCourseDrawerOpen,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        CourseSelectionStrip(
          courses = courses,
          selectedCourseName = selectedCourseName,
          onSelectCourse = {
            selectedCourseName = it
            isCourseDrawerOpen = false
          },
          onAddNewCourse = onAddNewCourse,
          modifier = Modifier.padding(vertical = 4.dp)
        )
      }

      // 4B: Deadline Drawer (Clean, Non-Cluttering Selector)
      AnimatedVisibility(
        visible = isDeadlineDrawerOpen,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(BrandCanvas, RoundedCornerShape(12.dp))
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Date Options
          val datePresets = remember {
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("MMM d", Locale.ENGLISH)
            val todayStr = "Today (${sdf.format(cal.time)})"
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val tomStr = "Tomorrow (${sdf.format(cal.time)})"
            listOf(
              Triple("Hari Ini", todayStr, false),
              Triple("Besok", tomStr, true),
              Triple("Senin Depan", "Next Week", false)
            )
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            datePresets.forEach { (shortLabel, fullStr, isTom) ->
              val isSelected = selectedDateLabel.contains(shortLabel, ignoreCase = true)
              Surface(
                shape = RoundedCornerShape(7.dp),
                color = if (isSelected) BrandCharcoal else Color.White,
                border = if (isSelected) null else BorderStroke(0.75.dp, LinearBorderHairline),
                modifier = Modifier
                  .clip(RoundedCornerShape(7.dp))
                  .clickable {
                    HapticEngine.selection(context, haptic)
                    selectedDateLabel = shortLabel
                    selectedFormattedDate = fullStr
                    isTomorrow = isTom
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

            // Calendar Picker
            Surface(
              shape = RoundedCornerShape(7.dp),
              color = Color.White,
              border = BorderStroke(0.75.dp, LinearBorderHairline),
              modifier = Modifier
                .clip(RoundedCornerShape(7.dp))
                .clickable {
                  HapticEngine.selection(context, haptic)
                  val cal = Calendar.getInstance()
                  DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                      val chosenCal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
                      val sdfShort = SimpleDateFormat("d MMM", Locale("id", "ID"))
                      val sdfFull = SimpleDateFormat("MMM d", Locale.ENGLISH)
                      selectedDateLabel = sdfShort.format(chosenCal.time)
                      selectedFormattedDate = sdfFull.format(chosenCal.time)
                      isTomorrow = false
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

          // Time Options (Tasteful non-screaming palette)
          val timePresets = listOf(
            Pair("23:59 Malam", "23:59"),
            Pair("17:00 Sore", "17:00"),
            Pair("13:00 Siang", "13:00"),
            Pair("09:00 Pagi", "09:00")
          )

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
                shape = RoundedCornerShape(7.dp),
                color = when {
                  isSelected && isEndOfDay -> BrandCharcoal
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
                  .clip(RoundedCornerShape(7.dp))
                  .clickable {
                    HapticEngine.selection(context, haptic)
                    selectedTimeStr = rawTime
                  }
              ) {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = when {
                    isSelected -> Color.White
                    isEndOfDay -> Color(0xFFB45309)
                    else -> BrandCharcoal
                  },
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.5.dp)
                )
              }
            }

            // Custom Time Picker
            Surface(
              shape = RoundedCornerShape(7.dp),
              color = Color.White,
              border = BorderStroke(0.75.dp, LinearBorderHairline),
              modifier = Modifier
                .clip(RoundedCornerShape(7.dp))
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
                      selectedTimeStr = "$hourStr:$minStr"
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

      Spacer(modifier = Modifier.height(2.dp))

      // 5. Bottom Action Row: Gesture Dismissal Hint + Create Task Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Gesture dismissal cue
        Text(
          text = "↓ Geser ke bawah untuk tutup",
          fontSize = 10.5.sp,
          color = Color(0xFF9CA3AF),
          fontWeight = FontWeight.Medium
        )

        val isReady = title.trim().isNotBlank()
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val btnScale by animateFloatAsState(
          targetValue = if (isPressed) 0.94f else 1.0f,
          animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
          label = "create_btn_scale"
        )

        Button(
          onClick = {
            if (isReady) {
              HapticEngine.success(context, haptic)
              onTaskCreated(
                title.trim(),
                description.trim(),
                categories[selectedCategoryIndex],
                if (isCollege) (selectedCourseName ?: "Kuliah") else "Personal",
                priorities[selectedPriorityIndex],
                selectedTimeStr,
                "$selectedDateLabel • $selectedTimeStr",
                if (isCollege) selectedCourseName else null
              )
            }
          },
          enabled = isReady,
          interactionSource = interactionSource,
          shape = RoundedCornerShape(11.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BrandCharcoal,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFF3F4F6),
            disabledContentColor = Color(0xFF9CA3AF)
          ),
          modifier = Modifier
            .height(38.dp)
            .graphicsLayer {
              scaleX = btnScale
              scaleY = btnScale
            }
            .testTag("quick_composer_create_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "Create Task",
              fontSize = 12.5.sp,
              fontWeight = FontWeight.SemiBold
            )
            Icon(
              imageVector = Icons.Default.ArrowUpward,
              contentDescription = null,
              modifier = Modifier.size(13.dp)
            )
          }
        }
      }
    }
  }
}
