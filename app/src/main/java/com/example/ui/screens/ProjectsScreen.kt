package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KanbanColumn
import com.example.data.model.ProjectItem
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.ui.components.LinearBorderHairline
import com.example.ui.components.LinearDoneGreen
import com.example.ui.components.LinearInProgressAmber
import com.example.ui.components.LinearPrioritySignal
import com.example.ui.components.LinearStatusRing
import com.example.ui.components.LinearUrgentRed
import com.example.ui.components.UnifiedTopAppBar
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
import com.example.ui.theme.HapticEngine

/**
 * Super High-End Executive Projects Workspace.
 * Redesigned for maximum clarity and user-friendliness:
 * 1. Unified 100% consistent Top Navigation Bar.
 * 2. Executive Project Cards with dual-metric milestone progress (percentage + count).
 * 3. Deep-dive Project Detail View with clear Back navigation.
 * 4. Clean vertical task sections (Belum Dikerjakan, Sedang Berjalan, Selesai).
 * 5. Instant inline task creator & new project dialog.
 */
@Composable
fun ProjectsScreen(
  projects: List<ProjectItem>,
  tasks: List<TaskItem>,
  onTaskClick: (String) -> Unit,
  onNewTaskClick: () -> Unit,
  modifier: Modifier = Modifier,
  onUpdateKanbanStatus: (String, KanbanColumn) -> Unit = { _, _ -> },
  onToggleTaskComplete: (String) -> Unit = {},
  onAddProject: (String, String, String) -> Unit = { _, _, _ -> },
  onAddTaskToProject: (String, String, String) -> Unit = { _, _, _ -> },
  userProfile: com.example.data.model.UserProfile? = null,
  onStartFocus: ((TaskItem) -> Unit)? = null
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  var selectedCategory by remember { mutableStateOf("All") }
  var selectedStatusFilter by remember { mutableStateOf("All") }
  var selectedProjectId by remember { mutableStateOf<String?>(null) }
  var showNewProjectDialog by remember { mutableStateOf(false) }

  val currentProject = projects.find { it.id == selectedProjectId }

  val filteredProjects = projects.filter { proj ->
    val catMatch = (selectedCategory == "All" || proj.category.equals(selectedCategory, ignoreCase = true))
    val projTasks = tasks.filter {
      it.project.equals(proj.title, ignoreCase = true) || it.category.equals(proj.category, ignoreCase = true)
    }
    val total = if (projTasks.isNotEmpty()) projTasks.size else proj.totalTasks
    val done = if (projTasks.isNotEmpty()) projTasks.count { it.isCompleted } else proj.completedTasks
    val isDone = total > 0 && done >= total
    val statusMatch = when (selectedStatusFilter) {
      "Active" -> !isDone
      "Completed" -> isDone
      else -> true
    }
    catMatch && statusMatch
  }

  val totalProjectTasks = tasks.count { task ->
    projects.any { it.title.equals(task.project, ignoreCase = true) }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
  ) {
    // 1. Unified Fixed Header (Rock-solid consistency with Home, Schedule, and Profile)
    if (currentProject == null) {
      UnifiedTopAppBar(
        title = "wii to do",
        subtitle = "${projects.size} proyek aktif • $totalProjectTasks tugas",
        showVerifiedBadge = (userProfile?.resolvedBadgeTier != null && userProfile.resolvedBadgeTier != com.example.data.model.VerifiedBadgeTier.NONE),
        verifiedTier = userProfile?.resolvedBadgeTier ?: com.example.data.model.VerifiedBadgeTier.NONE,
        actions = {
          // New Project Button
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                HapticEngine.selection(context, haptic)
                showNewProjectDialog = true
              },
            shape = RoundedCornerShape(10.dp),
            color = BrandCharcoal
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "Proyek",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
              )
            }
          }
        }
      )
    } else {
      UnifiedTopAppBar(
        title = currentProject.title,
        subtitle = "${currentProject.category} • ${currentProject.code}",
        showVerifiedBadge = false,
        customLeading = {
          Surface(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .clickable {
                HapticEngine.selection(context, haptic)
                selectedProjectId = null
              },
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(0.75.dp, LinearBorderHairline)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = BrandCharcoal,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        },
        actions = {
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable {
                HapticEngine.selection(context, haptic)
                selectedProjectId = null
              },
            shape = RoundedCornerShape(8.dp),
            color = BrandCanvas,
            border = BorderStroke(1.dp, BrandBorderLight)
          ) {
            Text(
              text = "Tutup Detail",
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = BrandSecondary,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
            )
          }
        }
      )
    }

    // 2. Main Content
    if (currentProject == null) {
      // OVERVIEW / LIST OF ALL PROJECTS
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
          top = 14.dp,
          bottom = 150.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Category Filter Chips
        item {
          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val categories = listOf("All" to "Semua Kategori", "College" to "🎓 Kuliah", "Work" to "💼 Pekerjaan", "Personal" to "👤 Pribadi")
            items(categories) { (catKey, label) ->
              val isSelected = (selectedCategory == catKey)
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .clickable {
                    HapticEngine.selection(context, haptic)
                    selectedCategory = catKey
                  },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) BrandCharcoal else Color.White,
                border = if (!isSelected) BorderStroke(1.dp, BrandBorder) else null
              ) {
                Text(
                  text = label,
                  fontSize = 11.5.sp,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                  color = if (isSelected) Color.White else BrandSecondary,
                  modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp)
                )
              }
            }
          }
        }

        // Status Segmented Filter (Semua, Aktif, Selesai)
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("All" to "Semua Proyek", "Active" to "⚡ Berjalan", "Completed" to "✓ Selesai").forEach { (statusKey, label) ->
              val isSelected = (selectedStatusFilter == statusKey)
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .clickable {
                    HapticEngine.selection(context, haptic)
                    selectedStatusFilter = statusKey
                  },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) BrandOliveBg else Color.White,
                border = BorderStroke(1.dp, if (isSelected) BrandOliveBorder else BrandBorderLight)
              ) {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) BrandOlive else BrandSecondary,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
              }
            }
          }
        }

        // Project Cards
        if (filteredProjects.isEmpty()) {
          item {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Outlined.Folder,
                contentDescription = null,
                tint = BrandSecondary.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Belum Ada Proyek",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandCharcoal
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tap '+ Proyek' di pojok kanan atas untuk membuat proyek baru.",
                fontSize = 12.sp,
                color = BrandSecondary
              )
            }
          }
        } else {
          items(filteredProjects, key = { it.id }) { proj ->
            val projTasks = tasks.filter {
              it.project.equals(proj.title, ignoreCase = true) ||
                  it.category.equals(proj.category, ignoreCase = true)
            }
            val total = if (projTasks.isNotEmpty()) projTasks.size else proj.totalTasks
            val done = if (projTasks.isNotEmpty()) projTasks.count { it.isCompleted } else proj.completedTasks
            val progress = if (total > 0) (done.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
            val pct = (progress * 100).toInt()

            ExecutiveProjectCard(
              project = proj,
              total = total,
              completed = done,
              progress = progress,
              progressPercent = pct,
              onClick = {
                HapticEngine.selection(context, haptic)
                selectedProjectId = proj.id
              }
            )
          }
        }
      }
    } else {
      // DEEP-DIVE PROJECT DETAIL VIEW
      ProjectDetailView(
        project = currentProject,
        tasks = tasks.filter {
          it.project.equals(currentProject.title, ignoreCase = true) ||
              it.category.equals(currentProject.category, ignoreCase = true)
        },
        onBack = { selectedProjectId = null },
        onTaskClick = onTaskClick,
        onToggleTask = onToggleTaskComplete,
        onUpdateKanbanStatus = onUpdateKanbanStatus,
        onAddTask = { title ->
          onAddTaskToProject(title, currentProject.title, currentProject.category)
        },
        onStartFocus = onStartFocus
      )
    }
  }

  // Create Project Dialog
  if (showNewProjectDialog) {
    CreateProjectDialog(
      onDismiss = { showNewProjectDialog = false },
      onCreate = { title, cat, desc ->
        onAddProject(title, cat, desc)
        showNewProjectDialog = false
      }
    )
  }
}

