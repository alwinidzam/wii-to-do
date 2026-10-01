package com.example.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random

/**
 * FocusAudioEngine provides real-time, zero-asset algorithmic soundscape synthesis
 * for deep focus sessions (Brown Noise Calm).
 */
object FocusAudioEngine {
  private var audioTrack: AudioTrack? = null
  private var audioJob: Job? = null
  private val scope = CoroutineScope(Dispatchers.Default)
  private var isPlaying = false

  fun startSoundscape() {
    if (isPlaying) return
    isPlaying = true

    audioJob = scope.launch {
      try {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
          sampleRate,
          AudioFormat.CHANNEL_OUT_MONO,
          AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(bufferSize)
          .setTransferMode(AudioTrack.MODE_STREAM)
          .build()

        audioTrack?.play()

        val random = Random()
        val buffer = ShortArray(bufferSize / 2)
        var lastOutput = 0.0

        while (isActive && isPlaying) {
          for (i in buffer.indices) {
            val white = (random.nextGaussian() * 0.1).toFloat()
            // Brown noise 6dB/octave low-pass integration
            lastOutput = (lastOutput + (0.02 * white)) / 1.02
            // Clamp and scale to 16-bit PCM range with soft volume
            val clamped = (lastOutput * 12000.0).coerceIn(-32000.0, 32000.0)
            buffer[i] = clamped.toInt().toShort()
          }
          audioTrack?.write(buffer, 0, buffer.size)
        }
      } catch (_: Exception) {
        // Handle audio device release gracefully
      }
    }
  }

  fun stopSoundscape() {
    isPlaying = false
    audioJob?.cancel()
    audioJob = null
    try {
      audioTrack?.pause()
      audioTrack?.flush()
      audioTrack?.stop()
      audioTrack?.release()
    } catch (_: Exception) {
    } finally {
      audioTrack = null
    }
  }
}
