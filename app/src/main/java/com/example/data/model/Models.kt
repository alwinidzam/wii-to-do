package com.example.data.model

enum class TaskPriority(val label: String) {
  NONE("None"),
  LOW("Low"),
  MED("Med"),
  HIGH("High"),
  BLOCKER("Blocker")
}

enum class KanbanColumn(val label: String) {
  TO_DO("To Do"),
  IN_PROGRESS("In Progress"),
  DONE("Done")
}

data class SubTask(
  val id: String,
  val title: String,
  val isCompleted: Boolean = false,
  val effortMinutes: Int = 15,
  val completedAt: String? = null
)

data class TaskItem(
  val id: String,
  val title: String,
  val description: String = "",
  val category: String = "Work", // College, Work, Personal
  val project: String = "General",
  val priority: TaskPriority = TaskPriority.MED,
  val dueTime: String = "Today",
  val dueDate: String = "Today (Oct 24)",
  val isCompleted: Boolean = false,
  val completedAt: String? = null,
  val subtasks: List<SubTask> = emptyList(),
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
  val courseName: String? = null
) {
  val actualSubtaskBadge: String?
    get() = if (subtasks.isNotEmpty()) "${subtasks.count { it.isCompleted }}/${subtasks.size} sub-tasks" else subtaskBadge

  val actualSubtasksTotal: Int
    get() = if (subtasks.isNotEmpty()) subtasks.size else (subtasksTotalCount ?: 0)

  val actualSubtasksCompleted: Int
    get() = if (subtasks.isNotEmpty()) subtasks.count { it.isCompleted } else (subtasksCompletedCount ?: 0)
}