/**
 * Executive Project Card Component with Dual-Metric Progress Bar.
 */
@Composable
private fun ExecutiveProjectCard(
  project: ProjectItem,
  total: Int,
  completed: Int,
  progress: Float,
  progressPercent: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.985f else 1.0f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
    label = "proj_scale"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .shadow(
        elevation = 0.5.dp,
        shape = RoundedCornerShape(16.dp),
        ambientColor = Color(0x06000000),
        spotColor = Color(0x04000000)
      )
      .clip(RoundedCornerShape(16.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null
      ) { onClick() },
    shape = RoundedCornerShape(16.dp),
    color = Color.White,
    border = BorderStroke(1.dp, BrandBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top Row: Category Pill + Health Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = when (project.category) {
            "College" -> BrandTerracottaBg
            "Work" -> BrandOliveBg
            else -> BrandTagBg
          },
          border = BorderStroke(
            1.dp,
            when (project.category) {
              "College" -> BrandTerracotta.copy(alpha = 0.25f)
              "Work" -> BrandOliveBorder
              else -> BrandBorderLight
            }
          )
        ) {
          Text(
            text = "${project.category.uppercase()} • ${project.code}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = when (project.category) {
              "College" -> BrandTerracotta
              "Work" -> BrandOlive
              else -> BrandCharcoal
            },
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
          )
        }

        // Health Status Pill
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (progressPercent >= 100) BrandOliveBg else BrandCanvas
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (progressPercent >= 100) BrandOlive else BrandCharcoal)
            )
            Text(
              text = if (progressPercent >= 100) "Selesai" else if (progressPercent >= 50) "On Track" else "In Progress",
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (progressPercent >= 100) BrandOlive else BrandCharcoal
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Title & Description
      Text(
        text = project.title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = BrandCharcoal,
        letterSpacing = (-0.01).sp
      )
      if (project.description.isNotBlank()) {
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = project.description,
          fontSize = 12.sp,
          color = BrandSecondary,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Dual-metric Milestone Progress
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "$progressPercent% selesai",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = BrandCharcoal
        )
        Text(
          text = "$completed/$total tugas",
          fontSize = 11.5.sp,
          fontWeight = FontWeight.Medium,
          color = BrandSecondary
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(5.5.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = if (progressPercent >= 100) BrandOlive else BrandCharcoal,
        trackColor = BrandCanvas
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Tap untuk lihat & kelola tugas →",
          fontSize = 11.sp,
          color = BrandSecondary,
          fontWeight = FontWeight.Medium
        )
        Text(
          text = "${total - completed} tersisa",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (total - completed > 0) BrandTerracotta else BrandOlive
        )
      }
    }
  }
}

