package com.example.data.auth

import com.example.data.model.UserProfile

sealed class AuthResult {
  data class Success(val profile: UserProfile, val isDeveloper: Boolean = false) : AuthResult()
  data class Error(val message: String, val field: AuthField? = null) : AuthResult()
}

enum class AuthField {
  EMAIL,
  PASSWORD,
  CONFIRM_PASSWORD,
  NAME,
  GENERAL
}
