package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import com.example.data.auth.AuthField
import com.example.data.auth.AuthResult
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WiiBrandLogo
import com.example.ui.theme.OliveSecondary
import com.example.ui.theme.OliveSecondaryContainer

@Composable
fun OnboardingScreen(
  onGetStarted: () -> Unit,
  onSignInClick: () -> Unit,
  onGuestSignIn: () -> Unit = onGetStarted,
  modifier: Modifier = Modifier
) {
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFAF9F7))
      .padding(horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(topInset + 32.dp))

    WiiBrandLogo(size = 56.dp)

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "Less noise.\nMore done.",
      fontSize = 36.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF060607),
      lineHeight = 42.sp,
      letterSpacing = (-0.03).sp,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Minimalist architectural productivity for university engineering and design sprint flows.",
      fontSize = 14.sp,
      color = Color(0xFF747878),
      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
      lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(36.dp))

    // 3 Feature Showcase Cards
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      FeatureHighlightCard(
        icon = Icons.Default.Bolt,
        title = "Focus Pomodoro Engine",
        subtitle = "Distraction-free timer, tactile haptics, and deep flow tracking."
      )
      FeatureHighlightCard(
        icon = Icons.Default.ViewKanban,
        title = "Architectural Kanban",
        subtitle = "Semantic design token tags and subtask checklists."
      )
      FeatureHighlightCard(
        icon = Icons.Default.CalendarToday,
        title = "Campus Calendar Sync",
        subtitle = "Unified schedule timeline from 08:00 to 20:00."
      )
    }

    Spacer(modifier = Modifier.weight(1f))

    // Actions
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = onGetStarted,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060607))
      ) {
        Text(
          text = "Get Started",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      OutlinedButton(
        onClick = onSignInClick,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = "Sign In with Existing Account",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF060607)
        )
      }

      OutlinedButton(
        onClick = onGuestSignIn,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
      ) {
        Text(
          text = "Continue as Guest (Offline Mode)",
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF747878)
        )
      }
    }
  }
}

@Composable
fun SignInScreen(
  onBackClick: () -> Unit,
  onSignInAttempt: suspend (email: String, password: String) -> AuthResult,
  onSignUpClick: () -> Unit,
  onGuestSignIn: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val coroutineScope = rememberCoroutineScope()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFAF9F7))
      .padding(horizontal = 24.dp)
  ) {
    Spacer(modifier = Modifier.height(topInset + 12.dp))

    Surface(
      modifier = Modifier
        .size(40.dp)
        .clip(RoundedCornerShape(10.dp))
        .clickable { onBackClick() },
      shape = RoundedCornerShape(10.dp),
      color = Color(0xFFFFFFFF),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFEFEF))
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Back",
          tint = Color(0xFF060607),
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "Welcome Back",
      fontSize = 26.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF060607),
      letterSpacing = (-0.02).sp
    )

    Text(
      text = "Sign in to access your wii to do workspace.",
      fontSize = 13.sp,
      color = Color(0xFF747878)
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Animated Error Banner
    AnimatedVisibility(
      visible = errorMessage != null,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically()
    ) {
      errorMessage?.let { error ->
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFFDE8E8),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF8B4B4))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = null,
              tint = Color(0xFF9B1C1C),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = error,
              color = Color(0xFF9B1C1C),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    OutlinedTextField(
      value = email,
      onValueChange = {
        email = it
        errorMessage = null
      },
      label = { Text("Email Address") },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
      colors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFFFFFFF),
        unfocusedContainerColor = Color(0xFFFFFFFF),
        focusedIndicatorColor = Color(0xFF060607),
        unfocusedIndicatorColor = Color(0xFFE3E2E0)
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = password,
      onValueChange = {
        password = it
        errorMessage = null
      },
      label = { Text("Password") },
      visualTransformation = if (passwordVisible) VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
      trailingIcon = {
        IconButton(onClick = { passwordVisible = !passwordVisible }) {
          Icon(
            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
            contentDescription = if (passwordVisible) "Hide password" else "Show password",
            tint = Color(0xFF747878),
            modifier = Modifier.size(20.dp)
          )
        }
      },
      singleLine = true,
      colors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFFFFFFF),
        unfocusedContainerColor = Color(0xFFFFFFFF),
        focusedIndicatorColor = Color(0xFF060607),
        unfocusedIndicatorColor = Color(0xFFE3E2E0)
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(22.dp))

    Button(
      onClick = {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()
        if (cleanEmail.isBlank()) {
          errorMessage = "Silakan masukkan alamat email Anda."
          return@Button
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
          errorMessage = "Format email tidak valid (contoh: user@domain.com)."
          return@Button
        }
        if (cleanPassword.isBlank()) {
          errorMessage = "Silakan masukkan kata sandi akun Anda."
          return@Button
        }
        isLoading = true
        coroutineScope.launch {
          val result = onSignInAttempt(cleanEmail, cleanPassword)
          isLoading = false
          if (result is AuthResult.Error) {
            errorMessage = result.message
          }
        }
      },
      enabled = !isLoading,
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp),
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060607))
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          color = Color.White,
          strokeWidth = 2.dp
        )
      } else {
        Text(
          text = "Sign In",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedButton(
      onClick = onGuestSignIn,
      modifier = Modifier
        .fillMaxWidth()
        .height(46.dp),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEBEBEB)),
      colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
    ) {
      Text(
        text = "Continue as Guest (Offline Mode)",
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF747878)
      )
    }

    Spacer(modifier = Modifier.weight(1f))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 24.dp),
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = "Don't have an account? ",
        fontSize = 13.sp,
        color = Color(0xFF747878)
      )
      Text(
        text = "Create Workspace",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF060607),
        modifier = Modifier.clickable { onSignUpClick() }
      )
    }
  }
}

