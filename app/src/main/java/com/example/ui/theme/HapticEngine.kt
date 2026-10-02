package com.example.ui.theme

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * High-End Apple HIG & Linear-Grade Haptic Engine for Android.
 * Emulates the Apple Taptic Engine feedback patterns:
 * - Notification Success: 2 crisp pulses (20ms pulse, 60ms gap, 20ms pulse)
 * - Notification Warning: 2 heavier detent pulses
 * - Notification Error: 4 rapid warning pulses
 * - Selection: 1 ultra-crisp micro-tick (12ms)
 * - Impact Light / Medium / Heavy: 1 crisp impulse
 */
object HapticEngine {

  private fun getVibrator(context: Context): Vibrator? {
    return try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }
    } catch (_: Exception) {
      null
    }
  }

  fun success(context: Context?, fallbackComposeHaptic: HapticFeedback? = null) {
    if (context == null) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }
    val vibrator = getVibrator(context) ?: run {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Apple HIG: Success = 2 pulses
        val timings = longArrayOf(0, 18, 50, 24)
        val amplitudes = intArrayOf(0, 180, 0, 255)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(longArrayOf(0, 20, 50, 25), -1)
      }
    } catch (_: Exception) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  fun warning(context: Context?, fallbackComposeHaptic: HapticFeedback? = null) {
    if (context == null) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }
    val vibrator = getVibrator(context) ?: run {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Apple HIG: Warning = 2 heavy pulses
        val timings = longArrayOf(0, 30, 60, 35)
        val amplitudes = intArrayOf(0, 220, 0, 255)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(longArrayOf(0, 30, 60, 35), -1)
      }
    } catch (_: Exception) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  fun error(context: Context?, fallbackComposeHaptic: HapticFeedback? = null) {
    if (context == null) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }
    val vibrator = getVibrator(context) ?: run {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Apple HIG: Error = 4 rapid pulses
        val timings = longArrayOf(0, 15, 35, 15, 35, 15, 35, 25)
        val amplitudes = intArrayOf(0, 200, 0, 200, 0, 200, 0, 255)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(longArrayOf(0, 15, 35, 15, 35, 15, 35, 25), -1)
      }
    } catch (_: Exception) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  fun selection(context: Context?, fallbackComposeHaptic: HapticFeedback? = null) {
    if (context == null) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
      return
    }
    val vibrator = getVibrator(context) ?: run {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
      return
    }

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Apple HIG: Selection = 1 crisp micro-tick
        vibrator.vibrate(VibrationEffect.createOneShot(12, 140))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(12)
      }
    } catch (_: Exception) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
  }

  fun impactLight(context: Context?, fallbackComposeHaptic: HapticFeedback? = null) {
    if (context == null) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }
    val vibrator = getVibrator(context) ?: run {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(15, 120))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(15)
      }
    } catch (_: Exception) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  fun impactMedium(context: Context?, fallbackComposeHaptic: HapticFeedback? = null) {
    if (context == null) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }
    val vibrator = getVibrator(context) ?: run {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
      return
    }

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(22, 190))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(22)
      }
    } catch (_: Exception) {
      fallbackComposeHaptic?.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }
}
