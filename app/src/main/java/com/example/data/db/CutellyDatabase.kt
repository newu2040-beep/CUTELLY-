package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CustomAnimationItem
import com.example.data.model.FloatingStickerConfig
import com.example.data.model.GestureMapping
import com.example.data.model.StickerItem
import com.example.data.model.UserProfile
import com.example.data.preseed.PreseededData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StickerItem::class,
        FloatingStickerConfig::class,
        GestureMapping::class,
        UserProfile::class,
        CustomAnimationItem::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CutellyDatabase : RoomDatabase() {
    abstract fun dao(): CutellyDao

    companion object {
        @Volatile
        private var INSTANCE: CutellyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CutellyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CutellyDatabase::class.java,
                    "cutelly_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        prepopulateDatabase(database.dao())
                    }
                }
            }

            suspend fun prepopulateDatabase(dao: CutellyDao) {
                dao.insertCustomAnimations(PreseededData.defaultCustomAnimations)
                dao.insertStickers(PreseededData.preseededStickers)
                dao.insertProfiles(PreseededData.initialProfiles)
                dao.insertGestureMappings(PreseededData.defaultGestureMappings)
                dao.insertFloatingConfig(
                    FloatingStickerConfig(
                        id = "active_main",
                        stickerId = "cat_white",
                        posXRatio = 0.82f,
                        posYRatio = 0.35f,
                        sizeDp = 84,
                        opacity = 1.0f,
                        rotationDeg = 0.0f,
                        isLocked = false,
                        snapToEdge = true,
                        touchThrough = false,
                        isVisible = true
                    )
                )
            }
        }
    }
}
