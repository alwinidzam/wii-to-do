package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta
import com.example.ui.theme.HapticEngine

enum class NavigationTab(
  val idLabel: String,
  val enLabel: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector
) {
  HOME("Beranda", "Home", Icons.Filled.Home, Icons.Outlined.Home),
  SCHEDULE("Jadwal", "Schedule", Icons.Outlined.CalendarToday, Icons.Outlined.CalendarToday),
  PROJECTS("Proyek", "Projects", Icons.Outlined.Folder, Icons.Outlined.Folder),
  PROFILE("Profil", "Profile", Icons.Outlined.Person, Icons.Outlined.Person);

  fun getLabel(lang: AppLanguage): String = if (lang == AppLanguage.ID) idLabel else enLabel
}

/**
 * Super High-End Floating Navigation Dock & FAB:
 * - Mathematically concentric corner radii: Outer (28dp) - Padding (6dp) = Inner (22dp)
 * - Safe horizontal end cushions preventing active pill from ever clipping or clashing with outer capsule
 * - Zero letter truncation on "Profil" or "Beranda"
 * - Balanced 54dp FAB aligned with 56dp dock
 * - Tactile Apple Taptic Engine feedback on all interactions
 */
@Composable
fun BottomDockNavigation(
  currentTab: NavigationTab,
  onTabSelected: (NavigationTab) -> Unit,
  onAddClick: () -> Unit,
  modifier: Modifier = Modifier,
  currentLanguage: AppLanguage = AppLanguage.ID
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

  val fabInteractionSource = remember { MutableInteractionSource() }
  val isFabPressed by fabInteractionSource.collectIsPressedAsState()
  val fabScale by animateFloatAsState(
    targetValue = if (isFabPressed) 0.92f else 1.0f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
    label = "fab_scale"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, bottom = 16.dp + bottomInset),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Freestanding Frosted Pill Navbar (56dp height, perfectly concentric 28dp radius)
      Surface(
        modifier = Modifier
          .weight(1f)
          .height(56.dp)
          .shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(28.dp),
            ambientColor = Color(0x14000000),
            spotColor = Color(0x18000000)
          ),
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = 0.97f),
        border = BorderStroke(0.75.dp, LinearBorderHairline)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          NavigationTab.values().forEach { tab ->
            val isSelected = (tab == currentTab)
            val tabInteractionSource = remember { MutableInteractionSource() }
            val isTabPressed by tabInteractionSource.collectIsPressedAsState()
            val tabScale by animateFloatAsState(
              targetValue = if (isTabPressed) 0.94f else 1.0f,
              animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
              label = "tab_scale"
            )

            if (isSelected) {
              // Active Tab: Concentric 22dp pill (28dp outer - 6dp margin = 22dp inner)
              Surface(
                modifier = Modifier
                  .testTag("nav_tab_${tab.name.lowercase()}")
                  .graphicsLayer {
                    scaleX = tabScale
                    scaleY = tabScale
                  }
                  .height(44.dp)
                  .clip(RoundedCornerShape(22.dp))
                  .clickable(
                    interactionSource = tabInteractionSource,
                    indication = null
                  ) {
                    HapticEngine.selection(context, haptic)
                    onTabSelected(tab)
                  },
                shape = RoundedCornerShape(22.dp),
                color = BrandCharcoal,
                shadowElevation = 1.dp
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
                ) {
                  Icon(
                    imageVector = tab.activeIcon,
                    contentDescription = tab.getLabel(currentLanguage),
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                  )
                  Spacer(modifier = Modifier.width(5.dp))
                  Text(
                    text = tab.getLabel(currentLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = (-0.01).sp
                  )
                }
              }
            } else {
              // Inactive Tab: Compact 40dp Circular Icon Button
              Box(
                modifier = Modifier
                  .testTag("nav_tab_${tab.name.lowercase()}")
                  .graphicsLayer {
                    scaleX = tabScale
                    scaleY = tabScale
                  }
                  .size(40.dp)
                  .clip(CircleShape)
                  .clickable(
                    interactionSource = tabInteractionSource,
                    indication = null
                  ) {
                    HapticEngine.selection(context, haptic)
                    onTabSelected(tab)
                  },
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = tab.inactiveIcon,
                  contentDescription = tab.getLabel(currentLanguage),
                  tint = BrandSecondary,
                  modifier = Modifier.size(18.dp)
                )

                // Unread dot indicator on Projects tab
                if (tab == NavigationTab.PROJECTS) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .align(Alignment.TopEnd)
                      .padding(top = 4.dp, end = 4.dp)
                      .background(BrandTerracotta, CircleShape)
                  )
                }
              }
            }
          }
        }
      }

      // 2. Primary Action Floating Add Button (Sleek 54dp, Concentric Circular Harmony)
      Surface(
        modifier = Modifier
          .testTag("add_task_fab")
          .size(54.dp)
          .graphicsLayer {
            scaleX = fabScale
            scaleY = fabScale
          }
          .shadow(
            elevation = 12.dp,
            shape = CircleShape,
            ambientColor = Color(0x18000000),
            spotColor = Color(0x22000000)
          )
          .clip(CircleShape)
          .clickable(
            interactionSource = fabInteractionSource,
            indication = null
          ) {
            HapticEngine.impactMedium(context, haptic)
            onAddClick()
          },
        shape = CircleShape,
        color = BrandCharcoal,
        border = BorderStroke(0.75.dp, LinearBorderHairline)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Task",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}
