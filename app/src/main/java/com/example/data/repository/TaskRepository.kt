package com.example.data.repository

import android.util.Log
import com.example.data.model.KanbanColumn
import com.example.data.model.SubTask
import com.example.data.model.TaskItem
import com.example.data.model.TaskPriority
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * TaskRepository handles CRUD operations and real-time snapshot listeners
 * for the 'tasks' collection in Firebase Firestore.
 */
class TaskRepository(
  private val firestore: FirebaseFirestore? = try {
    FirebaseFirestore.getInstance()
  } catch (e: Exception) {
    Log.w("TaskRepository", "FirebaseFirestore not initialized or google-services.json not configured: ${e.message}")
    null
  }
) {
  companion object {
    private const val TAG = "TaskRepository"
    private const val COLLECTION_TASKS = "tasks"
  }

  /**
   * Observes real-time changes to all tasks in the 'tasks' collection.
   * Emits updated lists whenever any task document is created, modified, or removed.
   */
  fun getTasksFlow(): Flow<List<TaskItem>> = callbackFlow {
    val db = firestore
    if (db == null) {
      Log.w(TAG, "Firestore instance is null; emitting empty list")
      trySend(emptyList())
      close()
      return@callbackFlow
    }

    val collectionRef = db.collection(COLLECTION_TASKS)
    val listenerRegistration = collectionRef.addSnapshotListener { snapshot, error ->
      if (error != null) {
        Log.e(TAG, "Error in tasks snapshot listener", error)
        close(error)
        return@addSnapshotListener
      }

      if (snapshot != null) {
        val taskItems = snapshot.documents.mapNotNull { doc ->
          doc.toTaskItem()
        }
        trySend(taskItems)
      }
    }

    awaitClose {
      Log.d(TAG, "Removing tasks snapshot listener")
      listenerRegistration.remove()
    }
  }

  /**
   * Observes real-time changes to a single task document by its ID.
   */
  fun getTaskByIdFlow(taskId: String): Flow<TaskItem?> = callbackFlow {
    val db = firestore
    if (db == null) {
      trySend(null)
      close()
      return@callbackFlow
    }

    val docRef = db.collection(COLLECTION_TASKS).document(taskId)
    val listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
      if (error != null) {
        Log.e(TAG, "Error in task document snapshot listener for $taskId", error)
        close(error)
        return@addSnapshotListener
      }

      if (snapshot != null && snapshot.exists()) {
        trySend(snapshot.toTaskItem())
      } else {
        trySend(null)
      }
    }

    awaitClose {
      listenerRegistration.remove()
    }
  }

  /**
   * Creates a new task in the 'tasks' collection.
   * If task.id is blank, generates a unique ID.
   */
  suspend fun createTask(task: TaskItem): Result<String> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      val taskId = if (task.id.isNotBlank()) task.id else UUID.randomUUID().toString()
      val taskWithId = task.copy(id = taskId)
      val docRef = db.collection(COLLECTION_TASKS).document(taskId)
      
      docRef.set(taskWithId.toFirestoreMap()).awaitTask()
      Result.success(taskId)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to create task", e)
      Result.failure(e)
    }
  }

  /**
   * Updates an existing task document in Firestore.
   */
  suspend fun updateTask(task: TaskItem): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      val docRef = db.collection(COLLECTION_TASKS).document(task.id)
      docRef.set(task.toFirestoreMap(), SetOptions.merge()).awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to update task ${task.id}", e)
      Result.failure(e)
    }
  }

  /**
   * Deletes a task document from Firestore by its ID.
   */
  suspend fun deleteTask(taskId: String): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      db.collection(COLLECTION_TASKS).document(taskId).delete().awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to delete task $taskId", e)
      Result.failure(e)
    }
  }

  /**
   * Toggles the completion state of a task in Firestore.
   */
  suspend fun setTaskCompletion(taskId: String, isCompleted: Boolean): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      val updates = mapOf(
        "isCompleted" to isCompleted,
        "completedAt" to if (isCompleted) "Today" else null
      )
      db.collection(COLLECTION_TASKS).document(taskId).update(updates).awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to toggle completion for task $taskId", e)
      Result.failure(e)
    }
  }

  /**
   * Updates the subtasks array of a task.
   */
  suspend fun updateSubtasks(taskId: String, subtasks: List<SubTask>): Result<Unit> {
    val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not available"))
    return try {
      val subtaskMaps = subtasks.map { it.toFirestoreMap() }
      db.collection(COLLECTION_TASKS).document(taskId).update("subtasks", subtaskMaps).awaitTask()
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to update subtasks for task $taskId", e)
      Result.failure(e)
    }
  }
}

