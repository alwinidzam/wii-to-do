package com.example.ui.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicCourse
import com.example.data.model.AcademicCourseDefaults
import com.example.data.model.AcademicSemester
import com.example.ui.components.LinearBorderHairline
import com.example.ui.theme.AppleSystemBlue
import com.example.ui.theme.AppleSystemGreen
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta
import com.example.ui.theme.BrandTerracottaBg
import com.example.ui.theme.HapticEngine
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Super High-End Academic Semester & Course Management Bottom Sheet.
 * Provides complete personal academic configuration:
 * 1. Multi-Semester management (Semester 1..8, Ganjil/Genap, active semester switcher).
 * 2. Course / Mata Kuliah CRUD per semester (Code, Name, Lecturer, SKS, Schedule, Room, Color).
 * 3. Workload & SKS tracking per semester.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicCourseManagerBottomSheet(
  semesters: List<AcademicSemester>,
  courses: List<AcademicCourse>,
  activeSemesterId: String,
  onSetActiveSemester: (String) -> Unit,
  onAddCourse: (AcademicCourse) -> Unit,
  onUpdateCourse: (AcademicCourse) -> Unit,
  onDeleteCourse: (String) -> Unit,
  onAddSemester: (Int, String, String, Double, Int) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val coroutineScope = rememberCoroutineScope()

  var selectedSemesterId by remember {
    mutableStateOf(activeSemesterId.ifEmpty { semesters.firstOrNull { it.isCurrent }?.id ?: semesters.firstOrNull()?.id ?: "sem_5" })
  }
  var showAddCourseForm by remember { mutableStateOf(false) }

  // New course form fields
  var courseName by remember { mutableStateOf("") }
  var courseCode by remember { mutableStateOf("") }
  var lecturer by remember { mutableStateOf("") }
  var sks by remember { mutableIntStateOf(3) }
  var scheduleDay by remember { mutableStateOf("Senin") }
  var scheduleTime by remember { mutableStateOf("08:00 - 10:30") }
  var classRoom by remember { mutableStateOf("Lab Komputer") }
  var selectedColorHex by remember { mutableStateOf(0xFF10B981) }

  val currentSemester = semesters.find { it.id == selectedSemesterId }
  val currentSemesterCourses = courses.filter { it.semesterId == selectedSemesterId }
  val totalSksInSemester = currentSemesterCourses.sumOf { it.sks }

  val dismissSheet = {
    coroutineScope.launch {
      sheetState.hide()
      onDismiss()
    }
  }

  ModalBottomSheet(
    onDismissRequest = { dismissSheet() },
    sheetState = sheetState,
    containerColor = Color.White,
    scrimColor = Color(0x66000000),
    dragHandle = {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 10.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(width = 36.dp, height = 4.dp)
            .background(BrandBorder, RoundedCornerShape(2.dp))
        )
      }
    },
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .imePadding()
        .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
      // 1. Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = AppleSystemGreen.copy(alpha = 0.12f),
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = AppleSystemGreen,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Column {
            Text(
              text = "Manajemen Akademik",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal
            )
            Text(
              text = "Semester & Mata Kuliah Pribadi",
              fontSize = 11.5.sp,
              color = BrandSecondary
            )
          }
        }

        IconButton(onClick = { dismissSheet() }, modifier = Modifier.size(32.dp)) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = BrandSecondary)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Horizontal Semester Selector Strip
      Text(
        text = "PILIH SEMESTER",
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        color = BrandSecondary,
        letterSpacing = 0.8.sp
      )
      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(semesters, key = { it.id }) { sem ->
          val isSelected = (sem.id == selectedSemesterId)
          val isCurrentActive = (sem.id == activeSemesterId || sem.isCurrent)

          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                HapticEngine.selection(context, haptic)
                selectedSemesterId = sem.id
              },
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) BrandCharcoal else BrandCanvas,
            border = BorderStroke(0.75.dp, if (isSelected) BrandCharcoal else LinearBorderHairline)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "Sem ${sem.semesterNumber}",
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else BrandCharcoal
              )
              if (isCurrentActive) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = if (isSelected) AppleSystemGreen else AppleSystemGreen.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = "AKTIF",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else AppleSystemGreen,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Semester Overview Card
      if (currentSemester != null) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = BrandCanvas,
          border = BorderStroke(0.75.dp, LinearBorderHairline)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = currentSemester.displayName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal
              )
              Text(
                text = "$totalSksInSemester / ${currentSemester.targetSks} SKS • Target IPK: ${String.format("%.2f", currentSemester.targetGpa)}",
                fontSize = 11.sp,
                color = BrandSecondary
              )
            }

            if (currentSemester.id != activeSemesterId && !currentSemester.isCurrent) {
              OutlinedButton(
                onClick = {
                  HapticEngine.success(context, haptic)
                  onSetActiveSemester(currentSemester.id)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, AppleSystemGreen),
                modifier = Modifier.height(32.dp)
              ) {
                Text("Jadikan Aktif", fontSize = 11.sp, color = AppleSystemGreen, fontWeight = FontWeight.Bold)
              }
            } else {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = AppleSystemGreen.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "Semester Berjalan",
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Bold,
                  color = AppleSystemGreen,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Courses Header & Add Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "DAFTAR MATA KULIAH (${currentSemesterCourses.size})",
          fontSize = 10.5.sp,
          fontWeight = FontWeight.Bold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )

        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
              HapticEngine.selection(context, haptic)
              showAddCourseForm = !showAddCourseForm
            },
          shape = RoundedCornerShape(8.dp),
          color = if (showAddCourseForm) BrandCanvas else BrandCharcoal
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = if (showAddCourseForm) Icons.Default.Close else Icons.Default.Add,
              contentDescription = null,
              tint = if (showAddCourseForm) BrandCharcoal else Color.White,
              modifier = Modifier.size(13.dp)
            )
            Text(
              text = if (showAddCourseForm) "Batal" else "+ Matkul",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (showAddCourseForm) BrandCharcoal else Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 5. Add Course Inline Form
      AnimatedVisibility(visible = showAddCourseForm) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
          shape = RoundedCornerShape(12.dp),
          color = BrandCanvas,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "Tambah Mata Kuliah Baru",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal
            )

            OutlinedTextField(
              value = courseName,
              onValueChange = {
                courseName = it
                if (courseCode.isBlank()) courseCode = AcademicCourseDefaults.generateCode(it)
              },
              label = { Text("Nama Mata Kuliah (misal: Kecerdasan Buatan)", fontSize = 11.sp) },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
              )
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = courseCode,
                onValueChange = { courseCode = it.uppercase() },
                label = { Text("Kode (AI)", fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                )
              )

              OutlinedTextField(
                value = sks.toString(),
                onValueChange = { sks = it.toIntOrNull()?.coerceIn(1, 6) ?: 3 },
                label = { Text("SKS (3)", fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                )
              )
            }

            OutlinedTextField(
              value = lecturer,
              onValueChange = { lecturer = it },
              label = { Text("Dosen Pengampu", fontSize = 11.sp) },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
              )
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = scheduleDay,
                onValueChange = { scheduleDay = it },
                label = { Text("Hari (Senin)", fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                )
              )

              OutlinedTextField(
                value = scheduleTime,
                onValueChange = { scheduleTime = it },
                label = { Text("Jam (08:00 - 10:30)", fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                )
              )
            }

            // Color palette selector
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Warna:", fontSize = 11.sp, color = BrandSecondary)
              AcademicCourseDefaults.PALETTE.forEach { colorVal ->
                val isColorSelected = (selectedColorHex == colorVal)
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(colorVal))
                    .clickable { selectedColorHex = colorVal }
                    .then(
                      if (isColorSelected) Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)
                      else Modifier
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (isColorSelected) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
              }
            }

            Button(
              onClick = {
                if (courseName.isNotBlank()) {
                  val newCourse = AcademicCourse(
                    id = "c_${UUID.randomUUID()}",
                    semesterId = selectedSemesterId,
                    code = if (courseCode.isNotBlank()) courseCode else AcademicCourseDefaults.generateCode(courseName),
                    name = courseName.trim(),
                    lecturer = lecturer.trim().ifEmpty { "Dosen Pengampu" },
                    sks = sks,
                    scheduleDay = scheduleDay.trim().ifEmpty { "Senin" },
                    scheduleTime = scheduleTime.trim().ifEmpty { "08:00 - 10:30" },
                    classRoom = classRoom.trim().ifEmpty { "Ruang Kuliah" },
                    colorHex = selectedColorHex
                  )
                  HapticEngine.success(context, haptic)
                  onAddCourse(newCourse)
                  courseName = ""
                  courseCode = ""
                  lecturer = ""
                  showAddCourseForm = false
                }
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal)
            ) {
              Text("Simpan Mata Kuliah", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }

      // 6. Courses List for Selected Semester
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (currentSemesterCourses.isEmpty()) {
          item {
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
              color = BrandCanvas,
              shape = RoundedCornerShape(12.dp)
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "Belum Ada Mata Kuliah",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BrandCharcoal
                )
                Text(
                  text = "Ketuk tombol '+ Matkul' untuk menambahkan mata kuliah di semester ini.",
                  fontSize = 11.5.sp,
                  color = BrandSecondary,
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
            }
          }
        } else {
          items(currentSemesterCourses, key = { it.id }) { course ->
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              color = Color.White,
              border = BorderStroke(0.75.dp, LinearBorderHairline)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  // Code pill
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(course.colorHex).copy(alpha = 0.15f),
                    modifier = Modifier.size(width = 44.dp, height = 36.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = course.code,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(course.colorHex)
                      )
                    }
                  }

                  Column(modifier = Modifier.weight(1f)) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      Text(
                        text = course.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandCharcoal
                      )
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = BrandCanvas
                      ) {
                        Text(
                          text = "${course.sks} SKS",
                          fontSize = 9.5.sp,
                          fontWeight = FontWeight.SemiBold,
                          color = BrandSecondary,
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                      }
                    }

                    Text(
                      text = "${course.lecturer} • ${course.scheduleDay} (${course.scheduleTime})",
                      fontSize = 11.sp,
                      color = BrandSecondary
                    )
                  }
                }

                IconButton(
                  onClick = {
                    HapticEngine.warning(context, haptic)
                    onDeleteCourse(course.id)
                  },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = BrandSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
