package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.KanbanColumn
import com.example.data.model.ProjectItem
import com.example.data.model.ScheduleCommitment
import com.example.data.model.SubTask
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.data.model.UserProfile

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey val id: String,
  val title: String,
  val description: String = "",
  val category: String = "Work",
  val project: String = "General",
  val priority: TaskPriority = TaskPriority.MED,
  val dueTime: String = "Today",
  val dueDate: String = "Today (Oct 24)",
  val isCompleted: Boolean = false,
  val completedAt: String? = null,
  val subtasks: List<SubTask> = emptyList(),
  val attachments: List<com.example.data.model.AttachmentItem> = emptyList(),
  val tags: List<String> = emptyList(),
  val kanbanStatus: KanbanColumn = KanbanColumn.TO_DO,
  val estimatedEffortMinutes: Int = 30,
  val assignedTo: String = "Alwi Pratama",
  val urgencyBadge: String? = null,
  val attachmentCount: Int? = null,
  val subtaskBadge: String? = null,
  val subtasksCompletedCount: Int? = null,
  val subtasksTotalCount: Int? = null,
  val isTomorrow: Boolean = false,
  val courseName: String? = null,
  val isDeleted: Boolean = false,
  val deletedAt: Long? = null
)

fun TaskEntity.toModel(): TaskItem = TaskItem(
  id = id,
  title = title,
  description = description,
  category = category,
  project = project,
  priority = priority,
  dueTime = dueTime,
  dueDate = dueDate,
  isCompleted = isCompleted,
  completedAt = completedAt,
  subtasks = subtasks,
  attachments = attachments,
  tags = tags,
  kanbanStatus = kanbanStatus,
  estimatedEffortMinutes = estimatedEffortMinutes,
  assignedTo = assignedTo,
  urgencyBadge = urgencyBadge,
  attachmentCount = if (attachments.isNotEmpty()) attachments.size else attachmentCount,
  subtaskBadge = subtaskBadge,
  subtasksCompletedCount = subtasksCompletedCount,
  subtasksTotalCount = subtasksTotalCount,
  isTomorrow = isTomorrow,
  courseName = courseName,
  isDeleted = isDeleted,
  deletedAt = deletedAt
)

fun TaskItem.toEntity(): TaskEntity = TaskEntity(
  id = id,
  title = title,
  description = description,
  category = category,
  project = project,
  priority = priority,
  dueTime = dueTime,
  dueDate = dueDate,
  isCompleted = isCompleted,
  completedAt = completedAt,
  subtasks = subtasks,
  attachments = attachments,
  tags = tags,
  kanbanStatus = kanbanStatus,
  estimatedEffortMinutes = estimatedEffortMinutes,
  assignedTo = assignedTo,
  urgencyBadge = urgencyBadge,
  attachmentCount = if (attachments.isNotEmpty()) attachments.size else attachmentCount,
  subtaskBadge = subtaskBadge,
  subtasksCompletedCount = subtasksCompletedCount,
  subtasksTotalCount = subtasksTotalCount,
  isTomorrow = isTomorrow,
  courseName = courseName,
  isDeleted = isDeleted,
  deletedAt = deletedAt
)

@Entity(tableName = "projects")
data class ProjectEntity(
  @PrimaryKey val id: String,
  val title: String,
  val description: String,
  val category: String,
  val code: String,
  val dueDate: String,
  val totalTasks: Int,
  val completedTasks: Int,
  val activeSprint: String = "Sprint #2 Active",
  val nextTaskPreview: String = "",
  val nextTaskDue: String = "",
  val tags: List<String> = emptyList(),
  val attachments: List<com.example.data.model.AttachmentItem> = emptyList()
)

fun ProjectEntity.toModel(): ProjectItem = ProjectItem(
  id = id,
  title = title,
  description = description,
  category = category,
  code = code,
  dueDate = dueDate,
  totalTasks = totalTasks,
  completedTasks = completedTasks,
  activeSprint = activeSprint,
  nextTaskPreview = nextTaskPreview,
  nextTaskDue = nextTaskDue,
  tags = tags,
  attachments = attachments
)

fun ProjectItem.toEntity(): ProjectEntity = ProjectEntity(
  id = id,
  title = title,
  description = description,
  category = category,
  code = code,
  dueDate = dueDate,
  totalTasks = totalTasks,
  completedTasks = completedTasks,
  activeSprint = activeSprint,
  nextTaskPreview = nextTaskPreview,
  nextTaskDue = nextTaskDue,
  tags = tags,
  attachments = attachments
)

