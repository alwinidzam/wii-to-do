package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCanvas
import com.example.ui.theme.BrandCard
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandCheckboxBorder
import com.example.ui.theme.BrandDotNeutral
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTagBg
import com.example.ui.theme.BrandTerracotta
import com.example.ui.theme.BrandTerracottaBg
import kotlinx.coroutines.delay

/**
 * TaskCardItem matching the reference HTML/CSS specification:
 * - Container: bg-white border border-brand-border rounded-2xl p-4 shadow-card
 * - Custom Architectural Checkbox: 20x20 rounded-lg (6dp radius) with border-2 #D4D2CB
 * - Task Title: 14sp SemiBold (600) text-brand-charcoal
 * - Tags & Metadata:
 *   - Category tag: bg #F2F1ED text-brand-charcoal
 *   - Time: clock/calendar icon + time (text-brand-secondary)
 *   - High priority tag: text-brand-terracotta bg-brand-terracottaBg "● High priority"
 *   - Urgency tag: "Due in 4h"
 *   - Attachment tag: "3 files" with paperclip icon
 *   - Sub-task tag: "1/3 sub-tasks"
 * - Sub-task Progress Bar: 96x6dp bar with brand-charcoal progress
 * - Explicit Delete Button: Triggers smooth slide-out animation to the right before task removal
 * - Priority Dot: 8dp circle (Terracotta, Neutral, or Light border)
 */
@Composable
fun TaskCardItem(
  task: TaskItem,
  onToggleComplete: () -> Unit,
  onClick: () -> Unit = {},
  modifier: Modifier = Modifier,
  onDelete: (() -> Unit)? = null
) {
  val haptic = LocalHapticFeedback.current
  var isExpanded by remember { mutableStateOf(false) }
  var isDeleting by remember { mutableStateOf(false) }

  val isDone = task.isCompleted

  val titleColor = if (isDone) BrandSecondary.copy(alpha = 0.6f) else BrandCharcoal
  val textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None

  // Once slide-out completes, trigger the repository delete callback
  LaunchedEffect(isDeleting) {
    if (isDeleting) {
      delay(320)
      onDelete?.invoke()
    }
  }

  AnimatedVisibility(
    visible = !isDeleting,
    enter = fadeIn(animationSpec = tween(200)),
    exit = slideOutHorizontally(
      targetOffsetX = { fullWidth -> fullWidth },
      animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(durationMillis = 200)) + shrinkVertically(animationSpec = tween(durationMillis = 300)),
    modifier = modifier
  ) {
    TaskCardContent(
      task = task,
      isDone = isDone,
      titleColor = titleColor,
      textDecoration = textDecoration,
      isExpanded = isExpanded,
      onToggleExpand = { isExpanded = !isExpanded },
      onToggleComplete = onToggleComplete,
      onClick = onClick,
      onDeleteClick = if (onDelete != null) {
        {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          isDeleting = true
        }
      } else null
    )
  }
}

