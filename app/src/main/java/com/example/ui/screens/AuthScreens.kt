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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
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
  onSignInSuccess: (email: String, password: String) -> Unit,
  onSignUpClick: () -> Unit,
  onGoogleSignIn: () -> Unit = { onSignInSuccess("alwi.student@university.edu", "") },
  onGuestSignIn: () -> Unit = { onSignInSuccess("guest@wiitodo.app", "") },
  modifier: Modifier = Modifier
) {
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFAF9F7))
      .padding(horizontal = 24.dp)
  ) {
    Spacer(modifier = Modifier.height(topInset + 16.dp))

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

    Spacer(modifier = Modifier.height(28.dp))

    Text(
      text = "Welcome Back",
      fontSize = 28.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF060607)
    )

    Text(
      text = "Sign in to sync your university schedule and projects.",
      fontSize = 13.sp,
      color = Color(0xFF747878)
    )

    Spacer(modifier = Modifier.height(28.dp))

    OutlinedTextField(
      value = email,
      onValueChange = { email = it },
      label = { Text("University Email or ID") },
      singleLine = true,
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
      onValueChange = { password = it },
      label = { Text("Password") },
      visualTransformation = PasswordVisualTransformation(),
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
      singleLine = true,
      colors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFFFFFFF),
        unfocusedContainerColor = Color(0xFFFFFFFF),
        focusedIndicatorColor = Color(0xFF060607),
        unfocusedIndicatorColor = Color(0xFFE3E2E0)
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(20.dp))

    Button(
      onClick = {
        if (email.isNotBlank()) {
          onSignInSuccess(email.trim(), password.trim())
        }
      },
      enabled = email.isNotBlank(),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp),
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060607))
    ) {
      Text(
        text = "Continue",
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

    Spacer(modifier = Modifier.height(14.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .height(1.dp)
          .background(Color(0xFFE5E5E5))
      )
      Text(
        text = "OR",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFA0A0A0),
        modifier = Modifier.padding(horizontal = 12.dp)
      )
      Box(
        modifier = Modifier
          .weight(1f)
          .height(1.dp)
          .background(Color(0xFFE5E5E5))
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedButton(
      onClick = onGoogleSignIn,
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4D4D4)),
      colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AccountCircle,
          contentDescription = "Google",
          tint = Color(0xFF4285F4),
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "Continue with Google",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF1F1F1F)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

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

    Spacer(modifier = Modifier.height(20.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
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
  onSignUpSuccess: (name: String, email: String, program: String) -> Unit,
  onGoogleSignIn: () -> Unit = { onSignUpSuccess("Alwi Pratama (Google)", "alwi.student@university.edu", "Informatics Engineering • Year 3") },
  onGuestSignIn: () -> Unit = { onSignUpSuccess("Guest Scholar", "guest@wiitodo.app", "Offline Focus Workspace") },
  modifier: Modifier = Modifier
) {
  val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var program by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFFAF9F7))
      .padding(horizontal = 24.dp)
  ) {
    Spacer(modifier = Modifier.height(topInset + 16.dp))

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

    Spacer(modifier = Modifier.height(28.dp))

    Text(
      text = "Create Workspace",
      fontSize = 28.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF060607)
    )

    Text(
      text = "Join 10,000+ engineers & designers mastering focus.",
      fontSize = 13.sp,
      color = Color(0xFF747878)
    )

    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
      value = name,
      onValueChange = { name = it },
      label = { Text("Full Name") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = email,
      onValueChange = { email = it },
      label = { Text("University / Work Email") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = program,
      onValueChange = { program = it },
      label = { Text("Program / Role") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
      onClick = {
        if (name.isNotBlank() && email.isNotBlank()) {
          onSignUpSuccess(name.trim(), email.trim(), program.trim())
        }
      },
      enabled = name.isNotBlank() && email.isNotBlank(),
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp),
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF060607))
    ) {
      Text(
        text = "Start Focus Architecture",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .height(1.dp)
          .background(Color(0xFFE5E5E5))
      )
      Text(
        text = "OR",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFA0A0A0),
        modifier = Modifier.padding(horizontal = 12.dp)
      )
      Box(
        modifier = Modifier
          .weight(1f)
          .height(1.dp)
          .background(Color(0xFFE5E5E5))
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedButton(
      onClick = onGoogleSignIn,
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4D4D4)),
      colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AccountCircle,
          contentDescription = "Google",
          tint = Color(0xFF4285F4),
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "Sign Up with Google",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF1F1F1F)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

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