@Entity(tableName = "schedules")
data class ScheduleEntity(
  @PrimaryKey val id: String,
  val startTime: String,
  val endTime: String,
  val title: String,
  val subtitle: String,
  val category: String,
  val location: String? = null,
  val isCompleted: Boolean = false,
  val isCurrentFocus: Boolean = false,
  val isDeepWork: Boolean = false
)

fun ScheduleEntity.toModel(): ScheduleCommitment = ScheduleCommitment(
  id = id,
  startTime = startTime,
  endTime = endTime,
  title = title,
  subtitle = subtitle,
  category = category,
  location = location,
  isCompleted = isCompleted,
  isCurrentFocus = isCurrentFocus,
  isDeepWork = isDeepWork
)

fun ScheduleItemToEntity(item: ScheduleCommitment): ScheduleEntity = ScheduleEntity(
  id = item.id,
  startTime = item.startTime,
  endTime = item.endTime,
  title = item.title,
  subtitle = item.subtitle,
  category = item.category,
  location = item.location,
  isCompleted = item.isCompleted,
  isCurrentFocus = item.isCurrentFocus,
  isDeepWork = item.isDeepWork
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey val id: String = "default_user",
  val name: String = "Alwi Pratama",
  val email: String = "alwinizam0405@gmail.com",
  val program: String = "Teknik Informatika • Semester 5",
  val university: String = "Universitas Teknologi Just Wiu",
  val studentId: String = "2110512044",
  val focusGoal: String = "Fokus Skripsi & Arsitektur UI Mobile",
  val avatarUri: String? = null,
  val avatarPresetId: String = "architect_1",
  val avatarColorHex: Long = 0xFF1E293B,
  val level: Int = 5,
  val levelTitle: String = "Focus Architect",
  val currentXp: Int = 1000,
  val targetXp: Int = 1500,
  val totalXpAllTime: Int = 2850,
  val streakDays: Int = 12,
  val tasksDoneCount: Int = 43,
  val onTimeRate: String = "97%",
  val focusHoursLogged: String = "18.5h",
  val campusSyncEnabled: Boolean = true,
  val morningBriefingEnabled: Boolean = true,
  val autoFocusMode: Boolean = true,
  val hapticFeedback: Boolean = true,
  val completionSounds: Boolean = true,
  val targetGpa: Double = 3.85,
  val targetSks: Int = 21,
  val targetDailyFocusHours: Double = 4.0
)

fun UserProfileEntity.toModel(): UserProfile = UserProfile(
  name = name,
  email = email,
  program = program,
  university = university,
  studentId = studentId,
  focusGoal = focusGoal,
  avatarUri = avatarUri,
  avatarPresetId = avatarPresetId,
  avatarColorHex = avatarColorHex,
  level = level,
  levelTitle = levelTitle,
  currentXp = currentXp,
  targetXp = targetXp,
  totalXpAllTime = totalXpAllTime,
  streakDays = streakDays,
  tasksDoneCount = tasksDoneCount,
  onTimeRate = onTimeRate,
  focusHoursLogged = focusHoursLogged,
  campusSyncEnabled = campusSyncEnabled,
  morningBriefingEnabled = morningBriefingEnabled,
  autoFocusMode = autoFocusMode,
  hapticFeedback = hapticFeedback,
  completionSounds = completionSounds,
  targetGpa = targetGpa,
  targetSks = targetSks,
  targetDailyFocusHours = targetDailyFocusHours
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
  name = name,
  email = email,
  program = program,
  university = university,
  studentId = studentId,
  focusGoal = focusGoal,
  avatarUri = avatarUri,
  avatarPresetId = avatarPresetId,
  avatarColorHex = avatarColorHex,
  level = level,
  levelTitle = levelTitle,
  currentXp = currentXp,
  targetXp = targetXp,
  totalXpAllTime = totalXpAllTime,
  streakDays = streakDays,
  tasksDoneCount = tasksDoneCount,
  onTimeRate = onTimeRate,
  focusHoursLogged = focusHoursLogged,
  campusSyncEnabled = campusSyncEnabled,
  morningBriefingEnabled = morningBriefingEnabled,
  autoFocusMode = autoFocusMode,
  hapticFeedback = hapticFeedback,
  completionSounds = completionSounds,
  targetGpa = targetGpa,
  targetSks = targetSks,
  targetDailyFocusHours = targetDailyFocusHours
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
  @PrimaryKey val email: String, // Normalized lowercase
  val passwordHash: String,
  val salt: String,
  val displayName: String,
  val programOrWorkspace: String = "Personal Workspace",
  val createdAt: Long = System.currentTimeMillis()
)
