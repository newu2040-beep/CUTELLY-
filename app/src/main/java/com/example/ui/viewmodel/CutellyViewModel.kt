package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.CutellyDatabase
import com.example.data.model.ActionType
import com.example.data.model.CustomAnimationItem
import com.example.data.model.FloatingStickerConfig
import com.example.data.model.GestureMapping
import com.example.data.model.GestureType
import com.example.data.model.StickerItem
import com.example.data.model.UserProfile
import com.example.data.repository.CutellyRepository
import com.example.service.CutellyAccessibilityService
import com.example.service.CutellyOverlayService
import com.example.ui.theme.AppThemeSetting
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.UiDensityMode
import com.example.util.KawaiiHaptics
import com.example.util.KawaiiSoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CutellyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CutellyRepository
    val allStickers: StateFlow<List<StickerItem>>
    val gestureMappings: StateFlow<List<GestureMapping>>
    val userProfiles: StateFlow<List<UserProfile>>
    val currentProfile: StateFlow<UserProfile?>
    val activeFloatingConfig: StateFlow<FloatingStickerConfig?>
    val customAnimations: StateFlow<List<CustomAnimationItem>>

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isOverlayActive = MutableStateFlow(false)
    val isOverlayActive = _isOverlayActive.asStateFlow()

    private val _currentTheme = MutableStateFlow(AppThemeSetting.STRAWBERRY_MILK)
    val currentTheme = _currentTheme.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    private val _uiDensityMode = MutableStateFlow(UiDensityMode.AUTO)
    val uiDensityMode = _uiDensityMode.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled = _isSoundEnabled.asStateFlow()

    private val _isHapticsEnabled = MutableStateFlow(true)
    val isHapticsEnabled = _isHapticsEnabled.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(false)
    val isOnboardingCompleted = _isOnboardingCompleted.asStateFlow()

    private val prefs = application.getSharedPreferences("cutelly_prefs", Context.MODE_PRIVATE)

    init {
        val database = CutellyDatabase.getDatabase(application, viewModelScope)
        repository = CutellyRepository(database.dao())

        viewModelScope.launch {
            repository.ensureInitialized()
        }

        allStickers = repository.allStickers
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        gestureMappings = repository.gestureMappings
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        userProfiles = repository.userProfiles
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        currentProfile = repository.currentProfile
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        activeFloatingConfig = repository.activeFloatingConfig
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        customAnimations = repository.allCustomAnimations
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Read saved prefs
        _isOnboardingCompleted.value = prefs.getBoolean("onboarding_done", false)
        val savedTheme = prefs.getString("theme", AppThemeSetting.STRAWBERRY_MILK.name)
        _currentTheme.value = try {
            AppThemeSetting.valueOf(savedTheme ?: AppThemeSetting.STRAWBERRY_MILK.name)
        } catch (_: Exception) {
            AppThemeSetting.STRAWBERRY_MILK
        }

        val savedMode = prefs.getString("theme_mode", ThemeMode.SYSTEM.name)
        _themeMode.value = try {
            ThemeMode.valueOf(savedMode ?: ThemeMode.SYSTEM.name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }

        val savedDensity = prefs.getString("ui_density_mode", UiDensityMode.AUTO.name)
        _uiDensityMode.value = try {
            UiDensityMode.valueOf(savedDensity ?: UiDensityMode.AUTO.name)
        } catch (_: Exception) {
            UiDensityMode.AUTO
        }

        _isSoundEnabled.value = prefs.getBoolean("sound_enabled", true)
        _isHapticsEnabled.value = prefs.getBoolean("haptics_enabled", true)
        KawaiiSoundManager.isMuted = !_isSoundEnabled.value
        KawaiiHaptics.isEnabled = _isHapticsEnabled.value
        _isOverlayActive.value = CutellyOverlayService.isRunning
    }

    fun completeOnboarding() {
        _isOnboardingCompleted.value = true
        prefs.edit().putBoolean("onboarding_done", true).apply()
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTheme(theme: AppThemeSetting) {
        _currentTheme.value = theme
        prefs.edit().putString("theme", theme.name).apply()
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
        KawaiiSoundManager.playSound("pop")
        KawaiiHaptics.performClick(getApplication())
    }

    fun setUiDensityMode(mode: UiDensityMode) {
        _uiDensityMode.value = mode
        prefs.edit().putString("ui_density_mode", mode.name).apply()
        KawaiiSoundManager.playSound("pop")
        KawaiiHaptics.performClick(getApplication())
    }

    fun toggleSound() {
        val newState = !_isSoundEnabled.value
        _isSoundEnabled.value = newState
        KawaiiSoundManager.isMuted = !newState
        prefs.edit().putBoolean("sound_enabled", newState).apply()
    }

    fun toggleHaptics() {
        val newState = !_isHapticsEnabled.value
        _isHapticsEnabled.value = newState
        KawaiiHaptics.isEnabled = newState
        prefs.edit().putBoolean("haptics_enabled", newState).apply()
    }

    fun checkOverlayServiceState() {
        _isOverlayActive.value = CutellyOverlayService.isRunning
    }

    fun canDrawOverlays(): Boolean {
        return Settings.canDrawOverlays(getApplication())
    }

    fun isAccessibilityEnabled(): Boolean {
        return CutellyAccessibilityService.isServiceRunning
    }

    fun toggleOverlayService(onPermissionRequired: () -> Unit) {
        val context = getApplication<Application>()
        if (!canDrawOverlays()) {
            onPermissionRequired()
            return
        }

        if (CutellyOverlayService.isRunning) {
            CutellyOverlayService.stopOverlay(context)
            _isOverlayActive.value = false
        } else {
            CutellyOverlayService.startOverlay(context)
            _isOverlayActive.value = true
        }
    }

    fun selectActiveSticker(sticker: StickerItem) {
        viewModelScope.launch {
            repository.setActiveSticker(sticker.id)
            repository.wakeUpSticker()
            KawaiiSoundManager.playSound("pop")
            KawaiiHaptics.performClick(getApplication())
        }
    }

    fun toggleFavorite(sticker: StickerItem) {
        viewModelScope.launch {
            repository.toggleFavorite(sticker)
            KawaiiSoundManager.playSound("sparkle")
        }
    }

    fun updateFloatingConfig(config: FloatingStickerConfig) {
        viewModelScope.launch {
            repository.updateFloatingConfig(config)
        }
    }

    // Sleep mode & Active Time Period controls
    fun setActiveDurationMinutes(minutes: Int) {
        viewModelScope.launch {
            repository.updateActiveDuration(minutes)
            KawaiiSoundManager.playSound("bubble")
            KawaiiHaptics.performClick(getApplication())
        }
    }

    fun setSleepSchedule(enabled: Boolean, startH: Int, startM: Int, endH: Int, endM: Int) {
        viewModelScope.launch {
            repository.updateSleepSchedule(enabled, startH, startM, endH, endM)
            KawaiiSoundManager.playSound("pop")
        }
    }

    fun putStickerToSleep(reason: String = "Put to sleep by user") {
        viewModelScope.launch {
            repository.setStickerSleepState(isAsleep = true, reason = reason)
            KawaiiSoundManager.playSound("soft_click")
            KawaiiHaptics.performSuccessBurst(getApplication())
        }
    }

    fun wakeUpSticker() {
        viewModelScope.launch {
            repository.wakeUpSticker()
            KawaiiSoundManager.playSound("sparkle")
            KawaiiHaptics.performSuccessBurst(getApplication())
        }
    }

    // Custom Gestures
    fun createCustomGesture(
        name: String,
        gestureType: GestureType,
        actionType: ActionType,
        actionTarget: String,
        actionLabel: String,
        soundEffect: String,
        animation: String
    ) {
        viewModelScope.launch {
            val id = "custom_gesture_${System.currentTimeMillis()}"
            val mapping = GestureMapping(
                id = id,
                gestureName = name.ifEmpty { gestureType.displayName },
                gestureType = gestureType,
                actionType = actionType,
                actionTarget = actionTarget,
                actionLabel = actionLabel.ifEmpty { actionType.displayName },
                isEnabled = true,
                activationAnimation = animation,
                soundEffect = soundEffect,
                profileId = currentProfile.value?.id ?: "everyday",
                isCustom = true
            )
            repository.updateGestureMapping(mapping)
            KawaiiSoundManager.playSound("sparkle")
            KawaiiHaptics.performSuccessBurst(getApplication())
        }
    }

    fun deleteGestureMapping(id: String) {
        viewModelScope.launch {
            repository.deleteGestureMapping(id)
            KawaiiSoundManager.playSound("bubble")
            KawaiiHaptics.performClick(getApplication())
        }
    }

    // Custom Animations
    fun createOrUpdateCustomAnimation(
        id: String? = null,
        name: String,
        baseType: String,
        durationMs: Int,
        minScale: Float,
        maxScale: Float,
        translateYDp: Float,
        translateXDp: Float,
        rotationAngleDeg: Float,
        particleType: String,
        easingType: String
    ) {
        viewModelScope.launch {
            val animId = id ?: "custom_anim_${System.currentTimeMillis()}"
            val item = CustomAnimationItem(
                id = animId,
                name = name.ifEmpty { "Cute Motion" },
                baseType = baseType,
                durationMs = durationMs,
                minScale = minScale,
                maxScale = maxScale,
                translateYDp = translateYDp,
                translateXDp = translateXDp,
                rotationAngleDeg = rotationAngleDeg,
                particleType = particleType,
                easingType = easingType,
                isPreset = false
            )
            repository.saveCustomAnimation(item)
            KawaiiSoundManager.playSound("sparkle")
            KawaiiHaptics.performSuccessBurst(getApplication())
        }
    }

    fun deleteCustomAnimation(id: String) {
        viewModelScope.launch {
            repository.deleteCustomAnimation(id)
            if (activeFloatingConfig.value?.customAnimationId == id) {
                repository.setFloatingOverlayAnimation(null)
            }
            KawaiiSoundManager.playSound("bubble")
            KawaiiHaptics.performClick(getApplication())
        }
    }

    fun applyAnimationToActiveOverlay(animId: String?) {
        viewModelScope.launch {
            repository.setFloatingOverlayAnimation(animId)
            KawaiiSoundManager.playSound("sparkle")
            KawaiiHaptics.performSuccessBurst(getApplication())
        }
    }

    fun applyAnimationToSticker(stickerId: String, animId: String) {
        viewModelScope.launch {
            repository.setStickerAnimation(stickerId, animId)
            KawaiiSoundManager.playSound("chime")
            KawaiiHaptics.performClick(getApplication())
        }
    }

    fun toggleGestureEnabled(mappingId: String) {
        viewModelScope.launch {
            repository.toggleGestureEnabled(mappingId)
            KawaiiSoundManager.playSound("soft_click")
        }
    }

    fun updateGestureMapping(mapping: GestureMapping) {
        viewModelScope.launch {
            repository.updateGestureMapping(mapping)
            KawaiiSoundManager.playSound("chime")
        }
    }

    fun switchProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.switchProfile(profile.id)
            val matchingTheme = try {
                AppThemeSetting.valueOf(profile.themeSetting)
            } catch (_: Exception) {
                AppThemeSetting.STRAWBERRY_MILK
            }
            setTheme(matchingTheme)
            KawaiiSoundManager.playSound("chime")
        }
    }

    fun addCustomSticker(
        name: String,
        category: String,
        shapeType: String,
        customText: String,
        imagePath: String?,
        primaryHex: String,
        secondaryHex: String,
        animation: String
    ) {
        viewModelScope.launch {
            val id = "custom_${System.currentTimeMillis()}"
            val newSticker = StickerItem(
                id = id,
                name = name.ifEmpty { "Cute Sticker" },
                category = category,
                iconType = if (!imagePath.isNullOrEmpty()) "custom_image" else if (customText.isNotEmpty()) "custom_text" else "built_in",
                shapeType = shapeType,
                customText = customText,
                customImagePath = imagePath,
                primaryColorHex = primaryHex,
                secondaryColorHex = secondaryHex,
                defaultAnimation = animation,
                isFavorite = true,
                isUserCreated = true
            )
            repository.addCustomSticker(newSticker)
            repository.setActiveSticker(id)
            repository.wakeUpSticker()
            KawaiiSoundManager.playSound("sparkle")
            KawaiiHaptics.performSuccessBurst(getApplication())
        }
    }

    fun exportConfigurationJson(): String {
        val root = JSONObject()
        root.put("version", 2)
        root.put("theme", _currentTheme.value.name)
        root.put("themeMode", _themeMode.value.name)
        root.put("soundEnabled", _isSoundEnabled.value)
        root.put("hapticsEnabled", _isHapticsEnabled.value)

        val cfg = activeFloatingConfig.value
        if (cfg != null) {
            val cfgJson = JSONObject()
            cfgJson.put("stickerId", cfg.stickerId)
            cfgJson.put("sizeDp", cfg.sizeDp)
            cfgJson.put("opacity", cfg.opacity)
            cfgJson.put("snapToEdge", cfg.snapToEdge)
            cfgJson.put("touchThrough", cfg.touchThrough)
            cfgJson.put("activeDurationMinutes", cfg.activeDurationMinutes)
            cfgJson.put("sleepModeEnabled", cfg.sleepModeEnabled)
            root.put("floatingConfig", cfgJson)
        }

        val gesturesArray = JSONArray()
        for (m in gestureMappings.value) {
            val gObj = JSONObject()
            gObj.put("id", m.id)
            gObj.put("gestureType", m.gestureType.name)
            gObj.put("actionType", m.actionType.name)
            gObj.put("actionTarget", m.actionTarget)
            gObj.put("actionLabel", m.actionLabel)
            gObj.put("isEnabled", m.isEnabled)
            gObj.put("soundEffect", m.soundEffect)
            gObj.put("isCustom", m.isCustom)
            gesturesArray.put(gObj)
        }
        root.put("gestures", gesturesArray)
        return root.toString(2)
    }

    fun importConfigurationJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            val themeStr = root.optString("theme", AppThemeSetting.STRAWBERRY_MILK.name)
            setTheme(try { AppThemeSetting.valueOf(themeStr) } catch (_: Exception) { AppThemeSetting.STRAWBERRY_MILK })
            
            val modeStr = root.optString("themeMode", ThemeMode.SYSTEM.name)
            setThemeMode(try { ThemeMode.valueOf(modeStr) } catch (_: Exception) { ThemeMode.SYSTEM })

            if (root.has("floatingConfig")) {
                val cfgJson = root.getJSONObject("floatingConfig")
                val stickerId = cfgJson.optString("stickerId", "cat_white")
                val sizeDp = cfgJson.optInt("sizeDp", 84)
                val opacity = cfgJson.optDouble("opacity", 1.0).toFloat()
                val duration = cfgJson.optInt("activeDurationMinutes", 0)
                viewModelScope.launch {
                    val curr = activeFloatingConfig.value ?: FloatingStickerConfig()
                    repository.updateFloatingConfig(
                        curr.copy(
                            stickerId = stickerId,
                            sizeDp = sizeDp,
                            opacity = opacity,
                            activeDurationMinutes = duration,
                            sleepModeEnabled = duration > 0
                        )
                    )
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
