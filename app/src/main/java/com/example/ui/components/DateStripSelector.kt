package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandPillBg
import com.example.ui.theme.BrandSecondary
import java.util.Calendar

data class DayItem(
  val dayLetter: String,
  val dateNumber: String,
  val isToday: Boolean = false
)

fun getTodayIndex(): Int {
  val cal = Calendar.getInstance()
  val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
  return if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
}

fun getWeekDays(weekOffset: Int = 0): List<DayItem> {
  val calendar = Calendar.getInstance()
  val todayCal = Calendar.getInstance()
  
  val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
  val daysFromMonday = if (currentDayOfWeek == Calendar.SUNDAY) 6 else currentDayOfWeek - Calendar.MONDAY
  calendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday + (weekOffset * 7))

  val letters = listOf("M", "T", "W", "T", "F", "S", "S")
  val list = mutableListOf<DayItem>()
  for (i in 0..6) {
    val dayLetter = letters[i]
    val dateNumber = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val isToday = (calendar.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                   calendar.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR))
    list.add(DayItem(dayLetter, dateNumber, isToday))
    calendar.add(Calendar.DAY_OF_YEAR, 1)
  }
  return list
}

val SampleDays get() = getWeekDays()

/**
 * Super High-End Interactive Micro-Calendar (Horizontal Week Strip):
 * - Container: bg-[#F3F2EE] p-1.5 rounded-2xl border border-brand-border/60
 * - Inactive day: w-11 h-14 rounded-xl text-center (day letter 11sp, date 14sp SemiBold)
 * - Active day: w-12 h-15 bg-brand-charcoal text-white rounded-xl shadow-md scale-105 with white indicator dot
 * - Tactile micro-haptic & spring physics
 */
@Composable
fun DateStripSelector(
  selectedIndex: Int,
  onDaySelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
  days: List<DayItem> = SampleDays
) {
  val haptic = LocalHapticFeedback.current

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = BrandPillBg,
    border = BorderStroke(1.dp, BrandBorder.copy(alpha = 0.6f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 6.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      days.forEachIndexed { index, day ->
        val isSelected = (index == selectedIndex)
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val scale by animateFloatAsState(
          targetValue = if (isPressed) 0.93f else if (isSelected) 1.02f else 1.0f,
          animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
          label = "day_scale"
        )

        val dayBg by animateColorAsState(
          targetValue = if (isSelected) BrandCharcoal else Color.Transparent,
          animationSpec = tween(150)
        )
        val letterColor by animateColorAsState(
          targetValue = if (isSelected) Color.White.copy(alpha = 0.7f) else BrandSecondary,
          animationSpec = tween(150)
        )
        val numberColor by animateColorAsState(
          targetValue = if (isSelected) Color.White else BrandCharcoal,
          animationSpec = tween(150)
        )

        Box(
          modifier = Modifier
            .weight(1f)
            .height(if (isSelected) 60.dp else 56.dp)
            .graphicsLayer {
              scaleX = scale
              scaleY = scale
            }
            .clip(RoundedCornerShape(12.dp))
            .background(dayBg)
            .clickable(
              interactionSource = interactionSource,
              indication = null
            ) {
              if (!isSelected) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onDaySelected(index)
              }
            }
            .then(
              if (isSelected) {
                Modifier.shadow(6.dp, RoundedCornerShape(12.dp), ambientColor = Color(0x20000000), spotColor = Color(0x20000000))
              } else {
                Modifier
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = day.dayLetter,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = letterColor,
              lineHeight = 13.sp
            )
            Text(
              text = day.dateNumber,
              fontSize = if (isSelected) 15.sp else 14.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
              color = numberColor,
              lineHeight = 18.sp
            )
            if (isSelected) {
              Spacer(modifier = Modifier.height(2.dp))
              Box(
                modifier = Modifier
                  .size(4.dp)
                  .background(Color.White, CircleShape)
              )
            }
          }
        }
      }
    }
  }
}
