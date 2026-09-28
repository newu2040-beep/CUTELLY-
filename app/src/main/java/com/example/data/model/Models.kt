package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GestureType(val displayName: String, val iconDescription: String) {
    SINGLE_TAP("Single Tap", "Tap sticker once"),
    DOUBLE_TAP("Double Tap", "Tap sticker twice quickly"),
    TRIPLE_TAP("Triple Tap", "Tap sticker 3 times"),
    LONG_PRESS("Long Press", "Touch and hold for 0.5s"),
    SWIPE_UP("Swipe Up", "Flick sticker upward"),
    SWIPE_DOWN("Swipe Down", "Flick sticker downward"),
    SWIPE_LEFT("Swipe Left", "Flick sticker to the left"),
    SWIPE_RIGHT("Swipe Right", "Flick sticker to the right"),
    TRIPLE_SHAKE("Triple Shake", "Shake phone 3 times"),
    FOUR_SHAKE("4x Shake", "Shake phone 4 times rapidly"),
    FIVE_SHAKE("5x Frenzy Shake", "Shake phone 5 times in a burst"),
    TWO_FINGER_TAP("Two-Finger Tap", "Tap with two fingers simultaneously")
}

enum class ActionType(val displayName: String, val category: String) {
    // System
    TAKE_SCREENSHOT("Take Screenshot", "System"),
    OPEN_CAMERA("Open Camera", "Apps"),
    OPEN_GALLERY("Open Gallery", "Apps"),
    OPEN_BROWSER("Open Browser", "Apps"),
    OPEN_DIALER("Open Phone / Dialer", "Apps"),
    OPEN_MESSAGING("Open Messages", "Apps"),
    TOGGLE_FLASHLIGHT("Toggle Flashlight", "System"),
    OPEN_QUICK_SETTINGS("Open Quick Settings", "Navigation"),
    OPEN_NOTIFICATIONS("Open Notifications", "Navigation"),
    OPEN_RECENTS("Recent Apps Overview", "Navigation"),
    NAVIGATE_HOME("Navigate Home", "Navigation"),
    NAVIGATE_BACK("Back Action", "Navigation"),
    LOCK_SCREEN("Lock Phone Screen", "System"),
    POWER_DIALOG("Power Options Menu", "System"),
    OPEN_WIFI_SETTINGS("Wi-Fi Settings", "System"),
    OPEN_BLUETOOTH_SETTINGS("Bluetooth Settings", "System"),
    OPEN_SOUND_SETTINGS("Sound Settings", "System"),
    OPEN_DISPLAY_SETTINGS("Display Settings", "System"),
    OPEN_BATTERY_SETTINGS("Battery Settings", "System"),
    
    // Apps
    OPEN_APP("Launch Custom App", "Apps"),
    
    // Media & Volume
    MEDIA_PLAY_PAUSE("Media Play / Pause", "Media"),
    MEDIA_NEXT("Next Track", "Media"),
    MEDIA_PREVIOUS("Previous Track", "Media"),
    VOLUME_UP("Volume Up", "Media"),
    VOLUME_DOWN("Volume Down", "Media"),
    TOGGLE_MUTE("Toggle Mute / Unmute", "Media"),
    OPEN_MUSIC_APP("Open Music Player", "Media"),
    
    // Cutelly Special & Sleep
    SLEEP_STICKER("Put Sticker to Sleep (Zzz)", "Cutelly"),
    DISMISS_STICKER("Dismiss / Hide Sticker", "Cutelly"),
    CUTELLY_SHOW_HIDE("Toggle Sticker Visibility", "Cutelly"),
    CUTELLY_CHANGE_STICKER("Switch Active Sticker", "Cutelly"),
    CUTELLY_OPEN_GALLERY("Open Stickers Collection", "Cutelly"),
    CUTELLY_SWITCH_PROFILE("Switch Profile", "Cutelly"),
    CUTELLY_TOGGLE_ANIMATIONS("Toggle Idle Animation", "Cutelly"),
    CUTELLY_PLAY_SOUND("Play Cute Kawaii Sound", "Cutelly"),
    CUTELLY_OPEN_MENU("Open Floating Mini-Menu", "Cutelly"),
    SHOW_CUTE_MESSAGE("Show Kawaii Encouragement", "Cutelly")
}