data class ScheduleCommitment(
  val id: String,
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

data class ProjectItem(
  val id: String,
  val title: String,
  val description: String,
  val category: String, // College, Work, Personal
  val code: String, // e.g. "CS340", "Freelance"
  val dueDate: String,
  val totalTasks: Int,
  val completedTasks: Int,
  val activeSprint: String = "Sprint #2 Active",
  val nextTaskPreview: String = "",
  val nextTaskDue: String = "",
  val tags: List<String> = emptyList()
)

data class UserProfile(
  val name: String = "Alwi Pratama",
  val email: String = "alwi.student@university.edu",
  val program: String = "Informatics Engineering • Year 3",
  val focusGoal: String = "Focusing on Thesis & UI Architecture",
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
  val completionSounds: Boolean = true
)

data class FocusSessionState(
  val taskTitle: String,
  val targetSubtask: String,
  val totalSeconds: Int = 25 * 60,
  val remainingSeconds: Int = 25 * 60,
  val isRunning: Boolean = false,
  val soundscape: String = "Brown Noise Calm",
  val flowScore: Int = 92
)

data class SprintReflection(
  val id: String = java.util.UUID.randomUUID().toString(),
  val taskTitle: String,
  val durationMinutes: Int,
  val energyLevel: String,
  val note: String
)

enum class MilestoneTierLevel(
  val tierNumber: Int,
  val title: String,
  val subtitle: String,
  val minXp: Int,
  val maxXp: Int,
  val badgeCode: String,
  val philosophyQuote: String
) {
  FOUNDATION(
    tierNumber = 1,
    title = "Foundation",
    subtitle = "Bedrock of Discipline",
    minXp = 0,
    maxXp = 500,
    badgeCode = "TIER-I",
    philosophyQuote = "Order precedes mastery; every monument rests on silent bedrock."
  ),
  APPRENTICE_DRAFTER(
    tierNumber = 2,
    title = "Apprentice Drafter",
    subtitle = "Habit & Task Architecture",
    minXp = 500,
    maxXp = 1200,
    badgeCode = "TIER-II",
    philosophyQuote = "The mind drafts the blueprint; deliberate action carves reality."
  ),
  MASTER_BUILDER(
    tierNumber = 3,
    title = "Master Builder",
    subtitle = "Deep Sprints & Unbroken Flow",
    minXp = 1200,
    maxXp = 2500,
    badgeCode = "TIER-III",
    philosophyQuote = "Flow is not chance; it is intentional construction."
  ),
  FOCUS_ARCHITECT(
    tierNumber = 4,
    title = "Focus Architect",
    subtitle = "Cognitive Sovereignty",
    minXp = 2500,
    maxXp = 4500,
    badgeCode = "TIER-IV",
    philosophyQuote = "To master attention is to dictate the shape of one's destiny."
  ),
  SOVEREIGN_VISIONARY(
    tierNumber = 5,
    title = "Sovereign Visionary",
    subtitle = "Zenith of Timeless Output",
    minXp = 4500,
    maxXp = 10000,
    badgeCode = "TIER-V",
    philosophyQuote = "Simplicity at the pinnacle of complexity is true power."
  );

  companion object {
    fun fromXp(allTimeXp: Int): MilestoneTierLevel {
      return entries.lastOrNull { allTimeXp >= it.minXp } ?: FOUNDATION
    }
  }
}

enum class ShareCardAspectRatio(val label: String, val ratioWidth: Float, val ratioHeight: Float) {
  STORY_9_16("Story 9:16", 9f, 16f),
  SQUARE_1_1("Square 1:1", 1f, 1f)
}

enum class ShareCardTheme(
  val id: String,
  val label: String,
  val bgHex: Long,
  val surfaceHex: Long,
  val textPrimaryHex: Long,
  val textSecondaryHex: Long,
  val accentHex: Long,
  val borderHex: Long
) {
  BAUHAUS_WARM(
    id = "bauhaus",
    label = "Bauhaus Warm",
    bgHex = 0xFFF7F5F0,
    surfaceHex = 0xFFFFFFFF,
    textPrimaryHex = 0xFF1C1B19,
    textSecondaryHex = 0xFF7D7A73,
    accentHex = 0xFFD85A38,
    borderHex = 0xFFE5E2D9
  ),
  DARK_OBSIDIAN(
    id = "dark_obsidian",
    label = "Dark Obsidian",
    bgHex = 0xFF121212,
    surfaceHex = 0xFF1E1E1E,
    textPrimaryHex = 0xFFF5F5F5,
    textSecondaryHex = 0xFFA0A0A0,
    accentHex = 0xFF34D399,
    borderHex = 0xFF2E2E2E
  ),
  MINIMALIST_CHARCOAL(
    id = "minimal_charcoal",
    label = "Minimalist Charcoal",
    bgHex = 0xFFFFFFFF,
    surfaceHex = 0xFFFAF9F6,
    textPrimaryHex = 0xFF18181B,
    textSecondaryHex = 0xFF71717A,
    accentHex = 0xFF18181B,
    borderHex = 0xFFE4E4E7
  ),
  OLIVE_STUDIO(
    id = "olive_studio",
    label = "Olive Studio",
    bgHex = 0xFFF1F4F1,
    surfaceHex = 0xFFFFFFFF,
    textPrimaryHex = 0xFF243026,
    textSecondaryHex = 0xFF607063,
    accentHex = 0xFF436850,
    borderHex = 0xFFD3DDD4
  )
}

data class ShareCardConfig(
  val aspectRatio: ShareCardAspectRatio = ShareCardAspectRatio.STORY_9_16,
  val theme: ShareCardTheme = ShareCardTheme.BAUHAUS_WARM,
  val showXpProgress: Boolean = true,
  val showFocusHours: Boolean = true,
  val showStreak: Boolean = true,
  val showTasksDone: Boolean = true,
  val showQuote: Boolean = true,
  val customQuote: String = "Building focus, one architectural brick at a time."
)

data class AcademicCourse(
  val id: String,
  val code: String,       // e.g. "SO", "PW", "SD", "AI"
  val name: String,       // e.g. "Sistem Operasi"
  val colorHex: Long = 0xFF4F46E5
)

object AcademicCourseDefaults {
  val PRESET_COURSES = listOf(
    AcademicCourse("c_so", "SO", "Sistem Operasi", 0xFF10B981),
    AcademicCourse("c_pw", "PW", "Pemrograman Web", 0xFF4F46E5),
    AcademicCourse("c_sd", "SD", "Struktur Data", 0xFFF59E0B),
    AcademicCourse("c_ai", "AI", "Kecerdasan Buatan", 0xFF8B5CF6),
    AcademicCourse("c_bd", "BD", "Basis Data", 0xFF0284C7)
  )

  val PALETTE = listOf(
    0xFF10B981, // Emerald
    0xFF4F46E5, // Indigo
    0xFFF59E0B, // Amber
    0xFF8B5CF6, // Purple
    0xFFE11D48, // Rose
    0xFF0284C7, // Sky
    0xFF059669, // Forest
    0xFFD97706  // Tangerine
  )

  fun generateCode(name: String): String {
    val words = name.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
      words.size >= 2 -> words.take(3).map { it.first().uppercase() }.joinToString("")
      words.size == 1 && words[0].length >= 3 -> words[0].take(3).uppercase()
      words.size == 1 -> words[0].uppercase()
      else -> "SUB"
    }
  }
}


