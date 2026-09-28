package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object KawaiiSoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050

    var isMuted: Boolean = false

    fun playSound(type: String) {
        if (isMuted) return
        scope.launch {
            try {
                val samples = when (type.lowercase()) {
                    "pop" -> generatePop()
                    "bubble" -> generateBubble()
                    "chime" -> generateChime()
                    "sparkle" -> generateSparkle()
                    "soft_click" -> generateClick()
                    else -> generatePop()
                }
                playPcm(samples)
            } catch (e: Exception) {
                // Ignore audio errors gracefully
            }
        }
    }

    private fun playPcm(samples: ShortArray) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()
        audioTrack.setNotificationMarkerPosition(samples.size)
        audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack?) {
                track?.release()
            }
            override fun onPeriodicNotification(track: AudioTrack?) {}
        })
    }

    // Upward cheerful sweep: 550Hz -> 1200Hz
    private fun generatePop(): ShortArray {
        val durationSec = 0.08
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 550.0 + progress * 650.0
            phase += 2.0 * PI * freq / SAMPLE_RATE
            val envelope = 1.0 - progress
            val sample = sin(phase) * envelope * 0.7
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // Cute bubble blip: two alternating chirps
    private fun generateBubble(): ShortArray {
        val durationSec = 0.1
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = if (progress < 0.5) 750.0 + progress * 400.0 else 1150.0 - (progress - 0.5) * 300.0
            phase += 2.0 * PI * freq / SAMPLE_RATE
            val envelope = sin(progress * PI)
            val sample = sin(phase) * envelope * 0.65
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // Gentle bell chime: gentle decay of harmonic tones
    private fun generateChime(): ShortArray {
        val durationSec = 0.22
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = exp(-t * 9.0)
            val tone1 = sin(2.0 * PI * 659.25 * t) // E5
            val tone2 = sin(2.0 * PI * 783.99 * t) // G5
            val tone3 = sin(2.0 * PI * 987.77 * t) // B5
            val mixed = (tone1 * 0.5 + tone2 * 0.3 + tone3 * 0.2) * envelope * 0.7
            buffer[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // Sparkle: 4 swift ascending notes
    private fun generateSparkle(): ShortArray {
        val notes = doubleArrayOf(1046.5, 1318.5, 1567.98, 2093.0)
        val noteDuration = 0.035
        val samplesPerNote = (SAMPLE_RATE * noteDuration).toInt()
        val totalSamples = samplesPerNote * notes.size
        val buffer = ShortArray(totalSamples)

        for (n in notes.indices) {
            val freq = notes[n]
            var phase = 0.0
            for (i in 0 until samplesPerNote) {
                val progress = i.toDouble() / samplesPerNote
                phase += 2.0 * PI * freq / SAMPLE_RATE
                val envelope = 1.0 - progress
                val sample = sin(phase) * envelope * 0.5
                val index = n * samplesPerNote + i
                buffer[index] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    private fun generateClick(): ShortArray {
        val durationSec = 0.02
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val sample = sin(2.0 * PI * 1800.0 * (i.toDouble() / SAMPLE_RATE)) * (1.0 - progress) * 0.4
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }
}
