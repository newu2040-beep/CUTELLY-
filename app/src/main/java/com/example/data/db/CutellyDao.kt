package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CustomAnimationItem
import com.example.data.model.FloatingStickerConfig
import com.example.data.model.GestureMapping
import com.example.data.model.StickerItem
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface CutellyDao {
    // Custom Animations
    @Query("SELECT * FROM custom_animations ORDER BY isPreset DESC, createdAt ASC")
    fun getAllCustomAnimations(): Flow<List<CustomAnimationItem>>

    @Query("SELECT * FROM custom_animations WHERE id = :id LIMIT 1")
    suspend fun getCustomAnimationById(id: String): CustomAnimationItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomAnimation(animation: CustomAnimationItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomAnimations(animations: List<CustomAnimationItem>)

    @Query("DELETE FROM custom_animations WHERE id = :id")
    suspend fun deleteCustomAnimation(id: String)
    // Stickers
    @Query("SELECT * FROM stickers ORDER BY isUserCreated DESC, createdAt ASC")
    fun getAllStickers(): Flow<List<StickerItem>>

    @Query("SELECT * FROM stickers WHERE isFavorite = 1")
    fun getFavoriteStickers(): Flow<List<StickerItem>>

    @Query("SELECT * FROM stickers WHERE id = :id LIMIT 1")
    suspend fun getStickerById(id: String): StickerItem?

    @Query("SELECT * FROM stickers WHERE id = :id LIMIT 1")
    fun getStickerFlowById(id: String): Flow<StickerItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStickers(stickers: List<StickerItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSticker(sticker: StickerItem)

    @Update
    suspend fun updateSticker(sticker: StickerItem)

    @Query("DELETE FROM stickers WHERE id = :id")
    suspend fun deleteStickerById(id: String)

    @Query("SELECT COUNT(*) FROM stickers")
    suspend fun getStickersCount(): Int

    // Floating Stickers
    @Query("SELECT * FROM floating_stickers")
    fun getAllFloatingStickers(): Flow<List<FloatingStickerConfig>>

    @Query("SELECT * FROM floating_stickers WHERE id = :id LIMIT 1")
    fun getFloatingStickerConfigFlow(id: String): Flow<FloatingStickerConfig?>

    @Query("SELECT * FROM floating_stickers WHERE id = :id LIMIT 1")
    suspend fun getFloatingStickerConfig(id: String): FloatingStickerConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFloatingConfig(config: FloatingStickerConfig)

    @Update
    suspend fun updateFloatingConfig(config: FloatingStickerConfig)

    // Gestures
    @Query("SELECT * FROM gesture_mappings ORDER BY id ASC")
    fun getAllGestureMappings(): Flow<List<GestureMapping>>

    @Query("SELECT * FROM gesture_mappings WHERE isEnabled = 1")
    fun getEnabledGestureMappings(): Flow<List<GestureMapping>>

    @Query("SELECT * FROM gesture_mappings WHERE id = :id LIMIT 1")
    suspend fun getGestureMapping(id: String): GestureMapping?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGestureMappings(mappings: List<GestureMapping>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGestureMapping(mapping: GestureMapping)

    @Update
    suspend fun updateGestureMapping(mapping: GestureMapping)

    @Query("DELETE FROM gesture_mappings WHERE id = :id")
    suspend fun deleteGestureMapping(id: String)

    // User Profiles
    @Query("SELECT * FROM user_profiles")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<UserProfile>)

    @Query("UPDATE user_profiles SET isCurrent = CASE WHEN id = :profileId THEN 1 ELSE 0 END")
    suspend fun setActiveProfile(profileId: String)
}
