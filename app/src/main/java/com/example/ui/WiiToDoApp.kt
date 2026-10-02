package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BottomDockNavigation
import com.example.ui.components.NavigationTab
import com.example.ui.dialogs.LevelUpDialog
import com.example.ui.screens.ActiveFocusScreen
import com.example.ui.screens.CreateTaskScreen
import com.example.ui.screens.FocusSummaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MilestoneJourneyScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.SignUpScreen
import com.example.ui.screens.SocialShareStudioScreen
import com.example.ui.screens.TaskDetailScreen
import com.example.ui.sheets.AddSubTaskSheet
import com.example.ui.sheets.AddTaskBottomSheet
import com.example.ui.sheets.ScheduleBookingSheet
import com.example.ui.theme.OliveSecondary
import com.example.ui.theme.OliveSecondaryContainer
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WiiToDoApp(
  viewModel: ToDoViewModel = viewModel()
) {
  BackHandler(enabled = viewModel.canNavigateBack) {
    viewModel.navigateBack()
  }

  val destination by viewModel.currentDestination.collectAsState()
  val tasks by viewModel.tasks.collectAsState()
  val projects by viewModel.projects.collectAsState()
  val schedule by viewModel.schedule.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val activeFocus by viewModel.activeFocusSession.collectAsState()
  val shareCardConfig by viewModel.shareCardConfig.collectAsState()

  val selectedDayIndex by viewModel.selectedDayIndex.collectAsState()
  val categoryFilter by viewModel.homeCategoryFilter.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val scheduleViewMode by viewModel.scheduleViewMode.collectAsState()

  val celebrationTask by viewModel.lastCompletedCelebration.collectAsState()
  val showLevelUpModal by viewModel.showLevelUpModal.collectAsState()
  val isAiGenerating by viewModel.isGeneratingAiSubtasks.collectAsState()

  var selectedDetailTaskId by remember { mutableStateOf("task_contrast_matrix") }
  var subTaskSheetParentId by remember { mutableStateOf<String?>(null) }
  var scheduleBookingSlot by remember { mutableStateOf<Pair<String, String>?>(null) }
  var showAddTaskBottomSheet by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  // Auto-dismiss celebration pill after 3.5s
  LaunchedEffect(celebrationTask) {
    if (celebrationTask != null) {
      delay(3500)
      viewModel.dismissCelebration()
    }
  }

  val currentLanguage by viewModel.currentLanguage.collectAsState()

  val isPrimaryTab = destination is AppDestination.Home ||
    destination is AppDestination.Schedule ||
    destination is AppDestination.Projects ||
    destination is AppDestination.Profile

  val currentNavTab = when (destination) {
    AppDestination.Home -> NavigationTab.HOME
    AppDestination.Schedule -> NavigationTab.SCHEDULE
    AppDestination.Projects -> NavigationTab.PROJECTS
    AppDestination.Profile -> NavigationTab.PROFILE
    else -> NavigationTab.HOME
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      snackbarHost = { SnackbarHost(snackbarHostState) },
      containerColor = com.example.ui.theme.PrimaryBackground,
      bottomBar = {
        if (isPrimaryTab) {
          BottomDockNavigation(
            currentTab = currentNavTab,
            onTabSelected = { tab ->
              when (tab) {
                NavigationTab.HOME -> viewModel.navigateTo(AppDestination.Home)
                NavigationTab.SCHEDULE -> viewModel.navigateTo(AppDestination.Schedule)
                NavigationTab.PROJECTS -> viewModel.navigateTo(AppDestination.Projects)
                NavigationTab.PROFILE -> viewModel.navigateTo(AppDestination.Profile)
              }
            },
            onAddClick = { showAddTaskBottomSheet = true },
            currentLanguage = currentLanguage
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        AnimatedContent(
          targetState = destination,
          transitionSpec = {
            (fadeIn(animationSpec = tween(220, delayMillis = 40)) +
              slideInHorizontally(animationSpec = tween(260, easing = FastOutSlowInEasing)) { fullWidth -> (fullWidth * 0.12f).toInt() })
              .togetherWith(
                fadeOut(animationSpec = tween(180)) +
                  slideOutHorizontally(animationSpec = tween(220, easing = FastOutSlowInEasing)) { fullWidth -> -(fullWidth * 0.12f).toInt() }
              )
          },
          label = "screen_navigation_transition"
        ) { currentDest ->
          when (currentDest) {
          AppDestination.Home -> {
            HomeScreen(
              tasks = tasks,
              selectedDayIndex = selectedDayIndex,
              selectedCategory = categoryFilter,
              searchQuery = searchQuery,
              onDaySelected = { viewModel.setSelectedDay(it) },
              onCategorySelected = { viewModel.setHomeCategoryFilter(it) },
              onSearchQueryChanged = { viewModel.setSearchQuery(it) },
              onToggleTaskComplete = { viewModel.toggleTaskCompletion(it) },
              onTaskClick = { id ->
                selectedDetailTaskId = id
                viewModel.navigateTo(AppDestination.TaskDetail(id))
              },
              onProfileClick = { viewModel.navigateTo(AppDestination.Profile) },
              userName = userProfile.name.split(" ").firstOrNull() ?: "Alwi",
              onDeleteTask = { id -> viewModel.deleteTask(id) },
              userProfile = userProfile,
              onMilestoneClick = { viewModel.navigateTo(AppDestination.MilestoneJourney) },
              currentLanguage = currentLanguage,
              onLanguageSelected = { viewModel.setLanguage(it) }
            )
          }

          AppDestination.Schedule -> {
            ScheduleScreen(
              scheduleItems = schedule,
              viewMode = scheduleViewMode,
              selectedDayIndex = selectedDayIndex,
              activeFocusSession = activeFocus,
              onViewModeChanged = { viewModel.setScheduleViewMode(it) },
              onDaySelected = { viewModel.setSelectedDay(it) },
              onScheduleSlotClick = { start, end ->
                scheduleBookingSlot = Pair(start, end)
              },
              onFocusMiniPlayerClick = { viewModel.navigateTo(AppDestination.ActiveFocus) },
              onToggleTimer = { viewModel.toggleFocusTimerRunning() },
              onCompleteSprint = { viewModel.completeCurrentSprint() }
            )
          }

          AppDestination.Projects -> {
            ProjectsScreen(
              projects = projects,
              tasks = tasks,
              onTaskClick = { id ->
                selectedDetailTaskId = id
                viewModel.navigateTo(AppDestination.TaskDetail(id))
              },
              onNewTaskClick = { viewModel.navigateTo(AppDestination.CreateTask) },
              onUpdateKanbanStatus = { id, col -> viewModel.updateKanbanStatus(id, col) }
            )
          }

          AppDestination.Profile -> {
            ProfileScreen(
              profile = userProfile,
              onToggleHaptics = { viewModel.toggleHapticFeedback() },
              onToggleCalendarSync = { viewModel.toggleCalendarSync() },
              onToggleMorningBriefing = { viewModel.toggleMorningBriefing() },
              onToggleAutoFocus = { viewModel.toggleAutoFocusMode() },
              onViewLevelCelebration = { viewModel.triggerLevelUpModal() },
              onSignOut = { viewModel.signOut() },
              onOpenMilestoneJourney = { viewModel.navigateTo(AppDestination.MilestoneJourney) },
              currentLanguage = currentLanguage,
              onLanguageSelected = { viewModel.setLanguage(it) }
            )
          }

          is AppDestination.TaskDetail -> {
            val targetId = currentDest.taskId.ifEmpty { selectedDetailTaskId }
            val currentTask = tasks.find { it.id == targetId }
              ?: tasks.find { it.id == selectedDetailTaskId }
              ?: tasks.firstOrNull()

            if (currentTask != null) {
              TaskDetailScreen(
                task = currentTask,
                onBackClick = { viewModel.navigateBack() },
                onToggleTaskComplete = { viewModel.toggleTaskCompletion(currentTask.id) },
                onToggleSubTask = { subId -> viewModel.toggleSubTask(currentTask.id, subId) },
                onAddSubTaskClick = { subTaskSheetParentId = currentTask.id },
                onAiBreakdownClick = { viewModel.generateAiSubtasksForTask(currentTask.id) },
                isAiGenerating = isAiGenerating,
                onStartFocusClick = {
                  val effort = if (currentTask.estimatedEffortMinutes > 0) currentTask.estimatedEffortMinutes else 15
                  viewModel.startFocusSession(currentTask.title, effort)
                },
                onRescheduleClick = { scheduleBookingSlot = Pair("16:00", "16:30") }
              )
            } else {
              Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "Task not found", color = com.example.ui.theme.BrandSecondary)
                  Spacer(modifier = Modifier.height(12.dp))
                  androidx.compose.material3.Button(onClick = { viewModel.navigateBack() }) {
                    Text("Go Back")
                  }
                }
              }
            }
          }

          AppDestination.CreateTask -> {
            CreateTaskScreen(
              onClose = { viewModel.navigateBack() },
              onTaskCreated = { title, desc, cat, proj, prio, dueTime, dueDate, withAiBreakdown ->
                val newTaskId = viewModel.addTask(title, desc, cat, proj, prio, dueTime, dueDate)
                if (withAiBreakdown) {
                  viewModel.generateAiSubtasksForTask(newTaskId)
                }
                coroutineScope.launch {
                  val message = if (withAiBreakdown) "Task added with AI subtasks to $proj" else "Task added to $proj"
                  snackbarHostState.showSnackbar(message)
                }
                viewModel.navigateBack()
              }
            )
          }

          AppDestination.ActiveFocus -> {
            val session = activeFocus ?: com.example.data.model.FocusSessionState("Deep Work Focus", "Sprint")
            ActiveFocusScreen(
              session = session,
              onClose = { viewModel.navigateTo(AppDestination.Home) },
              onToggleTimer = { viewModel.toggleFocusTimerRunning() },
              onAddFiveMinutes = { viewModel.addFiveMinutesToFocus() },
              onCompleteSprint = { viewModel.completeCurrentSprint() }
            )
          }

          AppDestination.FocusSummary -> {
            val session = activeFocus
            FocusSummaryScreen(
              session = session,
              onReturnHome = { viewModel.navigateTo(AppDestination.Home) },
              onTakeBreak = {
                viewModel.startFocusSession("Break & Stretch", 5)
              },
              onContinueSprint = {
                val nextTitle = session?.taskTitle ?: "Deep Focus Sprint"
                viewModel.startFocusSession(nextTitle, 15)
              },
              onSaveReflection = { energy, note ->
                viewModel.recordSprintReflection(energy, note)
              },
              onShareMilestoneClick = {
                viewModel.navigateTo(AppDestination.ShareStudio)
              }
            )
          }

          AppDestination.MilestoneJourney -> {
            MilestoneJourneyScreen(
              userProfile = userProfile,
              onBackClick = { viewModel.navigateBack() },
              onOpenShareStudio = { viewModel.navigateTo(AppDestination.ShareStudio) }
            )
          }

          AppDestination.ShareStudio -> {
            SocialShareStudioScreen(
              userProfile = userProfile,
              initialConfig = shareCardConfig,
              onConfigChange = { viewModel.updateShareCardConfig(it) },
              onBackClick = { viewModel.navigateBack() },
              onShowSnackbar = { msg ->
                coroutineScope.launch {
                  snackbarHostState.showSnackbar(msg)
                }
              }
            )
          }

          AppDestination.Onboarding -> {
            OnboardingScreen(
              onGetStarted = {
                viewModel.navigateTo(AppDestination.SignUp)
              },
              onSignInClick = { viewModel.navigateTo(AppDestination.Login) },
              onGuestSignIn = {
                viewModel.loginAsFreshUser("Guest Scholar", "guest@wiitodo.app", "Offline Focus Workspace")
              },
              onDeveloperLogin = {
                viewModel.loginAsDeveloper()
              }
            )
          }

          AppDestination.Login -> {
            SignInScreen(
              onBackClick = { viewModel.navigateBack() },
              onSignInSuccess = {
                viewModel.loginAsFreshUser("Alwi Pratama", "alwi.student@university.edu", "Informatics Engineering • Year 3")
              },
              onSignUpClick = { viewModel.navigateTo(AppDestination.SignUp) },
              onGoogleSignIn = {
                viewModel.loginAsFreshUser("Alwi Pratama (Google)", "alwi.student@university.edu", "Informatics Engineering • Year 3")
              },
              onGuestSignIn = {
                viewModel.loginAsFreshUser("Guest Scholar", "guest@wiitodo.app", "Offline Focus Workspace")
              },
              onDeveloperLogin = {
                viewModel.loginAsDeveloper()
              }
            )
          }

          AppDestination.SignUp -> {
            SignUpScreen(
              onBackClick = { viewModel.navigateBack() },
              onSignUpSuccess = { name, email, program ->
                viewModel.loginAsFreshUser(name, email, program)
              },
              onGoogleSignIn = {
                viewModel.loginAsFreshUser("Alwi Pratama (Google)", "alwi.student@university.edu", "Informatics Engineering • Year 3")
              },
              onGuestSignIn = {
                viewModel.loginAsFreshUser("Guest Scholar", "guest@wiitodo.app", "Offline Focus Workspace")
              }
            )
          }

          is AppDestination.AddSubTask -> {
            subTaskSheetParentId = currentDest.taskId
            viewModel.navigateBack()
          }

          is AppDestination.ScheduleBooking -> {
            scheduleBookingSlot = Pair(currentDest.startTime, currentDest.endTime)
            viewModel.navigateBack()
          }

          is AppDestination.FocusSetup -> {
            val task = tasks.find { it.id == currentDest.taskId }
            val title = task?.title ?: "Deep Work Sprint"
            val effort = task?.estimatedEffortMinutes ?: 25
            viewModel.startFocusSession(title, effort)
          }
        }
      }
    }
    }

    // Modal Sheet 1: Add Subtask
    if (subTaskSheetParentId != null) {
      val parentTask = tasks.find { it.id == subTaskSheetParentId }
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.BottomCenter
      ) {
        AddSubTaskSheet(
          parentTaskTitle = parentTask?.title ?: "Parent Task",
          onClose = { subTaskSheetParentId = null },
          onAddSubTask = { title, mins ->
            subTaskSheetParentId?.let { pid ->
              viewModel.addSubTask(pid, title, mins)
            }
            subTaskSheetParentId = null
            coroutineScope.launch {
              snackbarHostState.showSnackbar("Sub-task added (+30 XP)")
            }
          }
        )
      }
    }

    // Modal Sheet 2: Schedule Slot Booking
    if (scheduleBookingSlot != null) {
      val (start, end) = scheduleBookingSlot!!
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.BottomCenter
      ) {
        ScheduleBookingSheet(
          startTime = start,
          endTime = end,
          onClose = { scheduleBookingSlot = null },
          onBookSlot = { title, category, isDeep ->
            viewModel.bookScheduleSlot(start, end, title, category, isDeep)
            scheduleBookingSlot = null
            coroutineScope.launch {
              snackbarHostState.showSnackbar("Booked $start – $end for $title")
            }
          }
        )
      }
    }

    // Modal Sheet 3: Add Task Bottom Sheet (triggered by FAB)
    if (showAddTaskBottomSheet) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.BottomCenter
      ) {
        AddTaskBottomSheet(
          onDismiss = { showAddTaskBottomSheet = false },
          onTaskCreated = { title, desc, cat, proj, prio, dueTime, dueDate ->
            viewModel.addTask(title, desc, cat, proj, prio, dueTime, dueDate)
            showAddTaskBottomSheet = false
            coroutineScope.launch {
              snackbarHostState.showSnackbar("Task added to $cat")
            }
          }
        )
      }
    }

    // Level 5 Milestone Celebration Dialog
    if (showLevelUpModal) {
      LevelUpDialog(
        onDismiss = { viewModel.dismissLevelUpModal() }
      )
    }

    // Apple Dynamic Island Capsule Notification
    AnimatedVisibility(
      visible = celebrationTask != null,
      enter = slideInVertically { -it } + fadeIn(),
      exit = slideOutVertically { -it } + fadeOut(),
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 54.dp, start = 20.dp, end = 20.dp)
    ) {
      Surface(
        modifier = Modifier
          .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x18000000), spotColor = Color(0x22000000)),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1C1C1E)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = com.example.ui.theme.AppleSystemGreen
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
            }
          }

          Column {
            Text(
              text = "Task Completed",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = celebrationTask?.title ?: "",
              fontSize = 11.sp,
              color = Color.White.copy(alpha = 0.75f),
              maxLines = 1
            )
          }
        }
      }
    }
  }
}
