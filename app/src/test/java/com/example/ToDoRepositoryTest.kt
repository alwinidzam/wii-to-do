package com.example

import com.example.data.model.KanbanColumn
import com.example.data.model.TaskPriority
import com.example.data.repository.ToDoRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToDoRepositoryTest {

  private lateinit var repository: ToDoRepository

  @Before
  fun setUp() {
    repository = ToDoRepository()
  }

  @Test
  fun testFocusTimerDecrementsOneSecondAccurately() {
    // Start a 25-minute sprint = 1500 seconds
    repository.startFocusSession("Design System Audit", 25, "Brown Noise Calm")
    val initialSession = repository.activeFocusSession.value
    assertNotNull(initialSession)
    assertEquals(1500, initialSession?.totalSeconds)
    assertEquals(1500, initialSession?.remainingSeconds)

    // Tick exactly 1 second
    val finished = repository.tickFocusSecond()
    assertFalse("Timer should not finish after 1 second", finished)

    val afterOneTick = repository.activeFocusSession.value
    assertEquals(
      "Remaining seconds should be exactly 1499 (not dropped by 60s)",
      1499,
      afterOneTick?.remainingSeconds
    )
    assertEquals(
      "Total seconds must remain 1500 and not reset to 24m",
      1500,
      afterOneTick?.totalSeconds
    )
  }

  @Test
  fun testFocusTimerAutoCompletesAtZero() {
    // Start a short 1-minute sprint = 60s
    repository.startFocusSession("Quick Sprint", 1, "Campfire Rain")
    
    // Tick 59 seconds
    repeat(59) {
      val finished = repository.tickFocusSecond()
      assertFalse(finished)
    }
    assertEquals(1, repository.activeFocusSession.value?.remainingSeconds)

    // 60th tick: should reach 0 and return finished = true
    val finished = repository.tickFocusSecond()
    assertTrue("Timer must finish on reaching 0", finished)

    val completedSession = repository.activeFocusSession.value
    assertEquals(0, completedSession?.remainingSeconds)
    assertFalse("Timer must stop running on completion", completedSession?.isRunning ?: true)
  }

  @Test
  fun testXpAwardDoesNotTriggerPerpetualLevelUp() {
    // Initial profile starts at 1000 XP with 1500 target XP
    val initialProfile = repository.userProfile.value
    assertEquals(1000, initialProfile.currentXp)
    assertEquals(1500, initialProfile.targetXp)
    assertEquals(5, initialProfile.level)
    assertFalse(repository.showLevelUpModal.value)

    // Completing a task awards 120 XP -> total 1120 XP (< 1500)
    repository.awardXp(120)
    val updatedProfile = repository.userProfile.value
    assertEquals(1120, updatedProfile.currentXp)
    assertEquals(5, updatedProfile.level)
    assertFalse("Level up modal should NOT pop up when target XP is not met", repository.showLevelUpModal.value)

    // Awarding 400 more XP reaches 1520 XP (>= 1500 target) -> Level up!
    repository.awardXp(400)
    val leveledProfile = repository.userProfile.value
    assertEquals(6, leveledProfile.level)
    assertEquals(20, leveledProfile.currentXp) // 1520 - 1500 = 20 rollover
    assertEquals(2000, leveledProfile.targetXp) // Target scales to 2000
    assertTrue("Level up modal MUST show when leveling up", repository.showLevelUpModal.value)
  }

  @Test
  fun testSubtaskCountAndBadgeReactivity() {
    val taskId = repository.addTask(
      title = "Token Architecture Spec",
      description = "Verify tokens",
      category = "Work",
      project = "Design System Migration",
      priority = TaskPriority.HIGH,
      dueTime = "14:00",
      dueDate = "Today"
    )

    val initialTask = repository.tasks.value.first { it.id == taskId }
    assertEquals(0, initialTask.actualSubtasksTotal)
    assertEquals(0, initialTask.actualSubtasksCompleted)
    assertNull(initialTask.actualSubtaskBadge)

    // Add first subtask
    repository.addSubTask(taskId, "Create color primitives JSON", 15)
    val taskWithOneSub = repository.tasks.value.first { it.id == taskId }
    assertEquals(1, taskWithOneSub.actualSubtasksTotal)
    assertEquals(0, taskWithOneSub.actualSubtasksCompleted)
    assertEquals("0/1 sub-tasks", taskWithOneSub.actualSubtaskBadge)

    // Add second subtask
    repository.addSubTask(taskId, "Verify contrast ratios", 10)
    val taskWithTwoSubs = repository.tasks.value.first { it.id == taskId }
    assertEquals(2, taskWithTwoSubs.actualSubtasksTotal)
    assertEquals(0, taskWithTwoSubs.actualSubtasksCompleted)
    assertEquals("0/2 sub-tasks", taskWithTwoSubs.actualSubtaskBadge)

    // Toggle the first subtask
    val firstSubId = taskWithTwoSubs.subtasks.first().id
    repository.toggleSubTask(taskId, firstSubId)
    val taskWithToggledSub = repository.tasks.value.first { it.id == taskId }
    assertEquals(1, taskWithToggledSub.actualSubtasksCompleted)
    assertEquals("1/2 sub-tasks", taskWithToggledSub.actualSubtaskBadge)
  }

  @Test
  fun testKanbanStatusTransitions() {
    val taskId = repository.addTask(
      title = "Database Migration Test",
      category = "College",
      project = "Database Design Final"
    )

    // Initial status is TO_DO
    val task = repository.tasks.value.first { it.id == taskId }
    assertEquals(KanbanColumn.TO_DO, task.kanbanStatus)
    assertFalse(task.isCompleted)

    // Move to IN_PROGRESS
    repository.updateKanbanStatus(taskId, KanbanColumn.IN_PROGRESS)
    val inProgressTask = repository.tasks.value.first { it.id == taskId }
    assertEquals(KanbanColumn.IN_PROGRESS, inProgressTask.kanbanStatus)
    assertFalse(inProgressTask.isCompleted)

    // Move to DONE
    repository.updateKanbanStatus(taskId, KanbanColumn.DONE)
    val doneTask = repository.tasks.value.first { it.id == taskId }
    assertEquals(KanbanColumn.DONE, doneTask.kanbanStatus)
    assertTrue("Task should be marked completed when moved to DONE", doneTask.isCompleted)

    // Move back to IN_PROGRESS
    repository.updateKanbanStatus(taskId, KanbanColumn.IN_PROGRESS)
    val reopenedTask = repository.tasks.value.first { it.id == taskId }
    assertEquals(KanbanColumn.IN_PROGRESS, reopenedTask.kanbanStatus)
    assertFalse("Task should be reopened when moved away from DONE", reopenedTask.isCompleted)
  }

  @Test
  fun testScheduleSlotBookingOrderAndFocusFlag() {
    repository.bookScheduleSlot("08:00", "09:00", "Morning Review", "Work", isDeepWork = false)
    repository.bookScheduleSlot("18:00", "19:00", "Evening Wrapup", "Personal", isDeepWork = false)

    val schedule = repository.todaySchedule.value
    // Check sorting
    for (i in 0 until schedule.size - 1) {
      assertTrue(
        "Schedule must be sorted chronologically: ${schedule[i].startTime} <= ${schedule[i+1].startTime}",
        schedule[i].startTime <= schedule[i+1].startTime
      )
    }

    // Only at most one slot should be current focus
    val currentFocusCount = schedule.count { it.isCurrentFocus }
    assertTrue("At most one commitment should be current focus", currentFocusCount <= 1)
  }

  @Test
  fun testUserProfileUpdate() {
    repository.updateUserProfile("Budi Santoso", "budi@univ.ac.id", "Computer Science • Year 4")
    val profile = repository.userProfile.value
    assertEquals("Budi Santoso", profile.name)
    assertEquals("budi@univ.ac.id", profile.email)
    assertEquals("Computer Science • Year 4", profile.program)
  }

  @Test
  fun testSprintReflectionRecorded() {
    repository.recordSprintReflection("High Energy", "Great focus on design system tokens")
    val reflections = repository.sprintReflections.value
    assertEquals(1, reflections.size)
    assertEquals("High Energy", reflections.first().energy)
    assertEquals("Great focus on design system tokens", reflections.first().note)
  }

  @Test
  fun testMilestoneTierProgressionFromXp() {
    // 0 - 500 XP -> Foundation
    assertEquals(com.example.data.model.MilestoneTierLevel.FOUNDATION, com.example.data.model.MilestoneTierLevel.fromXp(0))
    assertEquals(com.example.data.model.MilestoneTierLevel.FOUNDATION, com.example.data.model.MilestoneTierLevel.fromXp(499))

    // 500 - 1200 XP -> Apprentice Drafter
    assertEquals(com.example.data.model.MilestoneTierLevel.APPRENTICE_DRAFTER, com.example.data.model.MilestoneTierLevel.fromXp(500))
    assertEquals(com.example.data.model.MilestoneTierLevel.APPRENTICE_DRAFTER, com.example.data.model.MilestoneTierLevel.fromXp(1199))

    // 1200 - 2500 XP -> Master Builder
    assertEquals(com.example.data.model.MilestoneTierLevel.MASTER_BUILDER, com.example.data.model.MilestoneTierLevel.fromXp(1200))
    assertEquals(com.example.data.model.MilestoneTierLevel.MASTER_BUILDER, com.example.data.model.MilestoneTierLevel.fromXp(2499))

    // 2500 - 4500 XP -> Focus Architect
    assertEquals(com.example.data.model.MilestoneTierLevel.FOCUS_ARCHITECT, com.example.data.model.MilestoneTierLevel.fromXp(2500))
    assertEquals(com.example.data.model.MilestoneTierLevel.FOCUS_ARCHITECT, com.example.data.model.MilestoneTierLevel.fromXp(4499))

    // 4500+ XP -> Sovereign Visionary
    assertEquals(com.example.data.model.MilestoneTierLevel.SOVEREIGN_VISIONARY, com.example.data.model.MilestoneTierLevel.fromXp(4500))
    assertEquals(com.example.data.model.MilestoneTierLevel.SOVEREIGN_VISIONARY, com.example.data.model.MilestoneTierLevel.fromXp(15000))
  }

  @Test
  fun testShareCardConfigUpdates() {
    val initialConfig = repository.shareCardConfig.value
    assertEquals(com.example.data.model.ShareCardAspectRatio.STORY_9_16, initialConfig.aspectRatio)
    assertEquals(com.example.data.model.ShareCardTheme.BAUHAUS_WARM, initialConfig.theme)

    val updatedConfig = initialConfig.copy(
      aspectRatio = com.example.data.model.ShareCardAspectRatio.SQUARE_1_1,
      theme = com.example.data.model.ShareCardTheme.DARK_OBSIDIAN,
      customQuote = "Focus is an architectural discipline."
    )
    repository.updateShareCardConfig(updatedConfig)

    val currentConfig = repository.shareCardConfig.value
    assertEquals(com.example.data.model.ShareCardAspectRatio.SQUARE_1_1, currentConfig.aspectRatio)
    assertEquals(com.example.data.model.ShareCardTheme.DARK_OBSIDIAN, currentConfig.theme)
    assertEquals("Focus is an architectural discipline.", currentConfig.customQuote)
  }

  @Test
  fun testAllTimeXpAccumulationAndTierSync() {
    val initialProfile = repository.userProfile.value
    assertEquals(2850, initialProfile.totalXpAllTime)
    assertEquals("Focus Architect", initialProfile.levelTitle)

    // Award XP
    repository.awardXp(200)
    val afterXp = repository.userProfile.value
    assertEquals(3050, afterXp.totalXpAllTime)
    assertEquals("Focus Architect", afterXp.levelTitle)
  }

  @Test
  fun testCollegeTaskCreationWithCourseName() {
    val taskId = repository.addNewTask(
      title = "Implement B-Tree Indexing",
      category = "College",
      courseName = "Basis Data Lanjut"
    )
    val createdTask = repository.tasks.value.firstOrNull { it.id == taskId }
    assertNotNull("Created task should be found in repository", createdTask)
    assertEquals("College", createdTask?.category)
    assertEquals("Basis Data Lanjut", createdTask?.courseName)
    assertEquals("Implement B-Tree Indexing", createdTask?.title)
  }
}