/**
 * Deep-Dive Project Detail View with 3 Clean Vertical Status Groups.
 */
@Composable
private fun ProjectDetailView(
  project: ProjectItem,
  tasks: List<TaskItem>,
  onBack: () -> Unit,
  onTaskClick: (String) -> Unit,
  onToggleTask: (String) -> Unit,
  onUpdateKanbanStatus: (String, KanbanColumn) -> Unit,
  onAddTask: (String) -> Unit,
  onStartFocus: ((TaskItem) -> Unit)? = null
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  var quickTaskTitle by remember { mutableStateOf("") }

  val todoTasks = tasks.filter { !it.isCompleted && it.kanbanStatus != KanbanColumn.IN_PROGRESS }
  val inProgressTasks = tasks.filter { !it.isCompleted && it.kanbanStatus == KanbanColumn.IN_PROGRESS }
  val doneTasks = tasks.filter { it.isCompleted || it.kanbanStatus == KanbanColumn.DONE }

  val total = tasks.size
  val completed = doneTasks.size
  val progress = if (total > 0) (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
  val progressPct = (progress * 100).toInt()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(
      top = 12.dp,
      bottom = 150.dp
    ),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Back Navigation & Project Milestone Summary Card
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  HapticEngine.selection(context, haptic)
                  onBack()
                },
              shape = RoundedCornerShape(8.dp),
              color = BrandCanvas,
              border = BorderStroke(1.dp, BrandBorderLight)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = null,
                  tint = BrandCharcoal,
                  modifier = Modifier.size(13.dp)
                )
                Text(
                  text = "Semua Proyek",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = BrandCharcoal
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = BrandTagBg
            ) {
              Text(
                text = project.code,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = project.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal
          )
          if (project.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = project.description,
              fontSize = 12.5.sp,
              color = BrandSecondary
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Progress
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "$progressPct% Selesai",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal
            )
            Text(
              text = "$completed dari $total tugas selesai",
              fontSize = 11.5.sp,
              color = BrandSecondary
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(2.5.dp)),
            color = BrandOlive,
            trackColor = BrandCanvas
          )
        }
      }
    }

    // Direct Focus Tip Banner
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = BrandOliveBg,
        border = BorderStroke(1.dp, BrandOliveBorder)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .background(BrandOlive.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text("⚡", fontSize = 13.sp)
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Alur Eksekusi Cepat",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = BrandOlive
            )
            Text(
              text = "Tap '⚡ Fokus' pada tugas di bawah untuk langsung menyalakan floating focus timer.",
              fontSize = 11.sp,
              color = BrandCharcoal.copy(alpha = 0.85f)
            )
          }
        }
      }
    }

    // 2. Fast Inline Task Adder
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrandBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = quickTaskTitle,
            onValueChange = { quickTaskTitle = it },
            placeholder = { Text("Tambah tugas untuk proyek ini...", fontSize = 12.sp, color = BrandSecondary) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent
            )
          )

          Button(
            onClick = {
              if (quickTaskTitle.isNotBlank()) {
                HapticEngine.success(context, haptic)
                onAddTask(quickTaskTitle.trim())
                quickTaskTitle = ""
              }
            },
            enabled = quickTaskTitle.isNotBlank(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text("Tambah", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 3. Section: Sedang Berjalan (In Progress)
    if (inProgressTasks.isNotEmpty()) {
      item {
        ProjectTaskSectionHeader(
          title = "Sedang Berjalan",
          count = inProgressTasks.size,
          indicatorColor = LinearInProgressAmber
        )
      }
      items(inProgressTasks, key = { it.id }) { task ->
        ProjectTaskRowItem(
          task = task,
          onToggle = { onToggleTask(task.id) },
          onClick = { onTaskClick(task.id) },
          onSetStatus = { col -> onUpdateKanbanStatus(task.id, col) },
          onStartFocus = { onStartFocus?.invoke(task) }
        )
      }
    }

    // 4. Section: Belum Selesai (To Do)
    item {
      ProjectTaskSectionHeader(
        title = "Belum Dikerjakan",
        count = todoTasks.size,
        indicatorColor = BrandCharcoal
      )
    }
    if (todoTasks.isEmpty()) {
      item {
        Text(
          text = "Tidak ada tugas tertunda di bagian ini.",
          fontSize = 11.5.sp,
          color = BrandSecondary,
          modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
      }
    } else {
      items(todoTasks, key = { it.id }) { task ->
        ProjectTaskRowItem(
          task = task,
          onToggle = { onToggleTask(task.id) },
          onClick = { onTaskClick(task.id) },
          onSetStatus = { col -> onUpdateKanbanStatus(task.id, col) },
          onStartFocus = { onStartFocus?.invoke(task) }
        )
      }
    }

    // 5. Section: Selesai (Done)
    if (doneTasks.isNotEmpty()) {
      item {
        ProjectTaskSectionHeader(
          title = "Selesai",
          count = doneTasks.size,
          indicatorColor = LinearDoneGreen
        )
      }
      items(doneTasks, key = { it.id }) { task ->
        ProjectTaskRowItem(
          task = task,
          onToggle = { onToggleTask(task.id) },
          onClick = { onTaskClick(task.id) },
          onSetStatus = { col -> onUpdateKanbanStatus(task.id, col) },
          onStartFocus = { onStartFocus?.invoke(task) }
        )
      }
    }
  }
}

@Composable
private fun ProjectTaskSectionHeader(
  title: String,
  count: Int,
  indicatorColor: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 4.dp, bottom = 2.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .background(indicatorColor, CircleShape)
    )
    Text(
      text = "$title ($count)",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = BrandCharcoal
    )
  }
}

