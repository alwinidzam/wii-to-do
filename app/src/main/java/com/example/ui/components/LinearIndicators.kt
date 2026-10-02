package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.KanbanColumn
import com.example.data.model.TaskPriority
import com.example.ui.theme.AppleSystemGreen
import com.example.ui.theme.HapticEngine

// Linear Semantic Colors
val LinearDoneGreen = Color(0xFF27AE60)
val LinearInProgressBlue = Color(0xFF5E6AD2)
val LinearInProgressAmber = Color(0xFFF2994A)
val LinearBacklogGray = Color(0xFF8A8F98)
val LinearBorderHairline = Color(0xFFE2E4E8)
val LinearUrgentRed = Color(0xFFEB5757)
val LinearHighOrange = Color(0xFFF2994A)
val LinearLowGray = Color(0xFF8A8F98)

/**
 * Linear-style semantic status ring icon with tactile bounce.
 * Statuses:
 * - DONE: Solid emerald green circle with white check
 * - IN_PROGRESS: Circle with 50% pie-slice fill
 * - TO_DO: Clean hollow circle with 1.5dp hairline border
 * - BACKLOG: Dashed circular ring
 * - CANCELED: Circle with diagonal slash
 */
@Composable
fun LinearStatusRing(
  status: KanbanColumn,
  isCompleted: Boolean,
  modifier: Modifier = Modifier,
  size: Dp = 20.dp,
  onClick: (() -> Unit)? = null
) {
  val context = LocalContext.current
  val isDone = isCompleted || status == KanbanColumn.DONE

  val scale by animateFloatAsState(
    targetValue = if (isDone) 1.15f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow
    ),
    label = "linear_status_scale"
  )

  val ringColor by animateColorAsState(
    targetValue = when {
      isDone -> LinearDoneGreen
      status == KanbanColumn.IN_PROGRESS -> LinearInProgressAmber
      else -> LinearBacklogGray
    },
    animationSpec = tween(durationMillis = 180),
    label = "linear_status_color"
  )

  Box(
    modifier = modifier
      .size(size)
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .then(
        if (onClick != null) {
          Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) {
            HapticEngine.selection(context)
            onClick()
          }
        } else Modifier
      ),
    contentAlignment = Alignment.Center
  ) {
    if (isDone) {
      // Solid filled circle with checkmark
      Canvas(modifier = Modifier.size(size)) {
        drawCircle(
          color = LinearDoneGreen,
          radius = size.toPx() / 2f
        )
      }
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Done",
        tint = Color.White,
        modifier = Modifier.size(size * 0.7f)
      )
    } else {
      Canvas(modifier = Modifier.size(size)) {
        val strokeWidth = 1.6.dp.toPx()
        val radius = (size.toPx() - strokeWidth) / 2f
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)

        when (status) {
          KanbanColumn.IN_PROGRESS -> {
            // Hollow ring + 50% pie slice fill
            drawCircle(
              color = LinearInProgressAmber.copy(alpha = 0.35f),
              radius = radius,
              center = center,
              style = Stroke(width = strokeWidth)
            )
            drawArc(
              color = LinearInProgressAmber,
              startAngle = -90f,
              sweepAngle = 180f,
              useCenter = true,
              topLeft = Offset(center.x - radius, center.y - radius),
              size = Size(radius * 2f, radius * 2f)
            )
          }

          KanbanColumn.BACKLOG -> {
            // Dashed stroke circle
            drawCircle(
              color = LinearBacklogGray,
              radius = radius,
              center = center,
              style = Stroke(
                width = strokeWidth,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 4f), 0f)
              )
            )
          }

          KanbanColumn.CANCELED -> {
            // Hollow circle with diagonal slash
            drawCircle(
              color = LinearBacklogGray.copy(alpha = 0.5f),
              radius = radius,
              center = center,
              style = Stroke(width = strokeWidth)
            )
            drawLine(
              color = LinearBacklogGray,
              start = Offset(center.x - radius * 0.7f, center.y + radius * 0.7f),
              end = Offset(center.x + radius * 0.7f, center.y - radius * 0.7f),
              strokeWidth = strokeWidth,
              cap = StrokeCap.Round
            )
          }

          KanbanColumn.TO_DO, KanbanColumn.DONE -> {
            // Clean hollow circle
            drawCircle(
              color = LinearBacklogGray.copy(alpha = 0.75f),
              radius = radius,
              center = center,
              style = Stroke(width = strokeWidth)
            )
          }
        }
      }
    }
  }
}

