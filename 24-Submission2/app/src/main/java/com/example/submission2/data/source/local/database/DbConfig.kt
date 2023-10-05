package com.example.submission2.data.source.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.submission2.data.source.local.entity.RemoteKeys
import com.example.submission2.data.source.local.entity.StoryEntity
import com.example.submission2.data.source.local.entity.StoryWithLocationEntity

@Database(
    entities = [StoryEntity::class, StoryWithLocationEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class DbConfig : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun storyWithLocationDao(): StoryWithLocationDao

    companion object {
        @Volatile
        private var INSTANCE: DbConfig? = null

        @JvmStatic
        fun getDatabase(context: Context): DbConfig {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DbConfig::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}