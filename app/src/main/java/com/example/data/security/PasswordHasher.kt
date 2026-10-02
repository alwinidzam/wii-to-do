package com.example.data.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
  private const val ITERATIONS = 10000
  private const val KEY_LENGTH = 256
  private const val SALT_LENGTH = 16

  fun generateSalt(): String {
    val random = SecureRandom()
    val salt = ByteArray(SALT_LENGTH)
    random.nextBytes(salt)
    return Base64.encodeToString(salt, Base64.NO_WRAP)
  }

  fun hashPassword(password: String, saltBase64: String): String {
    val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
    val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val hash = factory.generateSecret(spec).encoded
    return Base64.encodeToString(hash, Base64.NO_WRAP)
  }

  fun verifyPassword(password: String, saltBase64: String, expectedHashBase64: String): Boolean {
    return try {
      val computedHash = hashPassword(password, saltBase64)
      val computedBytes = Base64.decode(computedHash, Base64.NO_WRAP)
      val expectedBytes = Base64.decode(expectedHashBase64, Base64.NO_WRAP)
      MessageDigest.isEqual(computedBytes, expectedBytes)
    } catch (_: Exception) {
      false
    }
  }
}
