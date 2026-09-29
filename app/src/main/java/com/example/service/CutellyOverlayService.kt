package com.example.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.data.db.CutellyDatabase
import com.example.data.model.ActionType
import com.example.data.model.CustomAnimationItem
import com.example.data.model.FloatingStickerConfig
import com.example.data.model.GestureMapping
import com.example.data.model.GestureType
import com.example.data.model.StickerItem
import com.example.data.repository.CutellyRepository
import com.example.ui.stickers.KawaiiStickerView
import com.example.ui.theme.CutellyBlush
import com.example.ui.theme.CutellyInk
import com.example.ui.theme.CutellyMint
import com.example.ui.theme.CutellySoftPink
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager
import com.example.util.ShakeDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.sqrt

class CutellyOverlayService : Service() {

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_TOGGLE_VISIBILITY = "ACTION_TOGGLE_VISIBILITY"
        const val ACTION_WAKE_UP = "ACTION_WAKE_UP"
        const val ACTION_SLEEP = "ACTION_SLEEP"
        const val CHANNEL_ID = "cutelly_overlay_channel"
        const val NOTIFICATION_ID = 2026

        var isRunning: Boolean = false
            private set

        fun startOverlay(context: Context) {
            val intent = Intent(context, CutellyOverlayService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopOverlay(context: Context) {
            val intent = Intent(context, CutellyOverlayService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun toggleVisibility(context: Context) {
            val intent = Intent(context, CutellyOverlayService::class.java).apply {
                action = ACTION_TOGGLE_VISIBILITY
            }
            context.startService(intent)
        }

        fun wakeUp(context: Context) {
            val intent = Intent(context, CutellyOverlayService::class.java).apply {
                action = ACTION_WAKE_UP
            }
            context.startService(intent)
        }

        fun putToSleep(context: Context) {
            val intent = Intent(context, CutellyOverlayService::class.java).apply {
                action = ACTION_SLEEP
            }
            context.startService(intent)
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var repository: CutellyRepository
    private var windowManager: WindowManager? = null

    private var overlayView: ComposeView? = null
    private var dismissView: ComposeView? = null
    private var windowLayoutParams: WindowManager.LayoutParams? = null
    private var dismissLayoutParams: WindowManager.LayoutParams? = null

    private var shakeDetector: ShakeDetector? = null

    private val currentConfigState = mutableStateOf<FloatingStickerConfig?>(null)
    private var currentConfig: FloatingStickerConfig?
        get() = currentConfigState.value
        set(value) { currentConfigState.value = value }

    private val currentStickerState = mutableStateOf<StickerItem?>(null)
    private var currentSticker: StickerItem?
        get() = currentStickerState.value
        set(value) { currentStickerState.value = value }

    private val gestureMappingsState = mutableStateOf<List<GestureMapping>>(emptyList())
    private var gestureMappings: List<GestureMapping>
        get() = gestureMappingsState.value
        set(value) { gestureMappingsState.value = value }

    private val customAnimationsState = mutableStateOf<List<CustomAnimationItem>>(emptyList())
    private var customAnimations: List<CustomAnimationItem>
        get() = customAnimationsState.value
        set(value) { customAnimationsState.value = value }

    private var isTemporaryHidden = false
    private var showFloatingMenu = mutableStateOf(false)
    private var isHoveringDismiss = mutableStateOf(false)
    private var isDraggingState = mutableStateOf(false)

    private var autoSleepJob: Job? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastTapTime = 0L

    override fun onCreate() {
        super.onCreate()
        val database = CutellyDatabase.getDatabase(this, serviceScope)
        repository = CutellyRepository(database.dao())
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        createNotificationChannel()
        initShakeDetector()
        observeDatabase()
        startSleepChecker()
        isRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopForeground(true)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_TOGGLE_VISIBILITY -> {
                isTemporaryHidden = !isTemporaryHidden
                updateOverlayVisibility()
            }
            ACTION_WAKE_UP -> {
                serviceScope.launch {
                    repository.wakeUpSticker()
                }
            }
            ACTION_SLEEP -> {
                serviceScope.launch {
                    repository.setStickerSleepState(isAsleep = true, reason = "Put to sleep by user")
                }
            }
            else -> {
                startForeground(NOTIFICATION_ID, buildNotification())
                ensureOverlayViewAttached()
            }
        }
        return START_STICKY
    }

    private fun initShakeDetector() {
        shakeDetector = ShakeDetector(this) { shakeCount ->
            val gestureType = when (shakeCount) {
                3 -> GestureType.TRIPLE_SHAKE
                4 -> GestureType.FOUR_SHAKE
                else -> GestureType.FIVE_SHAKE
            }
            val mapping = gestureMappings.find { it.gestureType == gestureType && it.isEnabled }
            if (mapping != null) {
                ActionExecutor.execute(this, mapping) { internalAction, _ ->
                    handleInternalAction(internalAction)
                }
            }
        }
        shakeDetector?.start()
    }

    private fun observeDatabase() {
        serviceScope.launch {
            repository.activeFloatingConfig.collect { config ->
                currentConfig = config
                if (config != null) {
                    val sticker = repository.getSticker(config.stickerId)
                    currentSticker = sticker
                    updateOverlayView(config, sticker)
                    updateNotification(config, sticker)
                }
            }
        }

        serviceScope.launch {
            repository.gestureMappings.collect { mappings ->
                gestureMappings = mappings
            }
        }

        serviceScope.launch {
            repository.allCustomAnimations.collect { anims ->
                customAnimations = anims
                val cfg = currentConfig
                val sticker = currentSticker
                if (cfg != null) {
                    updateOverlayView(cfg, sticker)
                }
            }
        }
    }

    // Periodic check for Active Time Period duration and Sleep Schedule
    private fun startSleepChecker() {
        autoSleepJob?.cancel()
        autoSleepJob = serviceScope.launch {
            while (isActive) {
                delay(10000L) // check every 10s
                val config = currentConfig ?: continue
                if (!config.isAsleep) {
                    // 1. Check Duration Timer
                    if (config.sleepModeEnabled && config.activeDurationMinutes > 0) {
                        val elapsed = System.currentTimeMillis() - config.activeStartTime
                        val limit = config.activeDurationMinutes * 60 * 1000L
                        if (elapsed >= limit) {
                            // Active time period completed -> Sleep Mode!
                            repository.setStickerSleepState(
                                isAsleep = true,
                                reason = "Active period (${config.activeDurationMinutes}m) finished"
                            )
                            Toast.makeText(
                                this@CutellyOverlayService,
                                "Sleep time! Your sticker is resting (Zzz) 🌙",
                                Toast.LENGTH_LONG
                            ).show()
                            KawaiiSoundManager.playSound("soft_click")
                        }
                    }

                    // 2. Check Daily Schedule
                    if (config.scheduleEnabled) {
                        val cal = Calendar.getInstance()
                        val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
                        val startMinutes = config.scheduledSleepStartHour * 60 + config.scheduledSleepStartMinute
                        val endMinutes = config.scheduledSleepEndHour * 60 + config.scheduledSleepEndMinute

                        val inSleepWindow = if (startMinutes < endMinutes) {
                            currentMinutes in startMinutes until endMinutes
                        } else {
                            currentMinutes >= startMinutes || currentMinutes < endMinutes
                        }

                        if (inSleepWindow) {
                            repository.setStickerSleepState(
                                isAsleep = true,
                                reason = "Scheduled quiet hours"
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ensureOverlayViewAttached() {
        if (!Settings.canDrawOverlays(this)) return
        if (overlayView != null) return

        val wm = windowManager ?: return
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val config = currentConfig ?: FloatingStickerConfig()
        val sizePx = (config.sizeDp * displayMetrics.density).toInt()

        val layoutParams = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    if (config.touchThrough) WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE else 0,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (screenWidth * config.posXRatio).toInt().coerceIn(0, screenWidth - sizePx)
            y = (screenHeight * config.posYRatio).toInt().coerceIn(0, screenHeight - sizePx)
        }
        windowLayoutParams = layoutParams

        val composeView = ComposeView(this).apply {
            val lifecycleOwner = OverlayLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(OverlayViewModelStoreOwner())

            setContent {
                OverlayContent()
            }
        }

        // Direct Touch Handling for Silky Smooth 60/120 FPS Dragging
        var initialX = 0
        var initialY = 0
        var touchStartX = 0f
        var touchStartY = 0f
        var isDragging = false
        var downTime = 0L
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop

        composeView.setOnTouchListener { _, event ->
            val params = windowLayoutParams ?: return@setOnTouchListener false
            val cfg = currentConfig ?: FloatingStickerConfig()

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    touchStartX = event.rawX
                    touchStartY = event.rawY
                    downTime = System.currentTimeMillis()
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - touchStartX
                    val dy = event.rawY - touchStartY
                    val dist = sqrt(dx * dx + dy * dy)

                    if (!isDragging && dist > touchSlop) {
                        if (!cfg.isLocked) {
                            isDragging = true
                            isDraggingState.value = true
                            showDismissTarget()
                            KawaiiHaptics.performClick(this)
                        }
                    }

                    if (isDragging) {
                        params.x = (initialX + dx).toInt()
                        params.y = (initialY + dy).toInt()
                        try {
                            wm.updateViewLayout(composeView, params)
                        } catch (_: Exception) {}

                        // Check if hovering over bottom dismiss zone
                        val dm = resources.displayMetrics
                        val bottomThreshold = dm.heightPixels - (180 * dm.density).toInt()
                        val centerX = dm.widthPixels / 2
                        val hoverRange = (110 * dm.density).toInt()
                        val isHover = event.rawY > bottomThreshold &&
                                abs(event.rawX - centerX) < hoverRange

                        if (isHover != isHoveringDismiss.value) {
                            isHoveringDismiss.value = isHover
                            if (isHover) {
                                KawaiiHaptics.performDouble(this)
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    hideDismissTarget()
                    isDraggingState.value = false

                    if (isDragging) {
                        isDragging = false
                        val dm = resources.displayMetrics
                        val bottomThreshold = dm.heightPixels - (180 * dm.density).toInt()
                        val centerX = dm.widthPixels / 2
                        val hoverRange = (110 * dm.density).toInt()

                        val droppedInDismiss = event.rawY > bottomThreshold && abs(event.rawX - centerX) < hoverRange
                        val draggedOffScreen = params.x < -params.width / 2 || params.x > dm.widthPixels - params.width / 2 || params.y > dm.heightPixels - 50

                        if (droppedInDismiss || draggedOffScreen) {
                            // Dragged away / dropped on dismiss -> Put sticker to sleep!
                            KawaiiSoundManager.playSound("bubble")
                            KawaiiHaptics.performSuccessBurst(this)
                            Toast.makeText(this, "Sticker went to sleep (Zzz) 🌙", Toast.LENGTH_SHORT).show()
                            serviceScope.launch {
                                repository.setStickerSleepState(isAsleep = true, reason = "Dragged to remove")
                            }
                        } else {
                            // Normal release -> Snap to edge if enabled
                            if (cfg.snapToEdge) {
                                val mid = dm.widthPixels / 2
                                val targetX = if (params.x + params.width / 2 < mid) 20 else dm.widthPixels - params.width - 20
                                params.x = targetX
                                try {
                                    wm.updateViewLayout(composeView, params)
                                } catch (_: Exception) {}
                            }

                            // Persist position
                            val posXRatio = (params.x.toFloat() / dm.widthPixels).coerceIn(0f, 1f)
                            val posYRatio = (params.y.toFloat() / dm.heightPixels).coerceIn(0f, 1f)
                            serviceScope.launch {
                                repository.updateFloatingPosition(posXRatio, posYRatio)
                            }
                        }
                    } else {
                        // Tap / Double Tap / Long Press Detection
                        val elapsed = System.currentTimeMillis() - downTime
                        if (elapsed > 450) {
                            // Long Press
                            KawaiiHaptics.performSuccessBurst(this)
                            val mapping = gestureMappings.find { it.gestureType == GestureType.LONG_PRESS && it.isEnabled }
                            if (mapping != null && mapping.actionType != ActionType.CUTELLY_OPEN_MENU) {
                                ActionExecutor.execute(this, mapping) { action, _ ->
                                    handleInternalAction(action)
                                }
                            } else {
                                showFloatingMenu.value = !showFloatingMenu.value
                                updateOverlayView(cfg, currentSticker)
                            }
                        } else {
                            val now = System.currentTimeMillis()
                            if (now - lastTapTime < 320) {
                                // Double Tap!
                                lastTapTime = 0L
                                KawaiiHaptics.performDouble(this)
                                val mapping = gestureMappings.find { it.gestureType == GestureType.DOUBLE_TAP && it.isEnabled }
                                if (mapping != null) {
                                    ActionExecutor.execute(this, mapping) { action, _ ->
                                        handleInternalAction(action)
                                    }
                                } else {
                                    showFloatingMenu.value = !showFloatingMenu.value
                                    updateOverlayView(cfg, currentSticker)
                                }
                            } else {
                                // Potential single tap
                                lastTapTime = now
                                mainHandler.postDelayed({
                                    if (lastTapTime == now) {
                                        KawaiiHaptics.performClick(this)
                                        val mapping = gestureMappings.find { it.gestureType == GestureType.SINGLE_TAP && it.isEnabled }
                                        if (mapping != null) {
                                            ActionExecutor.execute(this, mapping) { action, _ ->
                                                handleInternalAction(action)
                                            }
                                        }
                                    }
                                }, 330)
                            }
                        }
                    }
                    true
                }

                else -> false
            }
        }

        try {
            wm.addView(composeView, layoutParams)
            overlayView = composeView
        } catch (_: Exception) {}

        ensureDismissTargetAttached()
    }

    private fun ensureDismissTargetAttached() {
        if (dismissView != null) return
        val wm = windowManager ?: return
        val dm = resources.displayMetrics

        val sizePx = (96 * dm.density).toInt()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            sizePx,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = (24 * dm.density).toInt()
        }
        dismissLayoutParams = params

        val composeView = ComposeView(this).apply {
            val lifecycleOwner = OverlayLifecycleOwner()
            lifecycleOwner.performRestore(null)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(OverlayViewModelStoreOwner())

            setContent {
                DismissZoneContent()
            }
            visibility = View.GONE
        }

        try {
            wm.addView(composeView, params)
            dismissView = composeView
        } catch (_: Exception) {}
    }

    private fun showDismissTarget() {
        dismissView?.visibility = View.VISIBLE
    }

    private fun hideDismissTarget() {
        dismissView?.visibility = View.GONE
        isHoveringDismiss.value = false
    }

    @Composable
    private fun DismissZoneContent() {
        val isHovering by isHoveringDismiss
        val scale by animateFloatAsState(targetValue = if (isHovering) 1.2f else 1.0f, label = "dismiss_scale")

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.scale(scale),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isHovering) CutellyBlush else CutellyInk.copy(alpha = 0.88f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isHovering) Icons.Default.Bedtime else Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isHovering) "Release to Sleep Zzz 🌙" else "Drop here to Sleep / Close ✨",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    private fun updateOverlayView(config: FloatingStickerConfig, sticker: StickerItem?) {
        val wm = windowManager ?: return
        val view = overlayView ?: run {
            ensureOverlayViewAttached()
            return
        }
        val params = windowLayoutParams ?: return

        val displayMetrics = resources.displayMetrics
        val extraWidth = if (showFloatingMenu.value) 130 else 0
        val extraHeight = if (showFloatingMenu.value) 70 else 0

        val wPx = ((config.sizeDp + extraWidth) * displayMetrics.density).toInt()
        val hPx = ((config.sizeDp + extraHeight) * displayMetrics.density).toInt()

        params.width = wPx.coerceAtLeast(100)
        params.height = hPx.coerceAtLeast(100)

        updateOverlayVisibility()
        try {
            wm.updateViewLayout(view, params)
        } catch (_: Exception) {}
    }

    private fun updateOverlayVisibility() {
        val view = overlayView ?: return
        val config = currentConfig ?: return
        // Hide if: manually hidden, or not visible in config, or in Sleep Mode!
        val shouldShow = config.isVisible && !config.isAsleep && !isTemporaryHidden
        view.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    @Composable
    private fun OverlayContent() {
        val config = currentConfig ?: FloatingStickerConfig()
        val sticker = currentSticker ?: StickerItem("cat_white", "Little Cat", "Cats")
        var isMenuOpen by remember { showFloatingMenu }

        val customAnim = config.customAnimationId?.let { id ->
            customAnimations.find { it.id == id }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Main Sticker view
            KawaiiStickerView(
                sticker = sticker,
                size = config.sizeDp.dp,
                isAnimated = true,
                opacity = config.opacity,
                customAnimation = customAnim
            )

            // Floating mini menu when opened
            AnimatedVisibility(
                visible = isMenuOpen,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CutellyInk.copy(alpha = 0.94f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MiniActionIcon(Icons.Default.Edit, "Edit") {
                            openMainActivity()
                            isMenuOpen = false
                        }
                        MiniActionIcon(Icons.Default.Palette, "Stickers") {
                            openMainActivity("stickers")
                            isMenuOpen = false
                        }
                        MiniActionIcon(Icons.Default.Gesture, "Gestures") {
                            openMainActivity("gestures")
                            isMenuOpen = false
                        }
                        MiniActionIcon(
                            if (config.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            if (config.isLocked) "Unlock" else "Lock"
                        ) {
                            serviceScope.launch {
                                repository.updateFloatingConfig(config.copy(isLocked = !config.isLocked))
                            }
                        }
                        // Sleep / Remove action directly from mini-menu
                        MiniActionIcon(Icons.Default.Bedtime, "Sleep (Zzz)") {
                            isMenuOpen = false
                            serviceScope.launch {
                                repository.setStickerSleepState(isAsleep = true, reason = "Manual sleep action")
                            }
                            KawaiiSoundManager.playSound("soft_click")
                            Toast.makeText(this@CutellyOverlayService, "Sticker went to sleep (Zzz) 🌙", Toast.LENGTH_SHORT).show()
                        }
                        MiniActionIcon(Icons.Default.Close, "Close") {
                            isMenuOpen = false
                            updateOverlayView(config, sticker)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MiniActionIcon(icon: ImageVector, description: String, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .clickable {
                    KawaiiHaptics.performClick(this@CutellyOverlayService)
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = Color.White,
                modifier = Modifier.size(17.dp)
            )
        }
    }

    private fun handleInternalAction(actionType: ActionType) {
        val config = currentConfig ?: return
        when (actionType) {
            ActionType.SLEEP_STICKER -> {
                serviceScope.launch {
                    repository.setStickerSleepState(isAsleep = true, reason = "Gesture trigger")
                }
                Toast.makeText(this, "Sticker went to sleep (Zzz) 🌙", Toast.LENGTH_SHORT).show()
            }
            ActionType.DISMISS_STICKER,
            ActionType.CUTELLY_SHOW_HIDE -> {
                isTemporaryHidden = !isTemporaryHidden
                updateOverlayVisibility()
            }
            ActionType.CUTELLY_CHANGE_STICKER -> {
                serviceScope.launch {
                    val all = repository.allStickers.first()
                    if (all.isNotEmpty()) {
                        val currentIdx = all.indexOfFirst { it.id == config.stickerId }
                        val next = all[(currentIdx + 1) % all.size]
                        repository.setActiveSticker(next.id)
                    }
                }
            }
            ActionType.CUTELLY_OPEN_GALLERY -> openMainActivity("stickers")
            ActionType.CUTELLY_OPEN_MENU -> {
                showFloatingMenu.value = !showFloatingMenu.value
                updateOverlayView(config, currentSticker)
            }
            ActionType.CUTELLY_TOGGLE_ANIMATIONS -> {
                // Toggled in config
            }
            ActionType.CUTELLY_PLAY_SOUND -> {
                KawaiiSoundManager.playSound("sparkle")
            }
            ActionType.CUTELLY_SWITCH_PROFILE -> {
                openMainActivity("profile")
            }
            else -> {}
        }
    }

    private fun openMainActivity(targetScreen: String? = null) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            if (targetScreen != null) {
                putExtra("navigate_to", targetScreen)
            }
        }
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "CUTELLY Floating Companion",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls and status for floating stickers and gesture overlay"
                setShowBadge(false)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(config: FloatingStickerConfig, sticker: StickerItem?) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(config, sticker))
    }

    private fun buildNotification(
        config: FloatingStickerConfig? = currentConfig,
        sticker: StickerItem? = currentSticker
    ): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isAsleep = config?.isAsleep == true
        val title = if (isAsleep) "CUTELLY is resting (Zzz) 🌙" else "CUTELLY is active ✨"
        val subtitle = if (isAsleep) {
            "Tap 'Wake Up' to bring ${sticker?.name ?: "your sticker"} back!"
        } else {
            "${sticker?.name ?: "Little Cat"} is floating on your screen"
        }

        val actionIntent = Intent(this, CutellyOverlayService::class.java).apply {
            action = if (isAsleep) ACTION_WAKE_UP else ACTION_SLEEP
        }
        val actionPendingIntent = PendingIntent.getService(
            this, 1, actionIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(openPendingIntent)
            .setOngoing(true)
            .addAction(
                if (isAsleep) android.R.drawable.ic_media_play else android.R.drawable.ic_lock_power_off,
                if (isAsleep) "✨ Wake Up" else "🌙 Sleep",
                actionPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        autoSleepJob?.cancel()
        shakeDetector?.stop()
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {}
        }
        dismissView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {}
        }
        overlayView = null
        dismissView = null
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)
        private val savedStateRegistryController = SavedStateRegistryController.create(this)

        override val lifecycle: Lifecycle get() = lifecycleRegistry
        override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

        fun performRestore(savedState: android.os.Bundle?) {
            savedStateRegistryController.performRestore(savedState)
        }

        fun handleLifecycleEvent(event: Lifecycle.Event) {
            lifecycleRegistry.handleLifecycleEvent(event)
        }
    }

    private class OverlayViewModelStoreOwner : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}