/**
 * Linear-style 3-bar cellular priority signal.
 * - Urgent: Red exclamation mark (#EB5757)
 * - High: 3 ascending bars active (#EB5757)
 * - Medium: 2 bars active (#F2994A)
 * - Low: 1 bar active (#8A8F98)
 * - None: 3 muted background bars
 */
@Composable
fun LinearPrioritySignal(
  priority: TaskPriority,
  modifier: Modifier = Modifier,
  isUrgent: Boolean = false
) {
  if (isUrgent || priority == TaskPriority.HIGH) {
    Row(
      modifier = modifier,
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.spacedBy(1.5.dp)
    ) {
      Box(
        modifier = Modifier
          .size(width = 3.dp, height = 4.dp)
          .graphicsLayer { clip = true }
      ) {
        Canvas(modifier = Modifier.size(width = 3.dp, height = 4.dp)) {
          drawRoundRect(color = LinearUrgentRed, cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
        }
      }
      Box(
        modifier = Modifier
          .size(width = 3.dp, height = 7.dp)
          .graphicsLayer { clip = true }
      ) {
        Canvas(modifier = Modifier.size(width = 3.dp, height = 7.dp)) {
          drawRoundRect(color = LinearUrgentRed, cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
        }
      }
      Box(
        modifier = Modifier
          .size(width = 3.dp, height = 11.dp)
          .graphicsLayer { clip = true }
      ) {
        Canvas(modifier = Modifier.size(width = 3.dp, height = 11.dp)) {
          drawRoundRect(color = LinearUrgentRed, cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
        }
      }
    }
  } else if (priority == TaskPriority.MED) {
    Row(
      modifier = modifier,
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.spacedBy(1.5.dp)
    ) {
      Box(
        modifier = Modifier.size(width = 3.dp, height = 4.dp)
      ) {
        Canvas(modifier = Modifier.size(width = 3.dp, height = 4.dp)) {
          drawRoundRect(color = LinearHighOrange, cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
        }
      }
      Box(
        modifier = Modifier.size(width = 3.dp, height = 7.dp)
      ) {
        Canvas(modifier = Modifier.size(width = 3.dp, height = 7.dp)) {
          drawRoundRect(color = LinearHighOrange, cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
        }
      }
      Box(
        modifier = Modifier.size(width = 3.dp, height = 11.dp)
      ) {
        Canvas(modifier = Modifier.size(width = 3.dp, height = 11.dp)) {
          drawRoundRect(color = LinearBacklogGray.copy(alpha = 0.25f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
        }
      }
    }
  } else {
    // Low / None
    Row(
      modifier = modifier,
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.spacedBy(1.5.dp)
    ) {
      Box(
        modifier = Modifier.size(width = 3.dp, height = 4.dp)
      ) {
        Canvas(modifier = Modifier.size(width = 3.dp, height = 4.dp)) {
          drawRoundRect(
            color = if (priority == TaskPriority.LOW) LinearLowGray else LinearBacklogGray.copy(alpha = 0.25f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx())
          )
        }
      }
      Box(
        modifier = Modifier.size(width = 3.dp, height = 7.dp)
      ) {
        Canvas(modifier = Modifier.size(width = 3.dp, height = 7.dp)) {
          drawRoundRect(color = LinearBacklogGray.copy(alpha = 0.25f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
        }
      }
      Box(
        modifier = Modifier.size(width = 3.dp, height = 11.dp)
      ) {
        Canvas(modifier = Modifier.size(width = 3.dp, height = 11.dp)) {
          drawRoundRect(color = LinearBacklogGray.copy(alpha = 0.25f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
        }
      }
    }
  }
}
