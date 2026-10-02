package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.converters.RoomTypeConverters
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.ScheduleDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.dao.UserAccountDao
import com.example.data.local.dao.UserProfileDao
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ScheduleEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.UserAccountEntity
import com.example.data.local.entity.UserProfileEntity

@Database(
  entities = [
    TaskEntity::class,
    ProjectEntity::class,
    ScheduleEntity::class,
    UserProfileEntity::class,
    UserAccountEntity::class
  ],
  version = 2,
  exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class WiiDatabase : RoomDatabase() {

  abstract fun taskDao(): TaskDao
  abstract fun projectDao(): ProjectDao
  abstract fun scheduleDao(): ScheduleDao
  abstract fun userProfileDao(): UserProfileDao
  abstract fun userAccountDao(): UserAccountDao

  companion object {
    @Volatile
    private var INSTANCE: WiiDatabase? = null

    fun getDatabase(context: Context): WiiDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          WiiDatabase::class.java,
          "wii_todo_database.db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
