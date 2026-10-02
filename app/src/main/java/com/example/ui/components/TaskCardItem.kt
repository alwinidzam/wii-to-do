package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.ui.theme.AppleSystemBlue
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandCheckboxBorder
import com.example.ui.theme.BrandDotNeutral
import com.example.ui.theme.BrandPillBg
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTagBg
import com.example.ui.theme.BrandTerracotta
import com.example.ui.theme.BrandTerracottaBg
import com.example.ui.theme.HapticEngine
import kotlinx.coroutines.delay

/**
 * Super High-End Apple HIG & Linear-Grade Task Item.
 * Features:
 * - Dual-direction Swipe-to-Triage (Swipe right = Complete, Swipe left = Delete)
 * - Linear semantic status rings with tactile spring feedback
 * - Linear 3-bar cellular priority signal
 * - Clean 0.75dp hairline border with subtle background tint
 * - Zero clipping on metadata tags
 * - Tactile Taptic Engine haptic responses
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCardItem(
  task: TaskItem,
  onToggleComplete: () -> Unit,
  onClick: () -> Unit = {},
  modifier: Modifier = Modifier,
  onDelete: (() -> Unit)? = null,
  isSelected: Boolean = false,
  onSelect: () -> Unit = {}
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  var isExpanded by remember { mutableStateOf(false) }
  var isDeleting by remember { mutableStateOf(false) }

  val isDone = task.isCompleted

  val titleColor = if (isDone) BrandSecondary.copy(alpha = 0.55f) else BrandCharcoal
  val textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None

  // Once slide-out completes, trigger delete
  LaunchedEffect(isDeleting) {
    if (isDeleting) {
      delay(280)
      onDelete?.invoke()
    }
  }

  val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = { dismissValue ->
      when (dismissValue) {
        SwipeToDismissBoxValue.StartToEnd -> {
          // Swiped Right -> Mark Done
          HapticEngine.success(context, haptic)
          onToggleComplete()
          false // Reset back smoothly after marking complete
        }
        SwipeToDismissBoxValue.EndToStart -> {
          // Swiped Left -> Delete
          if (onDelete != null) {
            HapticEngine.warning(context, haptic)
            isDeleting = true
            true
          } else {
            false
          }
        }
        SwipeToDismissBoxValue.Settled -> false
      }
    }
  )

  AnimatedVisibility(
    visible = !isDeleting,
    enter = fadeIn(animationSpec = tween(200)),
    exit = slideOutHorizontally(
      targetOffsetX = { fullWidth -> -fullWidth },
      animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(durationMillis = 180)) + shrinkVertically(animationSpec = tween(durationMillis = 260)),
    modifier = modifier
  ) {
    SwipeToDismissBox(
      state = dismissState,
      enableDismissFromStartToEnd = isSelected,
      enableDismissFromEndToStart = isSelected && onDelete != null,
      backgroundContent = {
        val direction = dismissState.dismissDirection
        val color by animateColorAsState(
          targetValue = when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> LinearDoneGreen
            SwipeToDismissBoxValue.EndToStart -> LinearUrgentRed
            else -> Color.Transparent
          },
          label = "swipe_bg_color"
        )

        Box(
          modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(14.dp))
            .background(color)
            .padding(horizontal = 20.dp),
          contentAlignment = when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
            else -> Alignment.CenterEnd
          }
        ) {
          when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Complete",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  text = if (isDone) "Reopen" else "Complete",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
            }
            SwipeToDismissBoxValue.EndToStart -> {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = "Delete",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Icon(
                  imageVector = Icons.Outlined.Delete,
                  contentDescription = "Delete",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            else -> {}
          }
        }
      }
    ) {
      TaskCardContent(
        task = task,
        isDone = isDone,
        titleColor = titleColor,
        textDecoration = textDecoration,
        isExpanded = isExpanded,
        isSelected = isSelected,
        onToggleExpand = { isExpanded = !isExpanded },
        onToggleComplete = onToggleComplete,
        onClick = onClick,
        onSelect = onSelect,
        onDeleteClick = if (onDelete != null) {
          {
            HapticEngine.warning(context, haptic)
            isDeleting = true
          }
        } else null
      )
    }
  }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun TaskCardContent(
  task: TaskItem,
  isDone: Boolean,
  titleColor: Color,
  textDecoration: TextDecoration,
  isExpanded: Boolean,
  isSelected: Boolean,
  onToggleExpand: () -> Unit,
  onToggleComplete: () -> Unit,
  onClick: () -> Unit,
  onSelect: () -> Unit,
  onDeleteClick: (() -> Unit)?,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val cardAlpha = if (isDone) 0.70f else 1.0f

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("task_card_${task.id}")
      .shadow(
        elevation = if (isSelected) 2.dp else 0.5.dp,
        shape = RoundedCornerShape(14.dp),
        ambientColor = Color(0x06000000),
        spotColor = Color(0x04000000)
      )
      .animateContentSize(animationSpec = tween(durationMillis = 180)),
    shape = RoundedCornerShape(14.dp),
    color = Color.White.copy(alpha = cardAlpha),
    border = BorderStroke(
      if (isSelected) 1.5.dp else 0.75.dp,
      if (isSelected) BrandCharcoal else LinearBorderHairline
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clickable {
          HapticEngine.selection(context, haptic)
          onSelect()
        }
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        // Left side: Linear Status Ring + Content
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          // Status Ring touch target (40dp)
          Box(
            modifier = Modifier
              .testTag("task_checkbox_${task.id}")
              .size(36.dp)
              .clip(CircleShape)
              .clickable {
                if (!isDone) {
                  HapticEngine.success(context, haptic)
                } else {
                  HapticEngine.selection(context, haptic)
                }
                onToggleComplete()
              },
            contentAlignment = Alignment.Center
          ) {
            LinearStatusRing(
              status = task.kanbanStatus,
              isCompleted = isDone,
              size = 19.dp
            )
          }

          // Center: Title, Metadata & Subtask Progress
          Column(modifier = Modifier.weight(1f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = task.title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = titleColor,
                textDecoration = textDecoration,
                letterSpacing = (-0.01).sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
              )

              // Linear Priority Signal right beside title
              LinearPrioritySignal(priority = task.priority)
            }

            // Responsive FlowRow for Tags (wraps cleanly on mobile without clipping)
            FlowRow(
              modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              // Category Tag
              Surface(
                shape = RoundedCornerShape(5.dp),
                color = BrandTagBg,
                border = BorderStroke(0.5.dp, LinearBorderHairline)
              ) {
                Text(
                  text = task.category,
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Medium,
                  color = BrandCharcoal,
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                )
              }

              // Academic Course Tag (When present)
              if (!task.courseName.isNullOrBlank()) {
                Surface(
                  shape = RoundedCornerShape(5.dp),
                  color = Color(0xFF4F46E5).copy(alpha = 0.10f),
                  border = BorderStroke(0.5.dp, Color(0xFF4F46E5).copy(alpha = 0.35f))
                ) {
                  Text(
                    text = "🎓 ${task.courseName}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4F46E5),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              // Time badge with clock/calendar icon
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = if (task.isTomorrow) Icons.Outlined.CalendarToday else Icons.Outlined.Schedule,
                  contentDescription = "Time",
                  tint = BrandSecondary,
                  modifier = Modifier.size(11.dp)
                )
                Text(
                  text = if (isDone) "Done" else task.dueTime,
                  fontSize = 10.5.sp,
                  color = BrandSecondary
                )
              }

              // Urgency Badge (e.g., "Due in 4h")
              if (task.urgencyBadge != null && !isDone) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = BrandCanvas,
                  border = BorderStroke(0.5.dp, LinearBorderHairline)
                ) {
                  Text(
                    text = task.urgencyBadge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandSecondary,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                  )
                }
              }

              // Sub-task indicator tag
              if (task.subtaskBadge != null) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = BrandCanvas,
                  border = BorderStroke(0.5.dp, LinearBorderHairline)
                ) {
                  Text(
                    text = task.subtaskBadge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandSecondary,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                  )
                }
              }
            }

            // Sub-task Progress Bar
            if (task.actualSubtasksTotal > 0) {
              val subProgress = task.actualSubtasksCompleted.toFloat() / task.actualSubtasksTotal.toFloat()
              Row(
                modifier = Modifier
                  .padding(top = 8.dp)
                  .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BrandBorderLight)
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxHeight()
                      .fillMaxWidth(subProgress)
                      .clip(RoundedCornerShape(2.dp))
                      .background(if (subProgress >= 1f) LinearDoneGreen else BrandCharcoal)
                  )
                }
                Text(
                  text = "${task.actualSubtasksCompleted}/${task.actualSubtasksTotal}",
                  fontSize = 10.sp,
                  color = BrandSecondary,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }

      // Expandable Subtask checklist & details
      AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(animationSpec = tween(200)) + fadeIn(animationSpec = tween(200)),
        exit = shrinkVertically(animationSpec = tween(180)) + fadeOut(animationSpec = tween(150))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
        ) {
          if (task.description.isNotBlank()) {
            Text(
              text = task.description,
              fontSize = 12.sp,
              color = BrandSecondary,
              lineHeight = 16.sp,
              modifier = Modifier.padding(start = 48.dp, bottom = 8.dp)
            )
          }

          if (task.subtasks.isNotEmpty()) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              task.subtasks.forEach { sub ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(14.dp)
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (sub.isCompleted) LinearDoneGreen else Color.Transparent)
                      .border(1.dp, if (sub.isCompleted) LinearDoneGreen else LinearBacklogGray, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                  ) {
                    if (sub.isCompleted) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                      )
                    }
                  }
                  Text(
                    text = sub.title,
                    fontSize = 12.sp,
                    color = if (sub.isCompleted) BrandSecondary.copy(alpha = 0.6f) else BrandCharcoal,
                    textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                  )
                }
              }
            }
          }

          // Action row inside expanded card
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp, start = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = onClick,
              modifier = Modifier.height(30.dp),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(0.75.dp, LinearBorderHairline)
            ) {
              Text(
                text = "View Details",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = BrandCharcoal
              )
            }

            if (onDeleteClick != null) {
              IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Outlined.DeleteOutline,
                  contentDescription = "Delete",
                  tint = LinearUrgentRed,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }

      // Quick Action Dock when task is tapped/selected (image 2 logic)
      AnimatedVisibility(
        visible = isSelected,
        enter = fadeIn(animationSpec = tween(140)) + expandVertically(animationSpec = tween(180)),
        exit = fadeOut(animationSpec = tween(100)) + shrinkVertically(animationSpec = tween(150))
      ) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
          shape = RoundedCornerShape(10.dp),
          color = BrandPillBg,
          border = BorderStroke(1.dp, BrandBorderLight)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "← Swipe hapus | selesai →",
              fontSize = 10.5.sp,
              color = BrandSecondary,
              fontWeight = FontWeight.Medium
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              // 1-tap complete
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = LinearDoneGreen.copy(alpha = 0.12f),
                border = BorderStroke(0.75.dp, LinearDoneGreen.copy(alpha = 0.35f)),
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .clickable {
                    HapticEngine.success(context, haptic)
                    onToggleComplete()
                  }
              ) {
                Text(
                  text = if (isDone) "Reopen" else "Selesai",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = LinearDoneGreen,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              // 1-tap delete
              if (onDeleteClick != null) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = LinearUrgentRed.copy(alpha = 0.12f),
                  border = BorderStroke(0.75.dp, LinearUrgentRed.copy(alpha = 0.35f)),
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                      onDeleteClick()
                    }
                ) {
                  Text(
                    text = "Hapus",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LinearUrgentRed,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
