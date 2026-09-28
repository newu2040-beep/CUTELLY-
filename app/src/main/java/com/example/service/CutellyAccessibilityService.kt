package com.example.service

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityEvent

class CutellyAccessibilityService : AccessibilityService() {

    companion object {
        var instance: CutellyAccessibilityService? = null
            private set

        val isServiceRunning: Boolean
            get() = instance != null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Privacy guaranteed: We do not process screen content or keystrokes.
    }

    override fun onInterrupt() {
        // Unused
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }

    fun triggerGlobalBack(): Boolean = performGlobalAction(GLOBAL_ACTION_BACK)
    fun triggerGlobalHome(): Boolean = performGlobalAction(GLOBAL_ACTION_HOME)
    fun triggerGlobalRecents(): Boolean = performGlobalAction(GLOBAL_ACTION_RECENTS)
    fun triggerGlobalNotifications(): Boolean = performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    fun triggerGlobalQuickSettings(): Boolean = performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
    fun triggerGlobalPowerDialog(): Boolean = performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)

    fun triggerGlobalLockScreen(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        } else {
            false
        }
    }

    fun triggerGlobalScreenshot(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
        } else {
            false
        }
    }
}
