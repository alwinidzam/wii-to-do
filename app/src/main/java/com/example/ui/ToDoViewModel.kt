package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.FocusSessionState
import com.example.data.model.KanbanColumn
import com.example.data.model.ProjectItem
import com.example.data.model.ScheduleCommitment
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.example.data.model.UserProfile
import com.example.data.repository.ToDoRepository
import com.example.data.model.ShareCardConfig
import com.example.ui.components.getTodayIndex
import com.example.data.ai.GeminiTaskBreakdownService
import com.example.data.model.SubTask
import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AppDestination {
  data object Home : AppDestination
  data object Schedule : AppDestination
  data object Projects : AppDestination
  data object Profile : AppDestination
  data class TaskDetail(val taskId: String) : AppDestination
  data object CreateTask : AppDestination
  data class AddSubTask(val taskId: String) : AppDestination
  data class ScheduleBooking(val startTime: String = "16:00", val endTime: String = "16:30") : AppDestination
  data class FocusSetup(val taskId: String? = null) : AppDestination
  data object ActiveFocus : AppDestination
  data object FocusSummary : AppDestination
  data object MilestoneJourney : AppDestination
  data object ShareStudio : AppDestination
  data object Onboarding : AppDestination
  data object Login : AppDestination
  data object SignUp : AppDestination
}

