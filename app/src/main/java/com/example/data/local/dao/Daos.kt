package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ScheduleEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks")
  fun getAllTasksFlow(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks")
  suspend fun getAllTasks(): List<TaskEntity>

  @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
  suspend fun getTaskById(taskId: String): TaskEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTasks(tasks: List<TaskEntity>)

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Query("DELETE FROM tasks WHERE id = :taskId")
  suspend fun deleteTaskById(taskId: String)

  @Query("DELETE FROM tasks")
  suspend fun deleteAllTasks()

  @Query("SELECT COUNT(*) FROM tasks")
  suspend fun getTaskCount(): Int
}

@Dao
interface ProjectDao {
  @Query("SELECT * FROM projects")
  fun getAllProjectsFlow(): Flow<List<ProjectEntity>>

  @Query("SELECT * FROM projects")
  suspend fun getAllProjects(): List<ProjectEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProjects(projects: List<ProjectEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProject(project: ProjectEntity)

  @Update
  suspend fun updateProject(project: ProjectEntity)

  @Query("SELECT COUNT(*) FROM projects")
  suspend fun getProjectCount(): Int
}

@Dao
interface ScheduleDao {
  @Query("SELECT * FROM schedules ORDER BY startTime ASC")
  fun getAllSchedulesFlow(): Flow<List<ScheduleEntity>>

  @Query("SELECT * FROM schedules ORDER BY startTime ASC")
  suspend fun getAllSchedules(): List<ScheduleEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSchedules(schedules: List<ScheduleEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSchedule(schedule: ScheduleEntity)

  @Update
  suspend fun updateSchedule(schedule: ScheduleEntity)

  @Query("SELECT COUNT(*) FROM schedules")
  suspend fun getScheduleCount(): Int
}

@Dao
interface UserProfileDao {
  @Query("SELECT * FROM user_profile WHERE id = 'default_user' LIMIT 1")
  fun getUserProfileFlow(): Flow<UserProfileEntity?>

  @Query("SELECT * FROM user_profile WHERE id = 'default_user' LIMIT 1")
  suspend fun getUserProfile(): UserProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}
