package com.example.ui.sheets

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.BrandBorder
import com.example.ui.theme.BrandBorderLight
import com.example.ui.theme.BrandCharcoal
import com.example.ui.theme.BrandOlive
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandTerracotta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
  profile: UserProfile,
  onDismiss: () -> Unit,
  onSaveProfile: (name: String, university: String, program: String, studentId: String, focusGoal: String) -> Unit
) {
  val haptic = LocalHapticFeedback.current
  val focusManager = LocalFocusManager.current
  val scrollState = rememberScrollState()

  var name by remember { mutableStateOf(profile.name) }
  var university by remember { mutableStateOf(profile.university) }
  var program by remember { mutableStateOf(profile.program) }
  var studentId by remember { mutableStateOf(profile.studentId) }
  var focusGoal by remember { mutableStateOf(profile.focusGoal) }

  var nameError by remember { mutableStateOf<String?>(null) }

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
            text = "INFORMASI AKUN",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = BrandOlive,
            letterSpacing = 0.8.sp
          )
          Text(
            text = "Edit Profil Pribadi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = BrandCharcoal
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 1. Full Name
      OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
          nameError = null
        },
        label = { Text("Nama Lengkap") },
        leadingIcon = {
          Icon(Icons.Default.Person, contentDescription = null, tint = BrandCharcoal, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        isError = nameError != null,
        supportingText = nameError?.let { { Text(it, color = BrandTerracotta) } },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandCharcoal,
          unfocusedBorderColor = BrandBorder
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Program Studi & Semester
      OutlinedTextField(
        value = program,
        onValueChange = { program = it },
        label = { Text("Program Studi & Semester (misal: Teknik Informatika • Semester 5)") },
        leadingIcon = {
          Icon(Icons.Default.School, contentDescription = null, tint = BrandCharcoal, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandCharcoal,
          unfocusedBorderColor = BrandBorder
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 3. Universitas / Institusi
      OutlinedTextField(
        value = university,
        onValueChange = { university = it },
        label = { Text("Universitas / Kampus") },
        leadingIcon = {
          Icon(Icons.Default.School, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandCharcoal,
          unfocusedBorderColor = BrandBorder
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Student ID / NIM
      OutlinedTextField(
        value = studentId,
        onValueChange = { studentId = it },
        label = { Text("Nomor Induk Mahasiswa (NIM)") },
        leadingIcon = {
          Icon(Icons.Default.Badge, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandCharcoal,
          unfocusedBorderColor = BrandBorder
        )
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 5. Focus Goal / Bio Motto
      OutlinedTextField(
        value = focusGoal,
        onValueChange = { focusGoal = it },
        label = { Text("Motto Fokus & Prioritas Belajar") },
        leadingIcon = {
          Icon(Icons.Default.Stars, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(18.dp))
        },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        shape = RoundedCornerShape(12.dp),
        maxLines = 3,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = BrandCharcoal,
          unfocusedBorderColor = BrandBorder
        )
      )

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
          modifier = Modifier.weight(1f).height(48.dp)
        ) {
          Text("Batal", color = BrandCharcoal, fontWeight = FontWeight.SemiBold)
        }

        Button(
          onClick = {
            if (name.trim().length < 2) {
              nameError = "Nama minimal 2 karakter"
              return@Button
            }
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onSaveProfile(name.trim(), university.trim(), program.trim(), studentId.trim(), focusGoal.trim())
            onDismiss()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = BrandCharcoal),
          modifier = Modifier.weight(1f).height(48.dp)
        ) {
          Text("Simpan Profil", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}
