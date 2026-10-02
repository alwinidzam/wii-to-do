package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.OliveSecondary
import com.example.ui.theme.OliveSecondaryContainer

@Composable
fun LevelUpDialog(
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(24.dp, RoundedCornerShape(20.dp)),
      shape = RoundedCornerShape(20.dp),
      color = Color(0xFF1F1F1F)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Icon badge with glow
        Surface(
          modifier = Modifier.size(68.dp),
          shape = CircleShape,
          color = Color(0xFFD5E5C9)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.WorkspacePremium,
              contentDescription = null,
              tint = Color(0xFF596751),
              modifier = Modifier.size(36.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "LEVEL 5 UNLOCKED",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFFD5E5C9),
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Focus Architect",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "You've logged 18+ hours of deep flow and completed 43 tasks on time.",
          fontSize = 12.sp,
          color = Color.White.copy(alpha = 0.75f),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Full XP Bar
        LinearProgressIndicator(
          progress = { 1.0f },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = Color(0xFFD5E5C9),
          trackColor = Color.White.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Perks List
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = Color.White.copy(alpha = 0.08f)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "UNLOCKED PERKS",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White.copy(alpha = 0.6f),
              letterSpacing = 0.8.sp
            )
            listOf(
              "Apple Haptic Engine & Focus Metrics",
              "Architectural Token Matrix Tags (#token/dark-matrix)",
              "1.25x Productivity Multiplier"
            ).forEach { perk ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = Color(0xFFD5E5C9),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = perk,
                  fontSize = 11.sp,
                  color = Color.White,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5E5C9))
        ) {
          Text(
            text = "Klaim & Lanjutkan",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF596751)
          )
        }
      }
    }
  }
}