@Composable
fun SignUpScreen(
  onBackClick: () -> Unit,
  onSignUpAttempt: suspend (name: String, email: String, password: String, confirmPassword: String, program: String) -> AuthResult,
  onSignInClick: () -> Unit,
  onGuestSignIn: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var program by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var confirmPasswordVisible by remember { mutableStateOf(false) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val coroutineScope = rememberCoroutineScope()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFAF9F7))
      .padding(horizontal = 24.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(topInset + 12.dp))

      Surface(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(10.dp))
          .clickable { onBackClick() },
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFEFEF))
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF060607),
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "Create Workspace",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF060607),
        letterSpacing = (-0.02).sp
      )

      Text(
        text = "Set up your account for a clean focus workspace.",
        fontSize = 13.sp,
        color = Color(0xFF747878)
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Animated Error Banner
      AnimatedVisibility(
        visible = errorMessage != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        errorMessage?.let { error ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 14.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFDE8E8),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF8B4B4))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Color(0xFF9B1C1C),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = error,
                color = Color(0xFF9B1C1C),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }

    item {
      OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
          errorMessage = null
        },
        label = { Text("Full Name") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = email,
        onValueChange = {
          email = it
          errorMessage = null
        },
        label = { Text("Email Address") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = password,
        onValueChange = {
          password = it
          errorMessage = null
        },
        label = { Text("Password (min. 6 characters)") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        trailingIcon = {
          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
              imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = if (passwordVisible) "Hide password" else "Show password",
              tint = Color(0xFF747878),
              modifier = Modifier.size(20.dp)
            )
          }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = confirmPassword,
        onValueChange = {
          confirmPassword = it
          errorMessage = null
        },
        label = { Text("Confirm Password") },
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        trailingIcon = {
          IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
            Icon(
              imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
              contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
              tint = Color(0xFF747878),
              modifier = Modifier.size(20.dp)
            )
          }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = program,
        onValueChange = {
          program = it
          errorMessage = null
        },
        label = { Text("Program / Role (Optional)") },
        placeholder = { Text("e.g. Informatics Engineering, Designer") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = {
          val cleanName = name.trim()
          val cleanEmail = email.trim()
          val cleanPassword = password.trim()
          val cleanConfirm = confirmPassword.trim()
          val cleanProgram = program.trim().ifBlank { "Personal Workspace" }

          if (cleanName.isBlank()) {
            errorMessage = "Silakan masukkan nama lengkap Anda."
            return@Button
          }
          if (cleanEmail.isBlank()) {
            errorMessage = "Silakan masukkan alamat email."
            return@Button
          }
          if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            errorMessage = "Format email tidak valid (contoh: user@domain.com)."
            return@Button
          }
          if (cleanPassword.length < 6) {
            errorMessage = "Kata sandi minimal 6 karakter."
            return@Button
          }
          if (cleanPassword != cleanConfirm) {
            errorMessage = "Konfirmasi kata sandi tidak cocok."
            return@Button
          }

          isLoading = true
          coroutineScope.launch {
            val result = onSignUpAttempt(cleanName, cleanEmail, cleanPassword, cleanConfirm, cleanProgram)
            isLoading = false
            if (result is AuthResult.Error) {
              errorMessage = result.message
            }
          }
        },
        enabled = !isLoading,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060607))
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = Color.White,
            strokeWidth = 2.dp
          )
        } else {
          Text(
            text = "Create Workspace",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedButton(
        onClick = onGuestSignIn,
        modifier = Modifier
          .fillMaxWidth()
          .height(46.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEBEBEB)),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
      ) {
        Text(
          text = "Continue as Guest (Offline Mode)",
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF747878)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.Center
      ) {
        Text(
          text = "Already have an account? ",
          fontSize = 13.sp,
          color = Color(0xFF747878)
        )
        Text(
          text = "Sign In",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF060607),
          modifier = Modifier.clickable { onSignInClick() }
        )
      }
    }
  }
}

@Composable
fun FeatureHighlightCard(
  icon: ImageVector,
  title: String,
  subtitle: String
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFFFFFFFF),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEFEFEF))
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = OliveSecondaryContainer
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF596751),
            modifier = Modifier.size(20.dp)
          )
        }
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF060607)
        )
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = Color(0xFF747878)
        )
      }
    }
  }
}
