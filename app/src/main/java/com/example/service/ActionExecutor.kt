package com.example.service

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.view.KeyEvent
import android.widget.Toast
import com.example.data.model.ActionType
import com.example.data.model.GestureMapping
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager

object ActionExecutor {
    private var isTorchOn = false

    fun execute(
        context: Context,
        mapping: GestureMapping,
        onInternalAction: ((ActionType, String) -> Unit)? = null
    ) {
        // Haptic feedback & Sound feedback
        KawaiiHaptics.performClick(context)
        if (mapping.soundEffect.isNotEmpty() && mapping.soundEffect != "none") {
            KawaiiSoundManager.playSound(mapping.soundEffect)
        }

        when (mapping.actionType) {
            ActionType.TAKE_SCREENSHOT -> {
                val service = CutellyAccessibilityService.instance
                if (service != null) {
                    val handled = service.triggerGlobalScreenshot()
                    if (!handled) {
                        Toast.makeText(context, "Screenshot requested", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    promptAccessibility(context, "Take Screenshot requires CUTELLY Accessibility Service")
                }
            }

            ActionType.OPEN_CAMERA -> {
                try {
                    val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    try {
                        val fallback = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(fallback)
                    } catch (e2: Exception) {
                        Toast.makeText(context, "Could not open camera", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            ActionType.OPEN_GALLERY -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        type = "image/*"
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open gallery", Toast.LENGTH_SHORT).show()
                }
            }

            ActionType.OPEN_BROWSER -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Browser not available", Toast.LENGTH_SHORT).show()
                }
            }

            ActionType.OPEN_DIALER -> {
                try {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Dialer not available", Toast.LENGTH_SHORT).show()
                }
            }

            ActionType.OPEN_MESSAGING -> {
                try {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_MESSAGING)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    try {
                        val fallback = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("sms:")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(fallback)
                    } catch (e2: Exception) {
                        Toast.makeText(context, "Messaging app not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            ActionType.OPEN_APP -> {
                val pkg = mapping.actionTarget
                if (pkg.isNotEmpty()) {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                    } else {
                        Toast.makeText(context, "App not found ($pkg)", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Open Cutelly app itself if no target selected
                    val selfIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                    selfIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(selfIntent)
                }
            }

            ActionType.TOGGLE_FLASHLIGHT -> {
                try {
                    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
                    val cameraId = cameraManager?.cameraIdList?.firstOrNull()
                    if (cameraManager != null && cameraId != null) {
                        isTorchOn = !isTorchOn
                        cameraManager.setTorchMode(cameraId, isTorchOn)
                        Toast.makeText(context, if (isTorchOn) "Flashlight On ✨" else "Flashlight Off", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Flashlight unavailable", Toast.LENGTH_SHORT).show()
                }
            }

            ActionType.OPEN_QUICK_SETTINGS -> {
                val service = CutellyAccessibilityService.instance
                if (service != null) {
                    service.triggerGlobalQuickSettings()
                } else {
                    openSettings(context, Settings.ACTION_SETTINGS)
                }
            }

            ActionType.OPEN_NOTIFICATIONS -> {
                val service = CutellyAccessibilityService.instance
                if (service != null) {
                    service.triggerGlobalNotifications()
                } else {
                    promptAccessibility(context, "Open Notifications requires Accessibility Service")
                }
            }

            ActionType.OPEN_RECENTS -> {
                val service = CutellyAccessibilityService.instance
                if (service != null) {
                    service.triggerGlobalRecents()
                } else {
                    promptAccessibility(context, "Open Recents requires Accessibility Service")
                }
            }

            ActionType.NAVIGATE_HOME -> {
                val service = CutellyAccessibilityService.instance
                if (service != null) {
                    service.triggerGlobalHome()
                } else {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            }

            ActionType.NAVIGATE_BACK -> {
                val service = CutellyAccessibilityService.instance
                if (service != null) {
                    service.triggerGlobalBack()
                } else {
                    promptAccessibility(context, "Navigate Back requires Accessibility Service")
                }
            }

            ActionType.LOCK_SCREEN -> {
                val service = CutellyAccessibilityService.instance
                if (service != null) {
                    service.triggerGlobalLockScreen()
                } else {
                    promptAccessibility(context, "Lock Screen requires Accessibility Service")
                }
            }

            ActionType.POWER_DIALOG -> {
                val service = CutellyAccessibilityService.instance
                if (service != null) {
                    service.triggerGlobalPowerDialog()
                } else {
                    promptAccessibility(context, "Power Dialog requires Accessibility Service")
                }
            }

            ActionType.OPEN_WIFI_SETTINGS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(panelIntent)
                        return
                    } catch (_: Exception) {}
                }
                openSettings(context, Settings.ACTION_WIFI_SETTINGS)
            }

            ActionType.OPEN_BLUETOOTH_SETTINGS -> {
                openSettings(context, Settings.ACTION_BLUETOOTH_SETTINGS)
            }

            ActionType.OPEN_SOUND_SETTINGS -> {
                openSettings(context, Settings.ACTION_SOUND_SETTINGS)
            }

            ActionType.OPEN_DISPLAY_SETTINGS -> {
                openSettings(context, Settings.ACTION_DISPLAY_SETTINGS)
            }

            ActionType.OPEN_BATTERY_SETTINGS -> {
                try {
                    val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    openSettings(context, Settings.ACTION_BATTERY_SAVER_SETTINGS)
                }
            }

            ActionType.MEDIA_PLAY_PAUSE -> {
                sendMediaKeyEvent(context, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
            }

            ActionType.MEDIA_NEXT -> {
                sendMediaKeyEvent(context, KeyEvent.KEYCODE_MEDIA_NEXT)
            }

            ActionType.MEDIA_PREVIOUS -> {
                sendMediaKeyEvent(context, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            }

            ActionType.VOLUME_UP -> {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                audioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
            }

            ActionType.VOLUME_DOWN -> {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                audioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
            }

            ActionType.OPEN_MUSIC_APP -> {
                try {
                    val intent = Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Music player not found", Toast.LENGTH_SHORT).show()
                }
            }

            ActionType.TOGGLE_MUTE -> {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                if (audioManager != null) {
                    val currentMode = audioManager.ringerMode
                    if (currentMode == AudioManager.RINGER_MODE_NORMAL) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                        Toast.makeText(context, "Muted / Vibrate mode \uD83D\uDD07", Toast.LENGTH_SHORT).show()
                    } else {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                        Toast.makeText(context, "Sound mode on \uD83D\uDD0A", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            ActionType.SHOW_CUTE_MESSAGE -> {
                val cuteQuotes = listOf(
                    "ฅ^•ﻌ•^ฅ You're doing amazing today!",
                    "✨ Keep shining bright like a star!",
                    "🌸 Sending you a warm gentle hug!",
                    "💖 Take a deep breath and relax!",
                    "🌟 Everything is going to be wonderful!",
                    "🍡 Don't forget to stay hydrated and smile!"
                )
                Toast.makeText(context, cuteQuotes.random(), Toast.LENGTH_SHORT).show()
            }

            ActionType.CUTELLY_PLAY_SOUND -> {
                val sound = if (mapping.soundEffect.isNotEmpty() && mapping.soundEffect != "none") mapping.soundEffect else "sparkle"
                KawaiiSoundManager.playSound(sound)
            }

            ActionType.SLEEP_STICKER,
            ActionType.DISMISS_STICKER,
            ActionType.CUTELLY_SHOW_HIDE,
            ActionType.CUTELLY_CHANGE_STICKER,
            ActionType.CUTELLY_OPEN_GALLERY,
            ActionType.CUTELLY_SWITCH_PROFILE,
            ActionType.CUTELLY_TOGGLE_ANIMATIONS,
            ActionType.CUTELLY_OPEN_MENU -> {
                onInternalAction?.invoke(mapping.actionType, mapping.actionTarget)
            }
        }
    }

    private fun openSettings(context: Context, action: String) {
        try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Settings not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun promptAccessibility(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    private fun sendMediaKeyEvent(context: Context, keyCode: Int) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (audioManager != null) {
            val down = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val up = KeyEvent(KeyEvent.ACTION_UP, keyCode)
            audioManager.dispatchMediaKeyEvent(down)
            audioManager.dispatchMediaKeyEvent(up)
        }
    }
}