@Composable
private fun ProjectTaskRowItem(
  task: TaskItem,
  onToggle: () -> Unit,
  onClick: () -> Unit,
  onSetStatus: (KanbanColumn) -> Unit,
  onStartFocus: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val isDone = task.isCompleted

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(12.dp),
    color = Color.White,
    border = BorderStroke(0.75.dp, LinearBorderHairline)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Checkbox circle
      Box(
        modifier = Modifier
          .size(30.dp)
          .clip(CircleShape)
          .clickable {
            if (!isDone) HapticEngine.success(context, haptic) else HapticEngine.selection(context, haptic)
            onToggle()
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isDone) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
          contentDescription = null,
          tint = if (isDone) LinearDoneGreen else BrandSecondary,
          modifier = Modifier.size(20.dp)
        )
      }

      // Title & Subtext & Course Badge
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = task.title,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (isDone) BrandSecondary.copy(alpha = 0.6f) else BrandCharcoal,
          textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          if (!task.courseName.isNullOrBlank()) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = BrandTerracottaBg
            ) {
              Text(
                text = task.courseName,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandTerracotta,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
              )
            }
          }
          if (task.dueDate.isNotBlank()) {
            Text(
              text = task.dueDate,
              fontSize = 10.5.sp,
              color = BrandSecondary
            )
          }
        }
      }

      // Action Pills: 1-Tap Fokus & Status Pill
      if (!isDone) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Instant 1-tap Focus pill
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = BrandCharcoal,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .clickable {
                HapticEngine.selection(context, haptic)
                onStartFocus?.invoke()
              }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Text(
                text = "⚡",
                fontSize = 9.sp
              )
              Text(
                text = "Fokus",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          // Quick Status Pill
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (task.kanbanStatus == KanbanColumn.IN_PROGRESS) LinearInProgressAmber.copy(alpha = 0.15f) else BrandCanvas,
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .clickable {
                HapticEngine.selection(context, haptic)
                if (task.kanbanStatus == KanbanColumn.IN_PROGRESS) {
                  onSetStatus(KanbanColumn.TO_DO)
                } else {
                  onSetStatus(KanbanColumn.IN_PROGRESS)
                }
              }
          ) {
            Text(
              text = if (task.kanbanStatus == KanbanColumn.IN_PROGRESS) "Sedang Jalan" else "Mulai",
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (task.kanbanStatus == KanbanColumn.IN_PROGRESS) Color(0xFFB45309) else BrandCharcoal,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Modal Dialog to Create a New Project.
 */
@Composable
private fun CreateProjectDialog(
  onDismiss: () -> Unit,
  onCreate: (String, String, String) -> Unit
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("College") }
  var description by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Buat Proyek Baru",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = BrandCharcoal
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Nama Proyek", fontSize = 12.sp) },
          placeholder = { Text("Contoh: Skripsi / Tugas Akhir", fontSize = 12.sp) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        // Category Selector
        Text("Kategori:", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = BrandCharcoal)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("College" to "Kuliah", "Work" to "Kerja", "Personal" to "Pribadi").forEach { (catKey, label) ->
            val isSelected = (category == catKey)
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) BrandCharcoal else BrandCanvas,
              border = if (!isSelected) BorderStroke(1.dp, BrandBorderLight) else null,
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  HapticEngine.selection(context, haptic)
                  category = catKey
                }
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else BrandCharcoal,
                modifier = Modifier.padding(vertical = 7.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Catatan / Deskripsi (Opsional)", fontSize = 12.sp) },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            HapticEngine.success(context, haptic)
            onCreate(title.trim(), category, description.trim())
          }
        },
        enabled = title.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Buat Proyek", fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Batal", color = BrandSecondary, fontSize = 12.sp)
      }
    },
    containerColor = Color.White,
    shape = RoundedCornerShape(16.dp)
  )
}
