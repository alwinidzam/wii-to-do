package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.MilestoneTierLevel
import com.example.data.model.UserProfile
import com.example.ui.components.WiiBrandLogo
import com.example.ui.theme.BrandAvatarBg
import com.example.ui.theme.BrandAvatarBorder
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCanvas
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
fun MilestoneJourneyScreen(
  userProfile: UserProfile,
  onBackClick: () -> Unit,
  onOpenShareStudio: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  val currentTier = MilestoneTierLevel.fromXp(userProfile.totalXpAllTime)
  val allTiers = MilestoneTierLevel.entries

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(BrandCanvas)
      .padding(horizontal = 20.dp),
    contentPadding = PaddingValues(
      top = topInset + 12.dp,
      bottom = 120.dp
    ),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Top Navigation Bar
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onBackClick()
            },
          shape = RoundedCornerShape(10.dp),
          color = Color.White,
          border = BorderStroke(1.dp, BrandBorder)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = BrandCharcoal,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          WiiBrandLogo(size = 24.dp)
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "MILESTONE ROAD",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = BrandCharcoal,
              letterSpacing = 0.5.sp
            )
            Text(
              text = "Architect of Focus Journey",
              fontSize = 11.sp,
              color = BrandSecondary
            )
          }
        }

        Surface(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onOpenShareStudio()
            },
          shape = RoundedCornerShape(10.dp),
          color = BrandCharcoal
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share",
              tint = Color.White,
              modifier = Modifier.size(17.dp)
            )
          }
        }
      }
    }

    // 2. Hero Milestone Status Card
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = BrandCharcoal,
        shadowElevation = 2.dp
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.White.copy(alpha = 0.15f)
              ) {
                Text(
                  text = currentTier.badgeCode,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  letterSpacing = 1.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = currentTier.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.02).sp
              )
              Text(
                text = currentTier.subtitle,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.75f)
              )
            }

            Surface(
              modifier = Modifier.size(48.dp),
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.12f)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.WorkspacePremium,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Progress toward next tier
          val tierSpan = (currentTier.maxXp - currentTier.minXp).toFloat().coerceAtLeast(1f)
          val progressInTier = (userProfile.totalXpAllTime - currentTier.minXp).toFloat().coerceIn(0f, tierSpan)
          val fraction = (progressInTier / tierSpan).coerceIn(0f, 1f)

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${userProfile.totalXpAllTime} Total XP",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Target: ${currentTier.maxXp} XP",
              fontSize = 11.sp,
              color = Color.White.copy(alpha = 0.75f)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.2f)
          )

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "“${currentTier.philosophyQuote}”",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            lineHeight = 16.sp
          )
        }
      }
    }

    // 3. Stats Tri-Metric Overview
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Triple("Focus Streak", "${userProfile.streakDays} Days", "consistency"),
          Triple("Focus Time", userProfile.focusHoursLogged, "deep work"),
          Triple("Tasks Done", "${userProfile.tasksDoneCount}", "all-time")
        ).forEach { (title, value, sub) ->
          val isStreak = title == "Focus Streak"
          Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = if (isStreak) Color(0xFFFFFBEB) else Color.White,
            border = BorderStroke(1.dp, if (isStreak) Color(0xFFFDE68A) else BrandBorder)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = title,
                fontSize = 10.sp,
                color = if (isStreak) Color(0xFFB45309) else BrandSecondary,
                fontWeight = FontWeight.Medium
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (isStreak) "$value ✨" else value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStreak) Color(0xFFB45309) else BrandCharcoal
              )
              Text(
                text = sub,
                fontSize = 9.sp,
                color = if (isStreak) Color(0xFFD97706).copy(alpha = 0.8f) else BrandSecondary.copy(alpha = 0.7f)
              )
            }
          }
        }
      }
    }

    // 4. Section Title
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "THE 5 ARCHITECTURAL TIERS",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = BrandSecondary,
          letterSpacing = 0.8.sp
        )
        Text(
          text = "Pure Personal Mastery",
          fontSize = 11.sp,
          color = BrandOlive,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // 5. Tier Milestone Journey Checkpoints
    itemsIndexed(allTiers) { index, tier ->
      val isCompleted = userProfile.totalXpAllTime >= tier.maxXp
      val isActive = userProfile.totalXpAllTime in tier.minXp until tier.maxXp
      val isLocked = userProfile.totalXpAllTime < tier.minXp

      val interactionSource = remember { MutableInteractionSource() }
      val isPressed by interactionSource.collectIsPressedAsState()
      val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "tier_scale"
      )

      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .graphicsLayer {
            scaleX = scale
            scaleY = scale
          }
          .shadow(
            elevation = if (isActive) 3.dp else 1.dp,
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
            onOpenShareStudio()
          },
        shape = RoundedCornerShape(16.dp),
        color = when {
          isActive -> Color.White
          isCompleted -> Color.White
          else -> BrandCanvas
        },
        border = BorderStroke(
          width = if (isActive) 1.5.dp else 1.dp,
          color = when {
            isActive -> BrandCharcoal
            isCompleted -> BrandOliveBorder
            else -> BrandBorderLight
          }
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Status Badge / Icon
              Surface(
                shape = CircleShape,
                color = when {
                  isCompleted -> BrandOliveBg
                  isActive -> BrandCharcoal
                  else -> BrandPillBg
                },
                modifier = Modifier.size(34.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  when {
                    isCompleted -> Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = "Completed",
                      tint = BrandOlive,
                      modifier = Modifier.size(16.dp)
                    )
                    isActive -> Icon(
                      imageVector = Icons.Default.AutoAwesome,
                      contentDescription = "Active Tier",
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                    else -> Icon(
                      imageVector = Icons.Default.Lock,
                      contentDescription = "Locked",
                      tint = BrandSecondary,
                      modifier = Modifier.size(15.dp)
                    )
                  }
                }
              }

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = tier.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLocked) BrandSecondary else BrandCharcoal
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when {
                      isActive -> BrandCharcoal
                      isCompleted -> BrandOliveBg
                      else -> BrandPillBg
                    }
                  ) {
                    Text(
                      text = tier.badgeCode,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = when {
                        isActive -> Color.White
                        isCompleted -> BrandOlive
                        else -> BrandSecondary
                      },
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = tier.subtitle,
                  fontSize = 11.sp,
                  color = BrandSecondary
                )
              }
            }

            // XP Range Tag
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isActive) BrandPillBg else Color.Transparent
            ) {
              Text(
                text = "${tier.minXp} – ${tier.maxXp} XP",
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isLocked) BrandSecondary.copy(alpha = 0.6f) else BrandCharcoal,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = tier.philosophyQuote,
            fontSize = 12.sp,
            color = if (isLocked) BrandSecondary.copy(alpha = 0.7f) else BrandSecondary,
            lineHeight = 16.sp
          )

          if (isActive) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              val remaining = tier.maxXp - userProfile.totalXpAllTime
              Text(
                text = "$remaining XP needed for ${allTiers.getOrNull(index + 1)?.title ?: "Next Level"}",
                fontSize = 11.sp,
                color = BrandOlive,
                fontWeight = FontWeight.SemiBold
              )
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = BrandOliveBg
              ) {
                Text(
                  text = "ACTIVE SPRINT",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = BrandOlive,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }
    }

    // 6. Action: Generate Social Card
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = {
          haptic.performHapticFeedback(HapticFeedbackType.LongPress)
          onOpenShareStudio()
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "Create Aesthetic Social Card",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Export modern Swiss/Bauhaus layouts to Instagram Story or WhatsApp Status",
        fontSize = 11.sp,
        color = BrandSecondary,
        modifier = Modifier.fillMaxWidth(),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }
  }
}