class ToDoViewModel(
  private val repository: ToDoRepository = ToDoRepository.getInstance()
) : ViewModel() {

  val tasks: StateFlow<List<TaskItem>> = repository.tasks
  val projects: StateFlow<List<ProjectItem>> = repository.projects
  val schedule: StateFlow<List<ScheduleCommitment>> = repository.schedule
  val userProfile: StateFlow<UserProfile> = repository.userProfile
  val activeFocusSession: StateFlow<FocusSessionState?> = repository.activeFocusSession
  val lastCompletedCelebration: StateFlow<TaskItem?> = repository.lastCompletedTaskCelebration
  val showLevelUpModal: StateFlow<Boolean> = repository.showLevelUpModal
  val shareCardConfig: StateFlow<ShareCardConfig> = repository.shareCardConfig
  val academicCourses: StateFlow<List<com.example.data.model.AcademicCourse>> = repository.academicCourses

  fun addAcademicCourse(course: com.example.data.model.AcademicCourse) {
    repository.addAcademicCourse(course)
  }

  private val _currentDestination = MutableStateFlow<AppDestination>(
    if (repository.isUserLoggedIn()) AppDestination.Home else AppDestination.Onboarding
  )
  val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

  fun setLoggedIn(loggedIn: Boolean) {
    repository.setUserLoggedIn(loggedIn)
  }

  fun loginAsDeveloper() {
    _navigationStack.clear()
    repository.loginAsDeveloper()
    _currentDestination.value = AppDestination.Home
  }

  fun loginAsFreshUser(
    name: String = "Alwi",
    email: String = "user@wiitodo.app",
    program: String = "Personal Workspace"
  ) {
    _navigationStack.clear()
    repository.loginAsFreshUser(name, email, program)
    _currentDestination.value = AppDestination.Home
  }

  fun signOut() {
    _navigationStack.clear()
    repository.signOutUser()
    _currentDestination.value = AppDestination.Onboarding
  }

  suspend fun authenticate(email: String, password: String): com.example.data.auth.AuthResult {
    val result = repository.authenticateUser(email, password)
    if (result is com.example.data.auth.AuthResult.Success) {
      _navigationStack.clear()
      _currentDestination.value = AppDestination.Home
    }
    return result
  }

  suspend fun register(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    program: String
  ): com.example.data.auth.AuthResult {
    val result = repository.registerUser(name, email, password, confirmPassword, program)
    if (result is com.example.data.auth.AuthResult.Success) {
      _navigationStack.clear()
      _currentDestination.value = AppDestination.Home
    }
    return result
  }

  private val _currentLanguage = MutableStateFlow<AppLanguage>(
    if (repository.getAppLanguage() == "en") AppLanguage.EN else AppLanguage.ID
  )
  val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

  fun setLanguage(lang: AppLanguage) {
    _currentLanguage.value = lang
    repository.setAppLanguage(lang.code)
  }

  private val _homeCategoryFilter = MutableStateFlow("All")
  val homeCategoryFilter: StateFlow<String> = _homeCategoryFilter.asStateFlow()

  private val _selectedDayIndex = MutableStateFlow(getTodayIndex())
  val selectedDayIndex: StateFlow<Int> = _selectedDayIndex.asStateFlow()

  private val _scheduleViewMode = MutableStateFlow("Day") // Day vs Week
  val scheduleViewMode: StateFlow<String> = _scheduleViewMode.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _isGeneratingAiSubtasks = MutableStateFlow(false)
  val isGeneratingAiSubtasks: StateFlow<Boolean> = _isGeneratingAiSubtasks.asStateFlow()

  private var timerJob: Job? = null

  private val _navigationStack = mutableListOf<AppDestination>()

  val canNavigateBack: Boolean
    get() {
      val curr = _currentDestination.value
      if (_navigationStack.isNotEmpty()) return true
      return curr !is AppDestination.Home && curr !is AppDestination.Onboarding
    }

  init {
    startTimerTicker()
  }

  fun switchToTab(destination: AppDestination) {
    if (_currentDestination.value == destination) return
    _currentDestination.value = destination
  }

  fun navigateTo(destination: AppDestination) {
    if (_currentDestination.value == destination) return
    _navigationStack.add(_currentDestination.value)
    _currentDestination.value = destination
  }

  fun navigateBack(): Boolean {
    if (_navigationStack.isNotEmpty()) {
      val prev = _navigationStack.removeAt(_navigationStack.size - 1)
      _currentDestination.value = prev
      return true
    }
    if (_currentDestination.value is AppDestination.Schedule ||
        _currentDestination.value is AppDestination.Projects ||
        _currentDestination.value is AppDestination.Profile) {
      _currentDestination.value = AppDestination.Home
      return true
    }
    if (_currentDestination.value is AppDestination.Login ||
        _currentDestination.value is AppDestination.SignUp) {
      _currentDestination.value = AppDestination.Onboarding
      return true
    }
    return false
  }

  fun updateShareCardConfig(config: ShareCardConfig) {
    repository.updateShareCardConfig(config)
  }

  fun setHomeCategoryFilter(category: String) {
    _homeCategoryFilter.value = category
  }

  fun setSelectedDay(index: Int) {
    _selectedDayIndex.value = index
  }

  fun setScheduleViewMode(mode: String) {
    _scheduleViewMode.value = mode
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun toggleTaskCompletion(taskId: String) {
    repository.toggleTaskCompletion(taskId)
  }

  fun deleteTask(taskId: String) {
    repository.deleteTask(taskId)
  }

  fun toggleSubTask(taskId: String, subtaskId: String) {
    repository.toggleSubTask(taskId, subtaskId)
  }

  fun addSubTask(taskId: String, title: String, effortMinutes: Int = 30) {
    repository.addSubTask(taskId, title, effortMinutes)
  }

  fun generateAiSubtasksForTask(taskId: String) {
    val targetTask = tasks.value.find { it.id == taskId } ?: return
    viewModelScope.launch {
      _isGeneratingAiSubtasks.value = true
      try {
        val generated = GeminiTaskBreakdownService.breakDownTask(targetTask.title)
        if (generated.isNotEmpty()) {
          repository.addSubTasks(taskId, generated)
        }
      } catch (_: Exception) {
      } finally {
        _isGeneratingAiSubtasks.value = false
      }
    }
  }

  fun generateAiSubtasksForTitle(title: String, onResult: (List<SubTask>) -> Unit) {
    if (title.isBlank()) return
    viewModelScope.launch {
      _isGeneratingAiSubtasks.value = true
      try {
        val generated = GeminiTaskBreakdownService.breakDownTask(title)
        onResult(generated)
      } catch (_: Exception) {
        onResult(emptyList())
      } finally {
        _isGeneratingAiSubtasks.value = false
      }
    }
  }

  fun addTask(
    title: String,
    description: String,
    category: String,
    project: String,
    priority: TaskPriority,
    dueTime: String,
    dueDate: String,
    courseName: String? = null
  ): String {
    return repository.addTask(
      title = title,
      description = description,
      category = category,
      project = project,
      priority = priority,
      dueTime = dueTime,
      dueDate = dueDate,
      courseName = courseName
    )
  }

  fun updateKanbanStatus(taskId: String, status: KanbanColumn) {
    repository.updateKanbanStatus(taskId, status)
  }

  fun bookScheduleSlot(
    startTime: String,
    endTime: String,
    title: String,
    category: String,
    isDeepWork: Boolean = false
  ) {
    repository.bookScheduleSlot(startTime, endTime, title, category, isDeepWork)
  }

  fun startFocusSession(taskTitle: String, minutes: Int, soundscape: String = "") {
    repository.startFocusSession(taskTitle, minutes, soundscape)
    navigateTo(AppDestination.ActiveFocus)
  }

  fun toggleFocusTimerRunning() {
    repository.toggleFocusTimerRunning()
  }

  fun addFiveMinutesToFocus() {
    repository.addFiveMinutesToFocus()
  }

  fun completeCurrentSprint() {
    repository.completeCurrentSprint()
    navigateTo(AppDestination.FocusSummary)
  }

  fun recordSprintReflection(energy: String, note: String) {
    repository.recordSprintReflection(energy, note)
  }

  fun updateProfile(name: String, email: String, program: String) {
    repository.updateUserProfile(name, email, program)
  }

  fun dismissCelebration() {
    repository.dismissCelebration()
  }

  fun dismissLevelUpModal() {
    repository.dismissLevelUpModal()
  }

  fun triggerLevelUpModal() {
    repository.triggerLevelUpCelebration()
  }

  fun toggleHapticFeedback() = repository.toggleHapticFeedback()
  fun toggleCalendarSync() = repository.toggleCalendarSync()
  fun toggleMorningBriefing() = repository.toggleMorningBriefing()
  fun toggleAutoFocusMode() = repository.toggleAutoFocusMode()

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
  }

  private fun startTimerTicker() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (true) {
        delay(1000)
        val finished = repository.tickFocusSecond()
        if (finished && _currentDestination.value is AppDestination.ActiveFocus) {
          _currentDestination.value = AppDestination.FocusSummary
        }
      }
    }
  }
}
