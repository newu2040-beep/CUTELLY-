package com.example.data.repository

import com.example.data.db.CutellyDao
import com.example.data.model.CustomAnimationItem
import com.example.data.model.FloatingStickerConfig
import com.example.data.model.GestureMapping
import com.example.data.model.StickerItem
import com.example.data.model.UserProfile
import com.example.data.preseed.PreseededData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CutellyRepository(private val dao: CutellyDao) {

    val allStickers: Flow<List<StickerItem>> = dao.getAllStickers()
    val favoriteStickers: Flow<List<StickerItem>> = dao.getFavoriteStickers()
    val gestureMappings: Flow<List<GestureMapping>> = dao.getAllGestureMappings()
    val userProfiles: Flow<List<UserProfile>> = dao.getAllProfiles()
    val currentProfile: Flow<UserProfile?> = dao.getCurrentProfileFlow()
    val activeFloatingConfig: Flow<FloatingStickerConfig?> = dao.getFloatingStickerConfigFlow("active_main")
    val allCustomAnimations: Flow<List<CustomAnimationItem>> = dao.getAllCustomAnimations()

    suspend fun ensureInitialized() {
        if (dao.getStickersCount() == 0) {
            dao.insertCustomAnimations(PreseededData.defaultCustomAnimations)
            dao.insertStickers(PreseededData.preseededStickers)
            dao.insertProfiles(PreseededData.initialProfiles)
            dao.insertGestureMappings(PreseededData.defaultGestureMappings)
            dao.insertFloatingConfig(
                FloatingStickerConfig(
                    id = "active_main",
                    stickerId = "cat_white"
                )
            )
        } else {
            // Check if custom animations are populated
            val anims = dao.getAllCustomAnimations().first()
            if (anims.isEmpty()) {
                dao.insertCustomAnimations(PreseededData.defaultCustomAnimations)
            }
        }
    }

    suspend fun getCustomAnimation(id: String): CustomAnimationItem? = dao.getCustomAnimationById(id)

    suspend fun saveCustomAnimation(anim: CustomAnimationItem) {
        dao.insertCustomAnimation(anim)
    }

    suspend fun deleteCustomAnimation(id: String) {
        dao.deleteCustomAnimation(id)
    }

    suspend fun setStickerAnimation(stickerId: String, animationId: String) {
        val sticker = dao.getStickerById(stickerId)
        if (sticker != null) {
            dao.updateSticker(sticker.copy(defaultAnimation = animationId))
        }
    }

    suspend fun setFloatingOverlayAnimation(animationId: String?) {
        val current = dao.getFloatingStickerConfig("active_main")
        if (current != null) {
            dao.updateFloatingConfig(current.copy(customAnimationId = animationId))
        }
    }

    suspend fun getSticker(id: String): StickerItem? = dao.getStickerById(id)

    suspend fun toggleFavorite(sticker: StickerItem) {
        dao.updateSticker(sticker.copy(isFavorite = !sticker.isFavorite))
    }

    suspend fun addCustomSticker(sticker: StickerItem) {
        dao.insertSticker(sticker)
    }

    suspend fun deleteSticker(id: String) {
        dao.deleteStickerById(id)
    }

    suspend fun setActiveSticker(stickerId: String) {
        val current = dao.getFloatingStickerConfig("active_main")
        if (current != null) {
            dao.updateFloatingConfig(current.copy(stickerId = stickerId, isVisible = true))
        } else {
            dao.insertFloatingConfig(FloatingStickerConfig("active_main", stickerId = stickerId))
        }
    }

    suspend fun updateFloatingConfig(config: FloatingStickerConfig) {
        dao.insertFloatingConfig(config)
    }

    suspend fun updateFloatingPosition(posXRatio: Float, posYRatio: Float) {
        val current = dao.getFloatingStickerConfig("active_main")
        if (current != null) {
            dao.updateFloatingConfig(current.copy(posXRatio = posXRatio, posYRatio = posYRatio))
        }
    }

    suspend fun toggleFloatingVisibility() {
        val current = dao.getFloatingStickerConfig("active_main")
        if (current != null) {
            dao.updateFloatingConfig(current.copy(isVisible = !current.isVisible))
        }
    }

    suspend fun setFloatingVisibility(visible: Boolean) {
        val current = dao.getFloatingStickerConfig("active_main")
        if (current != null) {
            dao.updateFloatingConfig(current.copy(isVisible = visible))
        }
    }

    suspend fun toggleGestureEnabled(mappingId: String) {
        val current = dao.getGestureMapping(mappingId)
        if (current != null) {
            dao.updateGestureMapping(current.copy(isEnabled = !current.isEnabled))
        }
    }

    suspend fun updateGestureMapping(mapping: GestureMapping) {
        dao.insertGestureMapping(mapping)
    }

    suspend fun deleteGestureMapping(id: String) {
        dao.deleteGestureMapping(id)
    }

    suspend fun setStickerSleepState(isAsleep: Boolean, reason: String = "") {
        val current = dao.getFloatingStickerConfig("active_main")
        if (current != null) {
            dao.updateFloatingConfig(
                current.copy(
                    isAsleep = isAsleep,
                    sleepReason = reason,
                    activeStartTime = if (!isAsleep) System.currentTimeMillis() else current.activeStartTime
                )
            )
        }
    }

    suspend fun wakeUpSticker() {
        val current = dao.getFloatingStickerConfig("active_main")
        if (current != null) {
            dao.updateFloatingConfig(
                current.copy(
                    isAsleep = false,
                    isVisible = true,
                    sleepReason = "",
                    activeStartTime = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun updateActiveDuration(minutes: Int) {
        val current = dao.getFloatingStickerConfig("active_main")
        if (current != null) {
            dao.updateFloatingConfig(
                current.copy(
                    activeDurationMinutes = minutes,
                    sleepModeEnabled = minutes > 0,
                    activeStartTime = System.currentTimeMillis(),
                    isAsleep = false
                )
            )
        }
    }

    suspend fun updateSleepSchedule(
        enabled: Boolean,
        startHour: Int,
        startMin: Int,
        endHour: Int,
        endMin: Int
    ) {
        val current = dao.getFloatingStickerConfig("active_main")
        if (current != null) {
            dao.updateFloatingConfig(
                current.copy(
                    scheduleEnabled = enabled,
                    scheduledSleepStartHour = startHour,
                    scheduledSleepStartMinute = startMin,
                    scheduledSleepEndHour = endHour,
                    scheduledSleepEndMinute = endMin
                )
            )
        }
    }

    suspend fun switchProfile(profileId: String) {
        dao.setActiveProfile(profileId)
    }
}
