package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KanbanColumn
import com.example.data.model.ProjectItem
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.ui.components.WiiBrandLogo
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
fun ProjectsScreen(
  projects: List<ProjectItem>,
  tasks: List<TaskItem>,
  onTaskClick: (String) -> Unit,
  onNewTaskClick: () -> Unit,
  modifier: Modifier = Modifier,
  onUpdateKanbanStatus: (String, KanbanColumn) -> Unit = { _, _ -> }
) {
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var viewTab by remember { mutableStateOf("Overview") } // "Overview" vs "Kanban"
  var selectedCategory by remember { mutableStateOf("All") }
  var selectedKanbanColumn by remember { mutableStateOf(KanbanColumn.IN_PROGRESS) }
  var selectedProjectId by remember { mutableStateOf(projects.firstOrNull()?.id ?: "proj_1") }

  val currentProject = projects.find { it.id == selectedProjectId } ?: projects.firstOrNull()

  val filteredProjects = projects.filter {
    selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
  ) {
    // 1. Fixed Top Bar (Pinned at top, does not scroll)
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = BrandCanvas
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 20.dp, end = 20.dp, top = if (topInset > 0.dp) topInset else 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          WiiBrandLogo(size = 30.dp)
          Column {
            Text(
              text = "wii to do",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal,
              letterSpacing = (-0.02).sp
            )
            Text(
              text = "projects",
              fontSize = 10.sp,
              color = BrandSecondary,
              fontWeight = FontWeight.Medium,
              letterSpacing = 0.5.sp
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color.White,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Row(modifier = Modifier.padding(3.dp)) {
            listOf("Overview", "Kanban").forEach { tab ->
              val isSelected = (viewTab == tab)
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewTab = tab
                  },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) BrandCharcoal else Color.Transparent
              ) {
                Text(
                  text = tab,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                  color = if (isSelected) Color.White else BrandSecondary,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      contentPadding = androidx.compose.foundation.layout.PaddingValues(
        top = 4.dp,
        bottom = 150.dp
      ),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

    if (viewTab == "Overview") {
      // Projects Overview View
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Projects",
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal,
              letterSpacing = (-0.02).sp
            )
            Text(
              text = "${projects.size} active projects across College & Work",
              fontSize = 12.sp,
              color = BrandSecondary
            )
          }

          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onNewTaskClick()
              },
            shape = RoundedCornerShape(10.dp),
            color = BrandCharcoal
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
              )
              Text(
                text = "New",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
              )
            }
          }
        }
      }

      // Filter chips
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("All", "College", "Work", "Personal").forEach { cat ->
            val isSelected = (selectedCategory == cat)
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  selectedCategory = cat
                },
              shape = RoundedCornerShape(16.dp),
              color = if (isSelected) BrandCharcoal else Color.White,
              border = if (!isSelected) BorderStroke(1.dp, BrandBorder) else null
            ) {
              Text(
                text = cat,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) Color.White else BrandSecondary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      // Project Cards
      items(filteredProjects, key = { it.id }) { proj ->
        val pct = (proj.completedTasks.toFloat() / proj.totalTasks.toFloat()).coerceIn(0f, 1f)
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
          targetValue = if (isPressed) 0.98f else 1.0f,
          animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
          label = "proj_scale"
        )

        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
              scaleX = scale
              scaleY = scale
            }
            .shadow(
              elevation = 1.dp,
              shape = RoundedCornerShape(16.dp),
              ambientColor = Color(0x06000000),
              spotColor = Color(0x05000000)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(
              interactionSource = interactionSource,
              indication = null
            ) {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              selectedProjectId = proj.id
              viewTab = "Kanban"
            },
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
                shape = RoundedCornerShape(6.dp),
                color = if (proj.category == "Work") BrandOliveBg else BrandTagBg,
                border = BorderStroke(1.dp, if (proj.category == "Work") BrandOliveBorder else BrandBorderLight)
              ) {
                Text(
                  text = proj.code,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (proj.category == "Work") BrandOlive else BrandCharcoal,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }

              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = BrandSecondary,
                modifier = Modifier.size(18.dp)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = proj.title,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal,
              letterSpacing = (-0.01).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "${proj.completedTasks} of ${proj.totalTasks} completed",
                fontSize = 12.sp,
                color = BrandSecondary
              )
              Text(
                text = "${(pct * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrandCharcoal
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
              progress = { pct },
              modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp)),
              color = BrandCharcoal,
              trackColor = BrandBorderLight
            )

            if (proj.nextTaskPreview.isNotEmpty()) {
              Spacer(modifier = Modifier.height(14.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                  Icon(
                    imageVector = Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = BrandSecondary,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = proj.nextTaskPreview,
                    fontSize = 12.sp,
                    color = BrandCharcoal,
                    maxLines = 1
                  )
                }
                Text(
                  text = proj.nextTaskDue,
                  fontSize = 11.sp,
                  color = if (proj.nextTaskDue.contains("Today")) BrandTerracotta else BrandSecondary,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }
    } else {
      // Interactive Kanban Board View
      // 1. Horizontal Project Selector Pills
      item {
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(projects) { p ->
            val isCurrent = (p.id == selectedProjectId)
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  selectedProjectId = p.id
                },
              shape = RoundedCornerShape(8.dp),
              color = if (isCurrent) BrandCharcoal else Color.White,
              border = if (!isCurrent) BorderStroke(1.dp, BrandBorder) else null
            ) {
              Text(
                text = p.code,
                fontSize = 11.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent) Color.White else BrandSecondary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      // 2. Project Header Card
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
                shape = RoundedCornerShape(6.dp),
                color = if (currentProject?.category == "Work") BrandOliveBg else BrandTagBg,
                border = BorderStroke(1.dp, if (currentProject?.category == "Work") BrandOliveBorder else BrandBorderLight)
              ) {
                Text(
                  text = currentProject?.code ?: "Project",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (currentProject?.category == "Work") BrandOlive else BrandCharcoal,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
              Text(
                text = currentProject?.dueDate ?: "",
                fontSize = 11.sp,
                color = BrandSecondary
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = currentProject?.title ?: "Project Tasks",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal,
              letterSpacing = (-0.02).sp
            )
            if (!currentProject?.description.isNullOrEmpty()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentProject!!.description,
                fontSize = 12.sp,
                color = BrandSecondary,
                lineHeight = 16.sp
              )
            }
          }
        }
      }

      // 3. Sprint & Tags Bar
      val projectTags = currentProject?.tags?.ifEmpty {
        listOf("#${currentProject.category.lowercase()}", "#sprint", "#focus")
      } ?: listOf("#project", "#tasks")

      item {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = BrandPillBg,
          border = BorderStroke(1.dp, BrandBorderLight)
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = BrandOlive,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = currentProject?.activeSprint ?: "Sprint Focus",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = BrandCharcoal
                )
              }
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = BrandOliveBg,
                border = BorderStroke(1.dp, BrandOliveBorder)
              ) {
                Text(
                  text = "ACTIVE",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = BrandOlive,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              items(projectTags) { tag ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color.White,
                  border = BorderStroke(1.dp, BrandBorderLight)
                ) {
                  Text(
                    text = tag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }
            }
          }
        }
      }

      // 4. Tasks Filtered By Project
      val projectTasks = tasks.filter { task ->
        currentProject == null ||
          task.project.equals(currentProject.title, ignoreCase = true) ||
          task.category.equals(currentProject.category, ignoreCase = true)
      }

      // 5. Column Switcher Header Tabs (In Progress, To Do, Done)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          KanbanColumn.values().forEach { col ->
            val isSelected = (selectedKanbanColumn == col)
            val count = projectTasks.count { it.kanbanStatus == col }
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  selectedKanbanColumn = col
                },
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) BrandCharcoal else Color.White,
              border = if (!isSelected) BorderStroke(1.dp, BrandBorder) else null
            ) {
              Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = col.label,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else BrandSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                  shape = CircleShape,
                  color = if (isSelected) Color.White.copy(alpha = 0.2f) else BrandPillBg
                ) {
                  Text(
                    text = "$count",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else BrandSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                  )
                }
              }
            }
          }
        }
      }

      // 6. Kanban Tasks List
      val kanbanTasks = projectTasks.filter { it.kanbanStatus == selectedKanbanColumn }

      if (kanbanTasks.isEmpty()) {
        item {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 20.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BrandBorderLight)
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = "No tasks in ${selectedKanbanColumn.label}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandCharcoal
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Move tasks here or create a new task for ${currentProject?.title ?: "this workspace"}",
                fontSize = 12.sp,
                color = BrandSecondary,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { onNewTaskClick() },
                shape = RoundedCornerShape(8.dp),
                color = BrandCharcoal
              ) {
                Text(
                  text = "+ Create Task",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
              }
            }
          }
        }
      }

      items(kanbanTasks, key = { it.id }) { task ->
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
          targetValue = if (isPressed) 0.98f else 1.0f,
          animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
          label = "task_scale"
        )

        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
              scaleX = scale
              scaleY = scale
            }
            .shadow(1.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable(
              interactionSource = interactionSource,
              indication = null
            ) {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onTaskClick(task.id)
            },
          shape = RoundedCornerShape(14.dp),
          color = Color.White,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (task.priority == TaskPriority.HIGH) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = BrandTerracottaBg
                  ) {
                    Text(
                      text = "High Priority",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = BrandTerracotta,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = BrandTagBg
                ) {
                  Text(
                    text = task.category,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandCharcoal,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = BrandSecondary,
                modifier = Modifier.size(16.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = task.title,
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = BrandCharcoal,
              textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )

            if (task.description.isNotEmpty()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = task.description,
                fontSize = 12.sp,
                color = BrandSecondary,
                maxLines = 2
              )
            }

            if (task.tags.isNotEmpty()) {
              Spacer(modifier = Modifier.height(8.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                task.tags.take(2).forEach { t ->
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = BrandPillBg,
                    border = BorderStroke(1.dp, BrandBorderLight)
                  ) {
                    Text(
                      text = t,
                      fontSize = 10.sp,
                      color = BrandOlive,
                      fontWeight = FontWeight.Medium,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = task.dueTime,
                fontSize = 11.sp,
                color = BrandSecondary
              )

              if (task.actualSubtasksTotal > 0) {
                Text(
                  text = "${task.actualSubtasksCompleted}/${task.actualSubtasksTotal} subtasks",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = BrandOlive
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Kanban Status Actions
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              when (selectedKanbanColumn) {
                KanbanColumn.TO_DO -> {
                  Surface(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onUpdateKanbanStatus(task.id, KanbanColumn.IN_PROGRESS)
                      },
                    shape = RoundedCornerShape(6.dp),
                    color = BrandCharcoal
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text(
                        text = "Start Sprint →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                      )
                    }
                  }
                }
                KanbanColumn.IN_PROGRESS -> {
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                          onUpdateKanbanStatus(task.id, KanbanColumn.TO_DO)
                        },
                      shape = RoundedCornerShape(6.dp),
                      color = BrandPillBg,
                      border = BorderStroke(1.dp, BrandBorderLight)
                    ) {
                      Text(
                        text = "← To Do",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                      )
                    }

                    Surface(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                          onUpdateKanbanStatus(task.id, KanbanColumn.DONE)
                        },
                      shape = RoundedCornerShape(6.dp),
                      color = BrandOlive
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Check,
                          contentDescription = null,
                          tint = Color.White,
                          modifier = Modifier.size(12.dp)
                        )
                        Text(
                          text = "Done",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.SemiBold,
                          color = Color.White
                        )
                      }
                    }
                  }
                }
                KanbanColumn.DONE -> {
                  Surface(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onUpdateKanbanStatus(task.id, KanbanColumn.IN_PROGRESS)
                      },
                    shape = RoundedCornerShape(6.dp),
                    color = BrandPillBg,
                    border = BorderStroke(1.dp, BrandBorderLight)
                  ) {
                    Text(
                      text = "↺ Reopen",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Medium,
                      color = BrandSecondary,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                  }
                }
                KanbanColumn.BACKLOG -> {
                  Surface(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onUpdateKanbanStatus(task.id, KanbanColumn.TO_DO)
                      },
                    shape = RoundedCornerShape(6.dp),
                    color = BrandCharcoal
                  ) {
                    Text(
                      text = "Move to To Do →",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                  }
                }
                KanbanColumn.CANCELED -> {
                  Surface(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onUpdateKanbanStatus(task.id, KanbanColumn.TO_DO)
                      },
                    shape = RoundedCornerShape(6.dp),
                    color = BrandPillBg,
                    border = BorderStroke(1.dp, BrandBorderLight)
                  ) {
                    Text(
                      text = "↺ Restore",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Medium,
                      color = BrandSecondary,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                  }
                }
                else -> {}
              }
            }
          }
        }
      }
    }
  }
}
}

