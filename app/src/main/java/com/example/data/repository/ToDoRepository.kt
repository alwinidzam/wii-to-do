package com.example.data.repository

import android.content.Context
import com.example.data.local.WiiDatabase
import com.example.data.local.entity.ScheduleItemToEntity
import com.example.data.local.entity.toEntity
import com.example.data.local.entity.toModel
import com.example.data.model.FocusSessionState
import com.example.data.model.KanbanColumn
import com.example.data.model.MilestoneTierLevel
import com.example.data.model.ProjectItem
import com.example.data.model.ScheduleCommitment
import com.example.data.model.ShareCardConfig
import com.example.data.model.SubTask
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.data.auth.AuthField
import com.example.data.auth.AuthResult
import com.example.data.local.entity.UserAccountEntity
import com.example.data.security.PasswordHasher
import java.util.UUID

class ToDoRepository private constructor(
  private val db: WiiDatabase? = null,
  private val appContext: Context? = null
) {

  companion object {
    @Volatile
    private var instance: ToDoRepository? = null
    private var database: WiiDatabase? = null
    private var applicationContext: Context? = null

    fun initialize(context: Context) {
      if (instance == null) {
        synchronized(this) {
          if (instance == null) {
            val appCtx = context.applicationContext
            applicationContext = appCtx
            val databaseInstance = WiiDatabase.getDatabase(appCtx)
            database = databaseInstance
            val repo = ToDoRepository(databaseInstance, appCtx)
            instance = repo
            repo.startDatabaseSync()
          }
        }
      }
    }

    fun getInstance(): ToDoRepository {
      return instance ?: synchronized(this) {
        instance ?: ToDoRepository(database, applicationContext).also {
          instance = it
          it.startDatabaseSync()
        }
      }
    }
  }

  fun isUserLoggedIn(): Boolean {
    val ctx = appContext ?: applicationContext ?: return false
    val prefs = ctx.getSharedPreferences("wii_todo_auth_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("is_logged_in", false)
  }

  fun setUserLoggedIn(loggedIn: Boolean) {
    val ctx = appContext ?: applicationContext ?: return
    val prefs = ctx.getSharedPreferences("wii_todo_auth_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("is_logged_in", loggedIn).apply()
  }

  fun getAppLanguage(): String {
    val ctx = appContext ?: applicationContext ?: return "id"
    val prefs = ctx.getSharedPreferences("wii_todo_app_prefs", Context.MODE_PRIVATE)
    return prefs.getString("wii_app_language", "id") ?: "id"
  }

  fun setAppLanguage(langCode: String) {
    val ctx = appContext ?: applicationContext ?: return
    val prefs = ctx.getSharedPreferences("wii_todo_app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("wii_app_language", langCode).apply()
  }

  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  fun startDatabaseSync() {
    val dbInstance = db ?: return
    scope.launch {
      try {
        ensureDeveloperAccountSeeded(dbInstance)
        if (!isUserLoggedIn()) {
          dbInstance.taskDao().deleteAllTasks()
          dbInstance.scheduleDao().deleteAllSchedules()
          dbInstance.userProfileDao().insertOrUpdateProfile(getDefaultFreshProfile().toEntity())
        } else if (dbInstance.userProfileDao().getUserProfile() == null) {
          dbInstance.userProfileDao().insertOrUpdateProfile(getDefaultFreshProfile().toEntity())
        }

        if (dbInstance.projectDao().getProjectCount() == 0) {
          dbInstance.projectDao().insertProjects(getDefaultFreshProjects().map { it.toEntity() })
        }

        launch {
          dbInstance.taskDao().getAllTasksFlow().collect { entities ->
            _tasks.value = entities.map { it.toModel() }
            updateProjectTaskCounts()
          }
        }

        launch {
          dbInstance.projectDao().getAllProjectsFlow().collect { entities ->
            _projects.value = entities.map { it.toModel() }
          }
        }

        launch {
          dbInstance.scheduleDao().getAllSchedulesFlow().collect { entities ->
            _schedule.value = entities.map { it.toModel() }
          }
        }

        launch {
          dbInstance.userProfileDao().getUserProfileFlow().collect { entity ->
            if (entity != null) {
              _userProfile.value = entity.toModel()
            }
          }
        }
      } catch (_: Exception) {
        // Fallback to in-memory state gracefully
      }
    }
  }

  private fun getDefaultFreshProjects(): List<ProjectItem> = listOf(
    ProjectItem(
      id = "proj_inbox",
      title = "Inbox",
      description = "General tasks and quick thoughts",
      category = "Personal",
      code = "Personal",
      dueDate = "Ongoing",
      totalTasks = 0,
      completedTasks = 0,
      activeSprint = "Sprint #1",
      nextTaskPreview = "",
      nextTaskDue = ""
    )
  )

  fun loginAsDeveloper() {
    setUserLoggedIn(true)
    val devTasks = getInitialTasks()
    val devProjects = getInitialProjects()
    val devSchedule = getInitialSchedule()
    val devProfile = UserProfile(
      name = "Alwi Pratama",
      email = "alwinizam0405@gmail.com",
      program = "Informatics Engineering • Year 3",
      focusGoal = "Focusing on Thesis & UI Architecture",
      level = 5,
      levelTitle = "Focus Architect",
      currentXp = 1000,
      targetXp = 1500,
      totalXpAllTime = 3850,
      streakDays = 14,
      tasksDoneCount = 43,
      onTimeRate = "99%",
      focusHoursLogged = "24.5h",
      campusSyncEnabled = true,
      morningBriefingEnabled = true,
      autoFocusMode = true,
      hapticFeedback = true,
      completionSounds = false
    )

    _tasks.value = devTasks
    _projects.value = devProjects
    _schedule.value = devSchedule
    _userProfile.value = devProfile
    _activeFocusSession.value = null
    updateProjectTaskCounts()

    scope.launch {
      try {
        val dbInstance = db ?: return@launch
        dbInstance.taskDao().deleteAllTasks()
        dbInstance.taskDao().insertTasks(devTasks.map { it.toEntity() })

        dbInstance.projectDao().deleteAllProjects()
        dbInstance.projectDao().insertProjects(devProjects.map { it.toEntity() })

        dbInstance.scheduleDao().deleteAllSchedules()
        dbInstance.scheduleDao().insertSchedules(devSchedule.map { ScheduleItemToEntity(it) })

        dbInstance.userProfileDao().insertOrUpdateProfile(devProfile.toEntity())
      } catch (_: Exception) {}
    }
  }

  fun loginAsFreshUser(
    name: String = "Alwi",
    email: String = "user@wiitodo.app",
    program: String = "Personal Workspace"
  ) {
    setUserLoggedIn(true)
    val freshProjects = getDefaultFreshProjects()
    val freshProfile = UserProfile(
      name = name.ifBlank { "User" },
      email = email.ifBlank { "user@wiitodo.app" },
      program = program.ifBlank { "Personal Workspace" },
      focusGoal = "Focusing on daily goals",
      level = 1,
      levelTitle = "Novice Scholar",
      currentXp = 0,
      targetXp = 500,
      totalXpAllTime = 0,
      streakDays = 1,
      tasksDoneCount = 0,
      onTimeRate = "100%",
      focusHoursLogged = "0h",
      campusSyncEnabled = false,
      morningBriefingEnabled = false,
      autoFocusMode = false,
      hapticFeedback = true,
      completionSounds = false,
      isVerified = false
    )

    _tasks.value = emptyList()
    _projects.value = freshProjects
    _schedule.value = emptyList()
    _userProfile.value = freshProfile
    _activeFocusSession.value = null

    scope.launch {
      try {
        val dbInstance = db ?: return@launch
        dbInstance.taskDao().deleteAllTasks()
        dbInstance.scheduleDao().deleteAllSchedules()
        dbInstance.projectDao().deleteAllProjects()
        dbInstance.projectDao().insertProjects(freshProjects.map { it.toEntity() })
        dbInstance.userProfileDao().insertOrUpdateProfile(freshProfile.toEntity())
      } catch (_: Exception) {}
    }
  }

  fun getDefaultFreshProfile(): UserProfile = UserProfile(
    name = "User",
    email = "",
    program = "Personal Workspace",
    focusGoal = "Focusing on daily goals",
    level = 1,
    levelTitle = "Novice Scholar",
    currentXp = 0,
    targetXp = 500,
    totalXpAllTime = 0,
    streakDays = 1,
    tasksDoneCount = 0,
    onTimeRate = "100%",
    focusHoursLogged = "0h",
    campusSyncEnabled = false,
    morningBriefingEnabled = false,
    autoFocusMode = false,
    hapticFeedback = true,
    completionSounds = false,
    isVerified = false
  )

  fun signOutUser() {
    setUserLoggedIn(false)
    _activeFocusSession.value = null
    _tasks.value = emptyList()
    _schedule.value = emptyList()
    _projects.value = getDefaultFreshProjects()
    val fresh = getDefaultFreshProfile()
    _userProfile.value = fresh
    scope.launch {
      try {
        val dbInstance = db ?: return@launch
        dbInstance.taskDao().deleteAllTasks()
        dbInstance.scheduleDao().deleteAllSchedules()
        dbInstance.userProfileDao().insertOrUpdateProfile(fresh.toEntity())
      } catch (_: Exception) {}
    }
  }

  suspend fun ensureDeveloperAccountSeeded(dbInstance: WiiDatabase) {
    try {
      val devEmail = "alwinizam0405@gmail.com"
      val existing = dbInstance.userAccountDao().getUserByEmail(devEmail)
      if (existing == null) {
        val devSalt = PasswordHasher.generateSalt()
        val devHash = PasswordHasher.hashPassword("justwiu1", devSalt)
        dbInstance.userAccountDao().insertUser(
          UserAccountEntity(
            email = devEmail,
            passwordHash = devHash,
            salt = devSalt,
            displayName = "Alwi Pratama",
            programOrWorkspace = "Informatics Engineering • Year 3"
          )
        )
      }
    } catch (_: Exception) {}
  }

  suspend fun authenticateUser(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
    val cleanEmail = email.trim().lowercase()
    val cleanPassword = password.trim()

    if (cleanEmail.isBlank()) {
      return@withContext AuthResult.Error("Masukkan alamat email.", AuthField.EMAIL)
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
      return@withContext AuthResult.Error("Format email tidak valid.", AuthField.EMAIL)
    }
    if (cleanPassword.isBlank()) {
      return@withContext AuthResult.Error("Masukkan kata sandi.", AuthField.PASSWORD)
    }

    val dbInstance = db ?: applicationContext?.let { WiiDatabase.getDatabase(it) }
      ?: return@withContext AuthResult.Error("Database belum diinisialisasi.", AuthField.GENERAL)

    ensureDeveloperAccountSeeded(dbInstance)

    val account = dbInstance.userAccountDao().getUserByEmail(cleanEmail)
      ?: return@withContext AuthResult.Error("Akun tidak ditemukan. Silakan daftar terlebih dahulu.", AuthField.EMAIL)

    val isPasswordValid = PasswordHasher.verifyPassword(cleanPassword, account.salt, account.passwordHash)
    if (!isPasswordValid) {
      return@withContext AuthResult.Error("Kata sandi salah. Silakan coba lagi.", AuthField.PASSWORD)
    }

    val isDev = cleanEmail == "alwinizam0405@gmail.com"
    if (isDev) {
      loginAsDeveloper()
      AuthResult.Success(userProfile.value, isDeveloper = true)
    } else {
      loginAsFreshUser(
        name = account.displayName,
        email = account.email,
        program = account.programOrWorkspace
      )
      AuthResult.Success(userProfile.value, isDeveloper = false)
    }
  }

  suspend fun registerUser(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    program: String
  ): AuthResult = withContext(Dispatchers.IO) {
    val cleanName = name.trim()
    val cleanEmail = email.trim().lowercase()
    val cleanPassword = password.trim()
    val cleanConfirm = confirmPassword.trim()
    val cleanProgram = program.trim().ifBlank { "Personal Workspace" }

    if (cleanName.isBlank()) {
      return@withContext AuthResult.Error("Nama lengkap tidak boleh kosong.", AuthField.NAME)
    }
    if (cleanEmail.isBlank()) {
      return@withContext AuthResult.Error("Email tidak boleh kosong.", AuthField.EMAIL)
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
      return@withContext AuthResult.Error("Format email tidak valid.", AuthField.EMAIL)
    }
    if (cleanPassword.length < 6) {
      return@withContext AuthResult.Error("Kata sandi minimal 6 karakter.", AuthField.PASSWORD)
    }
    if (cleanPassword != cleanConfirm) {
      return@withContext AuthResult.Error("Konfirmasi kata sandi tidak cocok.", AuthField.CONFIRM_PASSWORD)
    }

    val dbInstance = db ?: applicationContext?.let { WiiDatabase.getDatabase(it) }
      ?: return@withContext AuthResult.Error("Database belum diinisialisasi.", AuthField.GENERAL)

    ensureDeveloperAccountSeeded(dbInstance)

    val count = dbInstance.userAccountDao().emailExists(cleanEmail)
    if (count > 0) {
      return@withContext AuthResult.Error("Email sudah terdaftar. Silakan login.", AuthField.EMAIL)
    }

    val salt = PasswordHasher.generateSalt()
    val hash = PasswordHasher.hashPassword(cleanPassword, salt)
    val newAccount = UserAccountEntity(
      email = cleanEmail,
      passwordHash = hash,
      salt = salt,
      displayName = cleanName,
      programOrWorkspace = cleanProgram
    )

    dbInstance.userAccountDao().insertUser(newAccount)

    // Fresh login with 0 tasks, 0 XP, Level 1
    loginAsFreshUser(
      name = cleanName,
      email = cleanEmail,
      program = cleanProgram
    )

    AuthResult.Success(userProfile.value, isDeveloper = false)
  }

  private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
  val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

  private val _projects = MutableStateFlow<List<ProjectItem>>(getDefaultFreshProjects())
  val projects: StateFlow<List<ProjectItem>> = _projects.asStateFlow()

  private val _schedule = MutableStateFlow<List<ScheduleCommitment>>(emptyList())
  val schedule: StateFlow<List<ScheduleCommitment>> = _schedule.asStateFlow()

  private val _userProfile = MutableStateFlow(getDefaultFreshProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _activeFocusSession = MutableStateFlow<FocusSessionState?>(null)
  val activeFocusSession: StateFlow<FocusSessionState?> = _activeFocusSession.asStateFlow()

  private val _lastCompletedTaskCelebration = MutableStateFlow<TaskItem?>(null)
  val lastCompletedTaskCelebration: StateFlow<TaskItem?> = _lastCompletedTaskCelebration.asStateFlow()

  private val _showLevelUpModal = MutableStateFlow(false)
  val showLevelUpModal: StateFlow<Boolean> = _showLevelUpModal.asStateFlow()

  private val _academicCourses = MutableStateFlow<List<com.example.data.model.AcademicCourse>>(
    com.example.data.model.AcademicCourseDefaults.PRESET_COURSES
  )
  val academicCourses: StateFlow<List<com.example.data.model.AcademicCourse>> = _academicCourses.asStateFlow()

  fun addAcademicCourse(course: com.example.data.model.AcademicCourse) {
    if (_academicCourses.value.none { it.name.equals(course.name, ignoreCase = true) }) {
      _academicCourses.value = _academicCourses.value + course
    }
  }

  fun dismissCelebration() {
    _lastCompletedTaskCelebration.value = null
  }

  fun dismissLevelUpModal() {
    _showLevelUpModal.value = false
  }

  fun triggerLevelUpCelebration() {
    _showLevelUpModal.value = true
  }

  private val _sprintReflections = MutableStateFlow<List<com.example.data.model.SprintReflection>>(emptyList())
  val sprintReflections: StateFlow<List<com.example.data.model.SprintReflection>> = _sprintReflections.asStateFlow()

  private val _shareCardConfig = MutableStateFlow(ShareCardConfig())
  val shareCardConfig: StateFlow<ShareCardConfig> = _shareCardConfig.asStateFlow()

  fun updateShareCardConfig(config: ShareCardConfig) {
    _shareCardConfig.value = config
  }

  fun toggleTaskCompletion(taskId: String) {
    var justCompleted: TaskItem? = null
    _tasks.value = _tasks.value.map { task ->
      if (task.id == taskId) {
        val newStatus = !task.isCompleted
        val updated = task.copy(
          isCompleted = newStatus,
          completedAt = if (newStatus) "Today" else null,
          kanbanStatus = if (newStatus) KanbanColumn.DONE else KanbanColumn.IN_PROGRESS
        )
        if (newStatus) {
          justCompleted = updated
          awardXp(120)
        }
        scope.launch { try { db?.taskDao()?.updateTask(updated.toEntity()) } catch (_: Exception) {} }
        updated
      } else {
        task
      }
    }
    updateProjectTaskCounts()
    if (justCompleted != null) {
      _lastCompletedTaskCelebration.value = justCompleted
    }
  }

  fun addTask(
    title: String,
    description: String = "",
    category: String = "Work",
    project: String = "Design System Migration",
    priority: TaskPriority = TaskPriority.MED,
    dueTime: String = "Today",
    dueDate: String = "Today (Oct 24)",
    estimatedEffort: Int = 30,
    courseName: String? = null
  ): String {
    val id = UUID.randomUUID().toString()
    val newTask = TaskItem(
      id = id,
      title = title,
      description = description,
      category = category,
      courseName = courseName,
      project = project,
      priority = priority,
      dueTime = dueTime,
      dueDate = dueDate,
      isCompleted = false,
      kanbanStatus = KanbanColumn.TO_DO,
      estimatedEffortMinutes = estimatedEffort,
      subtasks = emptyList(),
      subtasksCompletedCount = 0,
      subtasksTotalCount = 0
    )
    _tasks.value = listOf(newTask) + _tasks.value
    updateProjectTaskCounts()
    scope.launch { try { db?.taskDao()?.insertTask(newTask.toEntity()) } catch (_: Exception) {} }
    return id
  }

  fun toggleSubTask(taskId: String, subtaskId: String) {
    _tasks.value = _tasks.value.map { task ->
      if (task.id == taskId) {
        val updatedSubtasks = task.subtasks.map { sub ->
          if (sub.id == subtaskId) {
            sub.copy(
              isCompleted = !sub.isCompleted,
              completedAt = if (!sub.isCompleted) "Today" else null
            )
          } else {
            sub
          }
        }
        val doneCount = updatedSubtasks.count { it.isCompleted }
        val totalCount = updatedSubtasks.size
        val updated = task.copy(
          subtasks = updatedSubtasks,
          subtasksCompletedCount = doneCount,
          subtasksTotalCount = totalCount,
          subtaskBadge = if (totalCount > 0) "$doneCount/$totalCount sub-tasks" else null
        )
        scope.launch { try { db?.taskDao()?.updateTask(updated.toEntity()) } catch (_: Exception) {} }
        updated
      } else {
        task
      }
    }
  }

  fun deleteTask(taskId: String) {
    _tasks.value = _tasks.value.filter { it.id != taskId }
    updateProjectTaskCounts()
    scope.launch { try { db?.taskDao()?.deleteTaskById(taskId) } catch (_: Exception) {} }
  }

  fun addSubTask(taskId: String, title: String, effortMinutes: Int = 30) {
    val newSub = SubTask(
      id = UUID.randomUUID().toString(),
      title = title,
      effortMinutes = effortMinutes,
      isCompleted = false
    )
    _tasks.value = _tasks.value.map { task ->
      if (task.id == taskId) {
        val updatedSubtasks = task.subtasks + newSub
        val doneCount = updatedSubtasks.count { it.isCompleted }
        val totalCount = updatedSubtasks.size
        val updated = task.copy(
          subtasks = updatedSubtasks,
          subtasksCompletedCount = doneCount,
          subtasksTotalCount = totalCount,
          subtaskBadge = "$doneCount/$totalCount sub-tasks"
        )
        scope.launch { try { db?.taskDao()?.updateTask(updated.toEntity()) } catch (_: Exception) {} }
        updated
      } else {
        task
      }
    }
  }

  fun addSubTasks(taskId: String, newSubtasks: List<SubTask>) {
    if (newSubtasks.isEmpty()) return
    _tasks.value = _tasks.value.map { task ->
      if (task.id == taskId) {
        val updatedSubtasks = task.subtasks + newSubtasks
        val doneCount = updatedSubtasks.count { it.isCompleted }
        val totalCount = updatedSubtasks.size
        val updated = task.copy(
          subtasks = updatedSubtasks,
          subtasksCompletedCount = doneCount,
          subtasksTotalCount = totalCount,
          subtaskBadge = "$doneCount/$totalCount sub-tasks"
        )
        scope.launch { try { db?.taskDao()?.updateTask(updated.toEntity()) } catch (_: Exception) {} }
        updated
      } else {
        task
      }
    }
  }

  fun updateKanbanStatus(taskId: String, newColumn: KanbanColumn) {
    _tasks.value = _tasks.value.map { task ->
      if (task.id == taskId) {
        val isNowDone = (newColumn == KanbanColumn.DONE)
        if (isNowDone && !task.isCompleted) {
          awardXp(120)
        }
        val updated = task.copy(
          kanbanStatus = newColumn,
          isCompleted = isNowDone,
          completedAt = if (isNowDone) "Today" else null
        )
        scope.launch { try { db?.taskDao()?.updateTask(updated.toEntity()) } catch (_: Exception) {} }
        updated
      } else {
        task
      }
    }
    updateProjectTaskCounts()
  }

  fun bookScheduleSlot(
    startTime: String,
    endTime: String,
    title: String,
    category: String,
    isDeepWork: Boolean = false
  ) {
    val cleanTitle = title.trim().ifEmpty { "Focus Session" }
    val newSlot = ScheduleCommitment(
      id = UUID.randomUUID().toString(),
      startTime = startTime,
      endTime = endTime,
      title = cleanTitle,
      subtitle = if (isDeepWork) "Focus Sprint • Deep Flow" else "Scheduled task",
      category = category,
      isDeepWork = isDeepWork,
      isCurrentFocus = false
    )
    _schedule.value = (_schedule.value + newSlot).sortedBy { it.startTime }
    scope.launch { try { db?.scheduleDao()?.insertSchedule(ScheduleItemToEntity(newSlot)) } catch (_: Exception) {} }
  }

  fun startFocusSession(
    taskTitle: String,
    minutes: Int,
    soundscape: String,
    targetSubtask: String? = null
  ) {
    val task = _tasks.value.find { it.title.equals(taskTitle, ignoreCase = true) }
    val firstIncomplete = task?.subtasks?.firstOrNull { !it.isCompleted }?.title
    val target = targetSubtask ?: firstIncomplete ?: "Sprint objective for $taskTitle"

    _activeFocusSession.value = FocusSessionState(
      taskTitle = taskTitle,
      targetSubtask = target,
      totalSeconds = minutes * 60,
      remainingSeconds = minutes * 60,
      isRunning = true,
      soundscape = soundscape
    )
  }

  fun tickFocusSecond(): Boolean {
    val current = _activeFocusSession.value ?: return false
    if (!current.isRunning || current.remainingSeconds <= 0) return false

    val nextRemaining = current.remainingSeconds - 1
    return if (nextRemaining <= 0) {
      _activeFocusSession.value = current.copy(remainingSeconds = 0, isRunning = false)
      completeCurrentSprint()
      true
    } else {
      _activeFocusSession.value = current.copy(remainingSeconds = nextRemaining)
      false
    }
  }

  fun toggleFocusTimerRunning() {
    _activeFocusSession.value = _activeFocusSession.value?.let { current ->
      current.copy(isRunning = !current.isRunning)
    }
  }

  fun addFiveMinutesToFocus() {
    _activeFocusSession.value = _activeFocusSession.value?.let { current ->
      current.copy(
        totalSeconds = current.totalSeconds + 300,
        remainingSeconds = current.remainingSeconds + 300
      )
    }
  }

  fun completeCurrentSprint() {
    val session = _activeFocusSession.value
    awardXp(120)
    if (session != null) {
      val loggedSecs = session.totalSeconds - session.remainingSeconds
      val addedHours = (if (loggedSecs > 0) loggedSecs else session.totalSeconds) / 3600.0
      val currentHours = _userProfile.value.focusHoursLogged.removeSuffix("h").toDoubleOrNull() ?: 18.5
      val updatedHours = String.format(java.util.Locale.US, "%.1fh", currentHours + addedHours)
      _userProfile.value = _userProfile.value.copy(focusHoursLogged = updatedHours)
    }
    _activeFocusSession.value = _activeFocusSession.value?.let {
      it.copy(remainingSeconds = 0, isRunning = false)
    }
  }

  fun recordSprintReflection(energy: String, note: String) {
    val session = _activeFocusSession.value ?: return
    val reflection = com.example.data.model.SprintReflection(
      taskTitle = session.taskTitle,
      durationMinutes = session.totalSeconds / 60,
      energyLevel = energy,
      note = note.trim()
    )
    _sprintReflections.value = _sprintReflections.value + reflection
  }

  fun awardXp(amount: Int) {
    val profile = _userProfile.value
    var newCurrentXp = profile.currentXp + amount
    val newTotalXp = profile.totalXpAllTime + amount
    val tasksDone = profile.tasksDoneCount + 1

    var currentLevel = profile.level
    var currentTargetXp = profile.targetXp
    var leveledUp = false

    while (newCurrentXp >= currentTargetXp) {
      newCurrentXp -= currentTargetXp
      currentLevel += 1
      currentTargetXp += 500
      leveledUp = true
    }

    val tier = MilestoneTierLevel.fromXp(newTotalXp)
    val newLevelTitle = tier.title

    _userProfile.value = profile.copy(
      currentXp = newCurrentXp,
      totalXpAllTime = newTotalXp,
      level = currentLevel,
      levelTitle = newLevelTitle,
      targetXp = currentTargetXp,
      tasksDoneCount = tasksDone,
      isVerified = profile.isVerified || currentLevel >= 5
    )
    if (leveledUp) {
      _showLevelUpModal.value = true
    }
    scope.launch { try { db?.userProfileDao()?.insertOrUpdateProfile(_userProfile.value.toEntity()) } catch (_: Exception) {} }
  }

  fun updateUserProfile(
    name: String? = null,
    email: String? = null,
    program: String? = null
  ) {
    _userProfile.value = _userProfile.value.copy(
      name = if (!name.isNullOrBlank()) name else _userProfile.value.name,
      email = if (!email.isNullOrBlank()) email else _userProfile.value.email,
      program = if (!program.isNullOrBlank()) program else _userProfile.value.program
    )
    scope.launch { try { db?.userProfileDao()?.insertOrUpdateProfile(_userProfile.value.toEntity()) } catch (_: Exception) {} }
  }

  private fun updateProjectTaskCounts() {
    val currentTasks = _tasks.value
    _projects.value = _projects.value.map { proj ->
      val projectTasks = currentTasks.filter { it.project.equals(proj.title, ignoreCase = true) }
      if (projectTasks.isNotEmpty()) {
        val updated = proj.copy(
          totalTasks = projectTasks.size,
          completedTasks = projectTasks.count { it.isCompleted }
        )
        scope.launch { try { db?.projectDao()?.updateProject(updated.toEntity()) } catch (_: Exception) {} }
        updated
      } else {
        proj
      }
    }
  }

  fun toggleHapticFeedback() {
    _userProfile.value = _userProfile.value.copy(
      hapticFeedback = !_userProfile.value.hapticFeedback
    )
    scope.launch { try { db?.userProfileDao()?.insertOrUpdateProfile(_userProfile.value.toEntity()) } catch (_: Exception) {} }
  }

  fun toggleCompletionSounds() {
    _userProfile.value = _userProfile.value.copy(
      completionSounds = !_userProfile.value.completionSounds
    )
    scope.launch { try { db?.userProfileDao()?.insertOrUpdateProfile(_userProfile.value.toEntity()) } catch (_: Exception) {} }
  }

  fun toggleCalendarSync() {
    _userProfile.value = _userProfile.value.copy(
      campusSyncEnabled = !_userProfile.value.campusSyncEnabled
    )
  }

  fun toggleMorningBriefing() {
    _userProfile.value = _userProfile.value.copy(
      morningBriefingEnabled = !_userProfile.value.morningBriefingEnabled
    )
  }

  fun toggleAutoFocusMode() {
    _userProfile.value = _userProfile.value.copy(
      autoFocusMode = !_userProfile.value.autoFocusMode
    )
  }

  private fun getInitialTasks(): List<TaskItem> {
    return listOf(
      TaskItem(
        id = "task_contrast_matrix",
        title = "Draft dark mode contrast matrix",
        description = "Verify WCAG AAA ratio thresholds (min 7.0:1 for normal text) across surface containers and elevated drawer modals.",
        category = "Work",
        project = "Design System Migration",
        priority = TaskPriority.HIGH,
        dueTime = "10:30 AM",
        dueDate = "Today (Oct 24)",
        isCompleted = false,
        kanbanStatus = KanbanColumn.IN_PROGRESS,
        estimatedEffortMinutes = 30,
        subtasksCompletedCount = 2,
        subtasksTotalCount = 4,
        subtasks = listOf(
          SubTask("st1", "Audit existing WCAG 2.1 AA/AAA ratios on tokens", isCompleted = true, effortMinutes = 10),
          SubTask("st2", "Collect surface elevation luminance values", isCompleted = true, effortMinutes = 5),
          SubTask("st3", "Define semantic token palette for inverted surfaces", isCompleted = false, effortMinutes = 10),
          SubTask("st4", "Export Figma token matrix JSON & test on preview", isCompleted = false, effortMinutes = 5)
        )
      ),
      TaskItem(
        id = "task_db_assignment",
        title = "Finish database assignment",
        description = "Include entity relationship diagram and primary key definitions for User and Workspace tables.",
        category = "College",
        project = "Database Design Final",
        priority = TaskPriority.HIGH,
        dueTime = "8:00 PM",
        dueDate = "Today (Oct 24)",
        urgencyBadge = "Due in 4h",
        isCompleted = false,
        kanbanStatus = KanbanColumn.TO_DO,
        estimatedEffortMinutes = 45
      ),
      TaskItem(
        id = "task_client_rev",
        title = "Client website revision notes",
        description = "Review wireframes with fintech design team and incorporate client feedback.",
        category = "Work",
        project = "Fintech Client Brand & Web",
        priority = TaskPriority.MED,
        dueTime = "10:30 AM",
        dueDate = "Today (Oct 24)",
        attachmentCount = 3,
        isCompleted = false,
        kanbanStatus = KanbanColumn.IN_PROGRESS,
        estimatedEffortMinutes = 30
      ),
      TaskItem(
        id = "task_hci_slides",
        title = "Review Human-Computer Interaction slides",
        description = "Go over cognitive load, Fitts's law, and accessible UI touch target standards.",
        category = "College",
        project = "CS404 Milestone",
        priority = TaskPriority.MED,
        dueTime = "2:00 PM",
        dueDate = "Today (Oct 24)",
        subtaskBadge = "1/3 sub-tasks",
        isCompleted = false,
        kanbanStatus = KanbanColumn.TO_DO,
        estimatedEffortMinutes = 40
      ),
      TaskItem(
        id = "task_econ_prep",
        title = "Prepare economics presentation",
        description = "Draft slide deck on interest rate impacts on venture capital liquidity.",
        category = "College",
        project = "Macroeconomics Research Paper",
        priority = TaskPriority.LOW,
        dueTime = "Tomorrow, 9:00 AM",
        dueDate = "Tomorrow",
        isTomorrow = true,
        isCompleted = false,
        kanbanStatus = KanbanColumn.TO_DO,
        estimatedEffortMinutes = 60
      ),
      TaskItem(
        id = "task_team_sync",
        title = "Team sync for final project",
        description = "Weekly sprint coordination, feature review, and milestone planning.",
        category = "Work",
        project = "Design System Migration",
        priority = TaskPriority.LOW,
        dueTime = "4:30 PM",
        dueDate = "Today (Oct 24)",
        isCompleted = false,
        kanbanStatus = KanbanColumn.TO_DO,
        estimatedEffortMinutes = 30
      ),
      TaskItem(
        id = "task_freelance_invoice",
        title = "Send freelance invoice",
        description = "Issue invoice for October design sprint #2 milestone completion.",
        category = "Work",
        project = "Fintech Client Brand & Web",
        priority = TaskPriority.LOW,
        dueTime = "5:30 PM",
        dueDate = "Today (Oct 24)",
        isCompleted = false,
        kanbanStatus = KanbanColumn.TO_DO,
        estimatedEffortMinutes = 15
      ),
      TaskItem(
        id = "task_db_mig_planning",
        title = "Database Migration Planning",
        description = "Sync with backend engineering on SQLite sharding & local cache replication.",
        category = "Work",
        project = "Design System Migration",
        priority = TaskPriority.LOW,
        dueTime = "3:30 PM",
        dueDate = "Today (Oct 24)",
        isCompleted = false,
        kanbanStatus = KanbanColumn.TO_DO,
        estimatedEffortMinutes = 45
      ),
      TaskItem(
        id = "task_design_system_tokens",
        title = "Design System Token Audit",
        description = "Audit token lightness across low-spec AMOLED panels.",
        category = "Work",
        project = "Design System Migration",
        priority = TaskPriority.LOW,
        dueTime = "11:00 AM",
        dueDate = "Today (Oct 24)",
        isCompleted = false,
        kanbanStatus = KanbanColumn.TO_DO,
        estimatedEffortMinutes = 25
      ),
      TaskItem(
        id = "task_printer_ink",
        title = "Buy printer ink & notebook",
        description = "Get refill cartridges and dot-grid notebook from campus stationery store.",
        category = "Personal",
        project = "Apartment & Moving Checklist",
        priority = TaskPriority.LOW,
        dueTime = "6:15 PM",
        dueDate = "Today (Oct 24)",
        isCompleted = false,
        kanbanStatus = KanbanColumn.TO_DO,
        estimatedEffortMinutes = 20
      )
    )
  }

  private fun getInitialProjects(): List<ProjectItem> {
    return listOf(
      ProjectItem(
        id = "proj_1",
        title = "Design System Migration",
        description = "Transitioning core component tokens, dark mode matrix, and AMOLED contrast ratios for v2.4 release.",
        category = "Work",
        code = "Work Studio",
        dueDate = "Due Nov 4 (11d left)",
        totalTasks = 18,
        completedTasks = 12,
        activeSprint = "Sprint #2 Active",
        nextTaskPreview = "Draft dark mode contrast matrix",
        nextTaskDue = "Today 17:30",
        tags = listOf("#token/dark-matrix", "#arch/elevation", "#wcag/contrast", "#spec/amoled")
      ),
      ProjectItem(
        id = "proj_2",
        title = "Database Design Final",
        description = "Relational schema design, normalization, stored procedures, and benchmark testing.",
        category = "College",
        code = "College · CS340",
        dueDate = "Due Tonight (8:00 PM)",
        totalTasks = 5,
        completedTasks = 3,
        activeSprint = "Sprint #1",
        nextTaskPreview = "Finish ER diagram submission",
        nextTaskDue = "Today, 8:00 PM"
      ),
      ProjectItem(
        id = "proj_3",
        title = "Fintech Client Brand & Web",
        description = "Client design system, wireframe prototypes, and mobile banking web experience.",
        category = "Work",
        code = "Work · Freelance",
        dueDate = "Due Oct 31",
        totalTasks = 8,
        completedTasks = 5,
        activeSprint = "Sprint #2",
        nextTaskPreview = "Client revision meeting",
        nextTaskDue = "Today, 10:30 AM"
      ),
      ProjectItem(
        id = "proj_4",
        title = "Macroeconomics Research Paper",
        description = "Empirical study on post-crisis monetary policy and venture capital market dynamics.",
        category = "College",
        code = "College · ECON201",
        dueDate = "Due Friday",
        totalTasks = 4,
        completedTasks = 1,
        activeSprint = "Draft Phase",
        nextTaskPreview = "Draft literature review",
        nextTaskDue = "Friday"
      ),
      ProjectItem(
        id = "proj_5",
        title = "Apartment & Moving Checklist",
        description = "Organize supplies, moving van booking, utility transfers, and lease signing.",
        category = "Personal",
        code = "Personal",
        dueDate = "Weekend",
        totalTasks = 6,
        completedTasks = 4,
        activeSprint = "Logistics",
        nextTaskPreview = "Buy packing boxes & supplies",
        nextTaskDue = "Weekend"
      )
    )
  }

  private fun getInitialSchedule(): List<ScheduleCommitment> {
    return listOf(
      ScheduleCommitment(
        id = "sch_1",
        startTime = "08:00",
        endTime = "08:45",
        title = "Morning Planning & Notion Sync",
        subtitle = "Morning Routine • All tasks reviewed",
        category = "Personal",
        isCompleted = true
      ),
      ScheduleCommitment(
        id = "sch_2",
        startTime = "08:30",
        endTime = "10:00",
        title = "Database Architecture Lecture",
        subtitle = "Room 302 • Prof. Vance",
        category = "College",
        location = "Room 302",
        isCompleted = true
      ),
      ScheduleCommitment(
        id = "sch_3",
        startTime = "10:30",
        endTime = "11:30",
        title = "Client revision & wireframe review",
        subtitle = "Zoom • Design handoff milestone",
        category = "Work",
        location = "Google Meet",
        isCompleted = true
      ),
      ScheduleCommitment(
        id = "sch_4",
        startTime = "11:00",
        endTime = "12:30",
        title = "UI Architecture Guidelines Review",
        subtitle = "Core team design review",
        category = "Work Studio",
        isDeepWork = true
      ),
      ScheduleCommitment(
        id = "sch_5",
        startTime = "13:00",
        endTime = "14:00",
        title = "Lunch Break & Walk",
        subtitle = "North quad transit",
        category = "Personal"
      ),
      ScheduleCommitment(
        id = "sch_6",
        startTime = "14:00",
        endTime = "15:30",
        title = "Weekly Product Sync & Critique",
        subtitle = "Core Design Team • 4 action items filed",
        category = "Product Team",
        isCompleted = true
      ),
      ScheduleCommitment(
        id = "sch_7",
        startTime = "16:00",
        endTime = "16:30",
        title = "Draft dark mode contrast matrix",
        subtitle = "Audit token lightness across low-spec AMOLED panels",
        category = "Work Studio",
        isCurrentFocus = true,
        isDeepWork = true
      ),
      ScheduleCommitment(
        id = "sch_8",
        startTime = "16:30",
        endTime = "17:30",
        title = "Database Migration Planning",
        subtitle = "Sync with backend engineering on SQLite sharding",
        category = "Engineering",
        isDeepWork = true
      ),
      ScheduleCommitment(
        id = "sch_9",
        startTime = "17:45",
        endTime = "18:00",
        title = "Daily Journal & Tomorrow's Three Rocks",
        subtitle = "Daily close reflection",
        category = "Personal"
      )
    )
  }
}