@Composable
private fun TaskCardContent(
  task: TaskItem,
  isDone: Boolean,
  titleColor: Color,
  textDecoration: TextDecoration,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onToggleComplete: () -> Unit,
  onClick: () -> Unit,
  onDeleteClick: (() -> Unit)?,
  modifier: Modifier = Modifier
) {
  val cardElevation = if (isDone) 0.dp else 2.dp
  val cardAlpha = if (isDone) 0.65f else 1.0f

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("task_card_${task.id}")
      .shadow(
        elevation = cardElevation,
        shape = RoundedCornerShape(16.dp),
        ambientColor = Color(0x0A000000),
        spotColor = Color(0x08000000)
      )
      .animateContentSize(animationSpec = tween(durationMillis = 200)),
    shape = RoundedCornerShape(16.dp),
    color = BrandCard.copy(alpha = cardAlpha),
    border = BorderStroke(1.dp, BrandBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onToggleExpand() }
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        // Left side: Checkbox + Content
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalAlignment = Alignment.Top
        ) {
          // Architectural Checkbox with tactile spring bounce & 36dp touch target
          val checkboxScale by animateFloatAsState(
            targetValue = if (isDone) 1.15f else 1.0f,
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioMediumBouncy,
              stiffness = Spring.StiffnessLow
            ),
            label = "checkbox_spring"
          )
          val checkboxBg by animateColorAsState(
            targetValue = if (isDone) BrandCharcoal else Color.Transparent,
            animationSpec = tween(durationMillis = 150)
          )
          val checkboxBorderColor by animateColorAsState(
            targetValue = if (isDone) BrandCharcoal else BrandCheckboxBorder,
            animationSpec = tween(durationMillis = 150)
          )

          Box(
            modifier = Modifier
              .testTag("task_checkbox_${task.id}")
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onToggleComplete()
              },
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .graphicsLayer {
                  scaleX = checkboxScale
                  scaleY = checkboxScale
                }
                .clip(RoundedCornerShape(6.dp))
                .background(checkboxBg)
                .then(
                  if (!isDone) Modifier.background(Color.White, RoundedCornerShape(6.dp))
                  else Modifier
                ),
              contentAlignment = Alignment.Center
            ) {
              Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(6.dp),
                color = checkboxBg,
                border = BorderStroke(2.dp, checkboxBorderColor)
              ) {
                if (isDone) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = "Completed",
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
              }
            }
          }

          // Center: Title, Metadata & Subtask Progress
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = task.title,
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = titleColor,
              textDecoration = textDecoration,
              letterSpacing = (-0.01).sp,
              maxLines = if (isExpanded) Int.MAX_VALUE else 2,
              overflow = TextOverflow.Ellipsis
            )

            // Responsive FlowRow for Tags (wraps cleanly on mobile without clipping)
            FlowRow(
              modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              // Category Tag
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = BrandTagBg
              ) {
                Text(
                  text = task.category,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = BrandCharcoal,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
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
                  modifier = Modifier.size(12.dp)
                )
                Text(
                  text = if (isDone) "Completed" else task.dueTime,
                  fontSize = 11.sp,
                  color = BrandSecondary
                )
              }

              // High Priority Tag
              if (task.priority == TaskPriority.HIGH && !isDone) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = BrandTerracottaBg
                ) {
                  Text(
                    text = "● High priority",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandTerracotta,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                  )
                }
              }

              // Urgency Badge (e.g., "Due in 4h")
              if (task.urgencyBadge != null && !isDone) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = BrandCanvas,
                  border = BorderStroke(1.dp, BrandBorderLight)
                ) {
                  Text(
                    text = task.urgencyBadge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              // Attachment Indicator (e.g., "3 files")
              if (task.attachmentCount != null && !isDone) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = Color(0xFFF8F7F4),
                  border = BorderStroke(1.dp, BrandBorderLight)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.AttachFile,
                      contentDescription = "Attachments",
                      tint = BrandSecondary,
                      modifier = Modifier.size(11.dp)
                    )
                    Text(
                      text = "${task.attachmentCount} files",
                      fontSize = 10.sp,
                      color = BrandSecondary
                    )
                  }
                }
              }

              // Sub-task Badge (e.g., "1/3 sub-tasks")
              if (task.actualSubtaskBadge != null && !isDone) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = BrandCanvas,
                  border = BorderStroke(1.dp, BrandBorderLight)
                ) {
                  Text(
                    text = task.actualSubtaskBadge!!,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = BrandSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }

            // Micro Sub-task Progress Bar
            if (task.actualSubtasksTotal > 0 && !isDone) {
              val completed = task.actualSubtasksCompleted
              val total = task.actualSubtasksTotal
              val fraction = (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)

              Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .width(96.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(BrandBorderLight)
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxHeight()
                      .fillMaxWidth(fraction)
                      .clip(RoundedCornerShape(3.dp))
                      .background(BrandCharcoal)
                  )
                }

                Text(
                  text = "$completed of $total sub-tasks",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = BrandSecondary
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Right side: Delete Button & Priority Dot
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Explicit Delete Button
          if (onDeleteClick != null) {
            IconButton(
              onClick = onDeleteClick,
              modifier = Modifier
                .testTag("delete_task_${task.id}")
                .size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = "Delete task",
                tint = BrandSecondary.copy(alpha = 0.45f),
                modifier = Modifier.size(16.dp)
              )
            }
          }

          val dotColor = when {
            isDone -> Color.Transparent
            task.priority == TaskPriority.HIGH -> BrandTerracotta
            task.priority == TaskPriority.MED -> BrandDotNeutral
            else -> BrandBorder
          }

          if (!isDone) {
            Box(
              modifier = Modifier
                .padding(end = 2.dp)
                .size(8.dp)
                .background(dotColor, CircleShape)
            )
          }
        }
      }

      // Expandable section for full description or actions if user taps
      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
        ) {
          if (task.description.isNotBlank()) {
            Text(
              text = task.description,
              fontSize = 12.sp,
              color = BrandSecondary,
              lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (onDeleteClick != null) {
              OutlinedButton(
                onClick = onDeleteClick,
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = BrandTerracotta
                ),
                border = BorderStroke(1.dp, BrandTerracotta.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "Delete",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }

            Button(
              onClick = onClick,
              colors = ButtonDefaults.buttonColors(
                containerColor = BrandCharcoal,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "Open Details",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }
  }
}