enum class StickerAnimationType(val displayName: String) {
    IDLE_BREATHE("Idle Breathing"),
    GENTLE_FLOAT("Gentle Floating"),
    SOFT_BOUNCE("Soft Bounce"),
    WIGGLE("Wiggle"),
    BLINKING("Blinking"),
    HAPPY_JUMP("Happy Jump"),
    HEART_BURST("Heart Burst"),
    SPARKLE("Sparkle"),
    SQUISH("Squish"),
    ROTATION("Small Rotation"),
    SLEEP("Sleep Zzz"),
    WAVE("Wave")
}

@Entity(tableName = "stickers")
data class StickerItem(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val iconType: String = "built_in", // built_in, custom_image, custom_shape, custom_text
    val shapeType: String = "none", // none, heart, star, circle, cloud, bubble
    val customText: String = "",
    val customImagePath: String? = null,
    val primaryColorHex: String = "#FFE1ED",
    val secondaryColorHex: String = "#FF78AB",
    val defaultAnimation: String = "IDLE_BREATHE",
    val isFavorite: Boolean = false,
    val isUserCreated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "floating_stickers")
data class FloatingStickerConfig(
    @PrimaryKey val id: String = "active_main",
    val stickerId: String = "cat_white",
    val posXRatio: Float = 0.82f,
    val posYRatio: Float = 0.35f,
    val sizeDp: Int = 84,
    val opacity: Float = 1.0f,
    val rotationDeg: Float = 0.0f,
    val isLocked: Boolean = false,
    val snapToEdge: Boolean = true,
    val touchThrough: Boolean = false,
    val isVisible: Boolean = true,
    val profileId: String = "everyday",
    val customAnimationId: String? = null,
    
    // Sleep Mode & Active Time Period
    val sleepModeEnabled: Boolean = false,
    val activeDurationMinutes: Int = 0, // 0 = indefinite (always awake), or 15, 30, 60, 120, etc.
    val activeStartTime: Long = System.currentTimeMillis(),
    val isAsleep: Boolean = false,
    val sleepReason: String = "",
    val scheduleEnabled: Boolean = false,
    val scheduledSleepStartHour: Int = 22,
    val scheduledSleepStartMinute: Int = 0,
    val scheduledSleepEndHour: Int = 7,
    val scheduledSleepEndMinute: Int = 0
)

@Entity(tableName = "gesture_mappings")
data class GestureMapping(
    @PrimaryKey val id: String,
    val gestureName: String,
    val gestureType: GestureType,
    val actionType: ActionType,
    val actionTarget: String = "", // package name or extra parameter
    val actionLabel: String,
    val isEnabled: Boolean = true,
    val activationAnimation: String = "SOFT_BOUNCE",
    val soundEffect: String = "pop", // pop, bubble, chime, sparkle, soft_click, none
    val profileId: String = "everyday",
    val isCustom: Boolean = false
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val isCurrent: Boolean = false,
    val themeSetting: String = "STRAWBERRY_MILK",
    val themeMode: String = "SYSTEM" // SYSTEM, LIGHT, DARK
)

@Entity(tableName = "custom_animations")
data class CustomAnimationItem(
    @PrimaryKey val id: String,
    val name: String,
    val baseType: String = "BOUNCE", // BOUNCE, FLOAT, PULSE, WIGGLE, SPIN, HEARTBEAT, SWAY, SQUISH, JITTER
    val durationMs: Int = 1000,
    val minScale: Float = 0.95f,
    val maxScale: Float = 1.08f,
    val translateYDp: Float = 6f,
    val translateXDp: Float = 0f,
    val rotationAngleDeg: Float = 8f,
    val particleType: String = "none", // none, hearts, sparkles, stars, bubbles, notes, zzz
    val easingType: String = "SMOOTH", // SMOOTH, BOUNCY, LINEAR
    val isPreset: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
