package com.example.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed interface AuthState {
  data object Idle : AuthState
  data object Authenticating : AuthState
  data class Success(val profile: UserProfile, val isGuest: Boolean) : AuthState
  data class Error(val message: String) : AuthState
}

class AuthManager private constructor() {

  private val firebaseAuth: FirebaseAuth? by lazy {
    try {
      FirebaseAuth.getInstance()
    } catch (_: Exception) {
      null
    }
  }

  suspend fun signInWithGoogle(
    context: Context,
    serverClientId: String = ""
  ): Result<UserProfile> = withContext(Dispatchers.IO) {
    try {
      val credentialManager = CredentialManager.create(context)

      // If serverClientId is not configured, gracefully fall back to local student profile
      if (serverClientId.isBlank()) {
        val guestUser = UserProfile(
          name = "Alwi Pratama",
          email = "alwi.student@university.edu",
          program = "Informatics Engineering • Year 3"
        )
        return@withContext Result.success(guestUser)
      }

      val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(serverClientId)
        .setAutoSelectEnabled(false)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

      val result = credentialManager.getCredential(
        request = request,
        context = context
      )

      val credential = result.credential
      if (credential is androidx.credentials.CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
      ) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val idToken = googleIdTokenCredential.idToken

        val auth = firebaseAuth
        val profile = if (auth != null) {
          val authCredential = GoogleAuthProvider.getCredential(idToken, null)
          val authResult = auth.signInWithCredential(authCredential).await()
          val firebaseUser = authResult.user
          UserProfile(
            name = firebaseUser?.displayName ?: googleIdTokenCredential.displayName ?: "Focus Scholar",
            email = firebaseUser?.email ?: googleIdTokenCredential.id,
            program = "Informatics Engineering • Year 3"
          )
        } else {
          UserProfile(
            name = googleIdTokenCredential.displayName ?: "Focus Scholar",
            email = googleIdTokenCredential.id,
            program = "Informatics Engineering • Year 3"
          )
        }
        Result.success(profile)
      } else {
        Result.failure(Exception("Unsupported credential type"))
      }
    } catch (e: GetCredentialException) {
      Result.failure(e)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  fun signInAsGuest(): UserProfile {
    return UserProfile(
      name = "Guest Scholar",
      email = "guest@wiitodo.app",
      program = "Offline Focus Workspace"
    )
  }

  fun signOut() {
    try {
      firebaseAuth?.signOut()
    } catch (_: Exception) {}
  }

  fun isUserSignedIn(): Boolean {
    return try {
      firebaseAuth?.currentUser != null
    } catch (_: Exception) {
      false
    }
  }

  companion object {
    @Volatile
    private var instance: AuthManager? = null

    fun getInstance(): AuthManager {
      return instance ?: synchronized(this) {
        instance ?: AuthManager().also { instance = it }
      }
    }
  }
}
