package com.example.ui.sheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthResult
import com.example.ui.theme.AppleSystemBlue
import com.example.ui.theme.AppleSystemGreen
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandPillBg
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordBottomSheet(
  onDismiss: () -> Unit,
  onChangePassword: suspend (currentPassword: String, newPassword: String) -> AuthResult
) {
  val coroutineScope = rememberCoroutineScope()
  val haptic = LocalHapticFeedback.current
  val focusManager = LocalFocusManager.current
  val scrollState = rememberScrollState()

  var currentPassword by remember { mutableStateOf("") }
  var newPassword by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }

  var currentPasswordVisible by remember { mutableStateOf(false) }
  var newPasswordVisible by remember { mutableStateOf(false) }
  var confirmPasswordVisible by remember { mutableStateOf(false) }

  var errorMessage by remember { mutableStateOf<String?>(null) }
  var successMessage by remember { mutableStateOf<String?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  // Password criteria evaluations
  val hasMinLength = newPassword.length >= 8
  val hasUpperAndLower = newPassword.any { it.isUpperCase() } && newPassword.any { it.isLowerCase() }
  val hasDigit = newPassword.any { it.isDigit() }
  val hasSpecialChar = newPassword.any { !it.isLetterOrDigit() }

  val strengthScore = listOf(hasMinLength, hasUpperAndLower, hasDigit, hasSpecialChar).count { it }
  val strengthColor = when (strengthScore) {
    0, 1 -> BrandTerracotta
    2 -> Color(0xFFD97706) // Amber
    3 -> AppleSystemBlue
    4 -> AppleSystemGreen
    else -> BrandBorderLight
  }

  val strengthLabel = when (strengthScore) {
    0 -> "Sangat Lemah"
    1 -> "Lemah"
    2 -> "Cukup"
    3 -> "Baik"
    4 -> "Sangat Kuat"
    else -> ""
  }

  val passwordsMatch = newPassword.isNotEmpty() && newPassword == confirmPassword
  val canSubmit = currentPassword.isNotEmpty() && hasMinLength && strengthScore >= 2 && passwordsMatch && !isLoading

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = Color.White,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 10.dp, bottom = 6.dp)
          .width(36.dp)
          .height(4.dp)
          .background(BrandBorder, RoundedCornerShape(2.dp))
      )
    },
    modifier = Modifier.imePadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Keamanan Akun",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandOlive,
            letterSpacing = 0.8.sp
          )
          Text(
            text = "Ganti Kata Sandi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal
          )
        }
        Surface(
          shape = CircleShape,
          color = BrandPillBg,
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = BrandCharcoal,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Gunakan kombinasi minimal 8 karakter dengan huruf, angka, dan simbol untuk keamanan maksimal.",
        fontSize = 13.sp,
        color = BrandSecondary,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Error / Success Banner
      AnimatedVisibility(visible = errorMessage != null) {
        errorMessage?.let { error ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 16.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFEE2E2),
            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = BrandTerracotta,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = error,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = BrandTerracotta
              )
            }
          }
        }
      }

      AnimatedVisibility(visible = successMessage != null) {
        successMessage?.let { success ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 16.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFDCFCE7),
            border = BorderStroke(1.dp, Color(0xFF86EFAC))
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AppleSystemGreen,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = success,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AppleSystemGreen
              )
            }
          }
        }
      }

      // 1. Current Password Field
      OutlinedTextField(
        value = currentPassword,
        onValueChange = {
          currentPassword = it
          errorMessage = null
        },
        label = { Text("Kata Sandi Saat Ini") },
        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        trailingIcon = {
          IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
            Icon(
              imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = if (currentPasswordVisible) "Sembunyikan sandi" else "Tampilkan sandi",
              tint = BrandSecondary,
              modifier = Modifier.size(20.dp)
            )
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandCharcoal,
          unfocusedBorderColor = BrandBorder,
          focusedLabelColor = BrandCharcoal,
          unfocusedLabelColor = BrandSecondary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 2. New Password Field
      OutlinedTextField(
        value = newPassword,
        onValueChange = {
          newPassword = it
          errorMessage = null
        },
        label = { Text("Kata Sandi Baru") },
        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        trailingIcon = {
          IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
            Icon(
              imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = if (newPasswordVisible) "Sembunyikan sandi" else "Tampilkan sandi",
              tint = BrandSecondary,
              modifier = Modifier.size(20.dp)
            )
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandCharcoal,
          unfocusedBorderColor = BrandBorder,
          focusedLabelColor = BrandCharcoal,
          unfocusedLabelColor = BrandSecondary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      )

      // Password Strength Meter (Linear / Apple HIG style)
      if (newPassword.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            for (i in 1..4) {
              val isFilled = i <= strengthScore
              val animatedColor by animateColorAsState(
                targetValue = if (isFilled) strengthColor else BrandBorderLight,
                label = "strength_bar"
              )
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp))
                  .background(animatedColor)
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Kekuatan: $strengthLabel",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = strengthColor
            )
          }
        }
      }

      // Password Criteria Checklist
      Spacer(modifier = Modifier.height(10.dp))
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = BrandPillBg,
        border = BorderStroke(1.dp, BrandBorderLight),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          PasswordRequirementItem(label = "Minimal 8 karakter", met = hasMinLength)
          PasswordRequirementItem(label = "Huruf besar & huruf kecil", met = hasUpperAndLower)
          PasswordRequirementItem(label = "Mengandung angka (0-9)", met = hasDigit)
          PasswordRequirementItem(label = "Simbol khusus (!@#$%)", met = hasSpecialChar)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Confirm New Password Field
      OutlinedTextField(
        value = confirmPassword,
        onValueChange = {
          confirmPassword = it
          errorMessage = null
        },
        label = { Text("Konfirmasi Kata Sandi Baru") },
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
          focusManager.clearFocus()
        }),
        trailingIcon = {
          IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
            Icon(
              imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = if (confirmPasswordVisible) "Sembunyikan sandi" else "Tampilkan sandi",
              tint = BrandSecondary,
              modifier = Modifier.size(20.dp)
            )
          }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = if (confirmPassword.isNotEmpty() && !passwordsMatch) BrandTerracotta else BrandCharcoal,
          unfocusedBorderColor = if (confirmPassword.isNotEmpty() && !passwordsMatch) BrandTerracotta else BrandBorder,
          focusedLabelColor = BrandCharcoal,
          unfocusedLabelColor = BrandSecondary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      )

      if (confirmPassword.isNotEmpty() && !passwordsMatch) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Konfirmasi kata sandi tidak cocok",
          fontSize = 11.sp,
          color = BrandTerracotta,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, BrandBorder),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
        ) {
          Text(
            text = "Batal",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandCharcoal
          )
        }

        Button(
          onClick = {
            if (!canSubmit) return@Button
            focusManager.clearFocus()
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
              val result = onChangePassword(currentPassword, newPassword)
              isLoading = false
              when (result) {
                is AuthResult.Success -> {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  successMessage = "Kata sandi berhasil diperbarui!"
                  kotlinx.coroutines.delay(1200)
                  onDismiss()
                }
                is AuthResult.Error -> {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  errorMessage = result.message
                }
              }
            }
          },
          enabled = canSubmit,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = BrandCharcoal,
            disabledContainerColor = BrandBorderLight
          ),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
          } else {
            Text(
              text = "Simpan Sandi",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (canSubmit) Color.White else BrandSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun PasswordRequirementItem(label: String, met: Boolean) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Surface(
      shape = CircleShape,
      color = if (met) AppleSystemGreen.copy(alpha = 0.15f) else Color.Transparent,
      modifier = Modifier.size(16.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = if (met) Icons.Default.Check else Icons.Default.Close,
          contentDescription = null,
          tint = if (met) AppleSystemGreen else BrandSecondary.copy(alpha = 0.5f),
          modifier = Modifier.size(11.dp)
        )
      }
    }
    Text(
      text = label,
      fontSize = 11.5.sp,
      fontWeight = if (met) FontWeight.SemiBold else FontWeight.Normal,
      color = if (met) BrandCharcoal else BrandSecondary
    )
  }
}
