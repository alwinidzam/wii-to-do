package com.example.ui.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicCourse
import com.example.data.model.AcademicCourseDefaults
import com.example.data.model.TaskPriority
import com.example.ui.components.CourseSelectionStrip
import com.example.ui.components.FluidDeadlineComposer
import com.example.ui.components.LinearBorderHairline
import com.example.ui.components.LinearHighOrange
import com.example.ui.components.LinearPrioritySignal
import com.example.ui.components.LinearUrgentRed
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.HapticEngine

/**
 * Linear-Grade Quick Composer Sheet for instant 0ms task capture.
 * Features:
 * - Docked directly above software keyboard (`imePadding`)
 * - Auto-focus on title input
 * - Dynamic Academic Course selector when Category is "College"
 * - Full Apple HIG & Linear Fluid Deadline & Time Composer (Today, Tomorrow, Weekend, Calendar, 23:59, etc.)
 * - 1-tap property cycling chips for Priority and Category
 * - Apple HIG Grabber handle
 * - Tactile Haptic feedback on task submission
 */
@OptIn(ExperimentalLayoutApi::class)
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

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var showDescriptionInput by remember { mutableStateOf(false) }

  var selectedCategoryIndex by remember { mutableStateOf(0) }
  val categories = listOf("College", "Work", "Personal", "Engineering")

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

  val focusRequester = remember { FocusRequester() }
  val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  val isCollege = categories[selectedCategoryIndex].equals("College", ignoreCase = true)

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .imePadding(),
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    color = Color.White,
    shadowElevation = 16.dp,
    border = BorderStroke(0.75.dp, LinearBorderHairline)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          start = 18.dp,
          end = 18.dp,
          top = 12.dp,
          bottom = 14.dp + bottomInset
        )
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // 1. Apple HIG Grabber Pill
      Box(
        modifier = Modifier
          .align(Alignment.CenterHorizontally)
          .width(36.dp)
          .height(4.5.dp)
          .clip(RoundedCornerShape(2.25.dp))
          .background(LinearBorderHairline)
      )

      // 2. Header: Title & Close
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "New Task",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = BrandCharcoal,
          letterSpacing = (-0.01).sp
        )

        IconButton(
          onClick = {
            HapticEngine.selection(context, haptic)
            onDismiss()
          },
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Dismiss",
            tint = BrandSecondary,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // 3. Clean Single-Line Title Input
      TextField(
        value = title,
        onValueChange = { title = it },
        placeholder = {
          Text(
            text = if (isCollege) "Nama tugas kuliah..." else "What needs to be done?",
            fontSize = 15.sp,
            color = BrandSecondary.copy(alpha = 0.55f),
            fontWeight = FontWeight.Normal
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
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
          onDone = {
            if (title.isNotBlank()) {
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
          }
        ),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .focusRequester(focusRequester)
          .testTag("quick_composer_title_input")
      )

      // Optional Description Input
      AnimatedVisibility(visible = showDescriptionInput) {
        TextField(
          value = description,
          onValueChange = { description = it },
          placeholder = {
            Text(
              text = "Add details, rubrik penilaian, catatan...",
              fontSize = 13.sp,
              color = BrandSecondary.copy(alpha = 0.5f)
            )
          },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = BrandCanvas,
            unfocusedContainerColor = BrandCanvas,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = BrandCharcoal,
            unfocusedTextColor = BrandCharcoal
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(bottom = 4.dp)
        )
      }

      // 4. Property Chips: Category & Priority
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Category Chip (1-tap cycling)
        val curCat = categories[selectedCategoryIndex]
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isCollege) Color(0xFF4F46E5).copy(alpha = 0.10f) else BrandCanvas,
          border = BorderStroke(0.75.dp, if (isCollege) Color(0xFF4F46E5).copy(alpha = 0.4f) else LinearBorderHairline),
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              HapticEngine.selection(context, haptic)
              selectedCategoryIndex = (selectedCategoryIndex + 1) % categories.size
            }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.Category,
              contentDescription = null,
              tint = if (isCollege) Color(0xFF4F46E5) else BrandSecondary,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = if (curCat == "College") "🎓 $curCat (Kuliah)" else curCat,
              fontSize = 11.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (isCollege) Color(0xFF4F46E5) else BrandCharcoal
            )
          }
        }

        // Priority Chip
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
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            LinearPrioritySignal(priority = curPriority)
            Text(
              text = when (curPriority) {
                TaskPriority.LOW -> "Low Priority"
                TaskPriority.MED -> "Medium Priority"
                TaskPriority.HIGH -> "High Priority"
                TaskPriority.BLOCKER -> "Urgent"
                TaskPriority.NONE -> "No Priority"
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

        // Toggle Notes / Description
        if (!showDescriptionInput) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = BrandCanvas,
            border = BorderStroke(0.75.dp, LinearBorderHairline),
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable {
                HapticEngine.selection(context, haptic)
                showDescriptionInput = true
              }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.Notes,
                contentDescription = null,
                tint = BrandSecondary,
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = "+ Catatan",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = BrandSecondary
              )
            }
          }
        }
      }

      // 5. Dynamic Course Selection (Revealed smoothly when Category = College)
      AnimatedVisibility(
        visible = isCollege,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        CourseSelectionStrip(
          courses = courses,
          selectedCourseName = selectedCourseName,
          onSelectCourse = { selectedCourseName = it },
          onAddNewCourse = onAddNewCourse,
          modifier = Modifier.padding(vertical = 2.dp)
        )
      }

      // 6. Fluid Deadline Composer (Relative dates, 23:59 assignment preset, calendar & time pickers)
      FluidDeadlineComposer(
        selectedDateLabel = selectedDateLabel,
        selectedTimeStr = selectedTimeStr,
        onDateSelected = { label, formatted, isTom ->
          selectedDateLabel = label
          selectedFormattedDate = formatted
          isTomorrow = isTom
        },
        onTimeSelected = { timeStr ->
          selectedTimeStr = timeStr
        }
      )

      // 7. Bottom Action: Create Task Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val isReady = title.trim().isNotBlank()
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
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BrandCharcoal,
            contentColor = Color.White,
            disabledContainerColor = LinearBorderHairline,
            disabledContentColor = BrandSecondary.copy(alpha = 0.5f)
          ),
          modifier = Modifier
            .height(38.dp)
            .testTag("quick_composer_create_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "Create Task",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
            Icon(
              imageVector = Icons.Default.ArrowUpward,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }
  }
}