/**
 * Extension to convert Google Play Services Task<T> into a Kotlin coroutine.
 */
suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
  addOnSuccessListener { result ->
    continuation.resume(result)
  }
  addOnFailureListener { exception ->
    continuation.resumeWithException(exception)
  }
}

/**
 * Maps a TaskItem domain model into a Firestore-friendly Map.
 */
fun TaskItem.toFirestoreMap(): Map<String, Any?> {
  return mapOf(
    "id" to id,
    "title" to title,
    "description" to description,
    "category" to category,
    "project" to project,
    "priority" to priority.name,
    "dueTime" to dueTime,
    "dueDate" to dueDate,
    "isCompleted" to isCompleted,
    "completedAt" to completedAt,
    "subtasks" to subtasks.map { it.toFirestoreMap() },
    "tags" to tags,
    "kanbanStatus" to kanbanStatus.name,
    "estimatedEffortMinutes" to estimatedEffortMinutes,
    "assignedTo" to assignedTo
  )
}

/**
 * Maps a SubTask into a Firestore-friendly Map.
 */
fun SubTask.toFirestoreMap(): Map<String, Any?> {
  return mapOf(
    "id" to id,
    "title" to title,
    "isCompleted" to isCompleted,
    "effortMinutes" to effortMinutes,
    "completedAt" to completedAt
  )
}

/**
 * Parses a DocumentSnapshot into a domain TaskItem.
 */
fun DocumentSnapshot.toTaskItem(): TaskItem? {
  if (!exists()) return null
  return try {
    val id = id
    val title = getString("title") ?: return null
    val description = getString("description") ?: ""
    val category = getString("category") ?: "Work"
    val project = getString("project") ?: "General"
    val priorityString = getString("priority") ?: TaskPriority.MED.name
    val priority = try {
      TaskPriority.valueOf(priorityString)
    } catch (e: Exception) {
      TaskPriority.MED
    }
    val dueTime = getString("dueTime") ?: "Today"
    val dueDate = getString("dueDate") ?: "Today (Oct 24)"
    val isCompleted = getBoolean("isCompleted") ?: false
    val completedAt = getString("completedAt")

    @Suppress("UNCHECKED_CAST")
    val subtasksRaw = get("subtasks") as? List<Map<String, Any?>> ?: emptyList()
    val subtasks = subtasksRaw.mapNotNull { subMap ->
      val subId = subMap["id"] as? String ?: return@mapNotNull null
      val subTitle = subMap["title"] as? String ?: return@mapNotNull null
      val subCompleted = subMap["isCompleted"] as? Boolean ?: false
      val effort = (subMap["effortMinutes"] as? Long)?.toInt() ?: 15
      val subCompletedAt = subMap["completedAt"] as? String
      SubTask(
        id = subId,
        title = subTitle,
        isCompleted = subCompleted,
        effortMinutes = effort,
        completedAt = subCompletedAt
      )
    }

    @Suppress("UNCHECKED_CAST")
    val tags = (get("tags") as? List<String>) ?: emptyList()

    val kanbanStatusString = getString("kanbanStatus") ?: KanbanColumn.TO_DO.name
    val kanbanStatus = try {
      KanbanColumn.valueOf(kanbanStatusString)
    } catch (e: Exception) {
      KanbanColumn.TO_DO
    }

    val estimatedEffortMinutes = getLong("estimatedEffortMinutes")?.toInt() ?: 30
    val assignedTo = getString("assignedTo") ?: "Alwi Pratama"

    TaskItem(
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
      tags = tags,
      kanbanStatus = kanbanStatus,
      estimatedEffortMinutes = estimatedEffortMinutes,
      assignedTo = assignedTo
    )
  } catch (e: Exception) {
    Log.e("TaskRepository", "Failed to deserialize task document $id", e)
    null
  }
}
