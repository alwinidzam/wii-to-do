package com.example.data.local.converters

import androidx.room.TypeConverter
import com.example.data.model.KanbanColumn
import com.example.data.model.SubTask
import com.example.data.model.TaskPriority
import org.json.JSONArray
import org.json.JSONObject

class RoomTypeConverters {

  @TypeConverter
  fun fromSubTaskList(subtasks: List<SubTask>?): String {
    if (subtasks.isNullOrEmpty()) return "[]"
    val array = JSONArray()
    for (item in subtasks) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("title", item.title)
        put("isCompleted", item.isCompleted)
        put("effortMinutes", item.effortMinutes)
        put("completedAt", item.completedAt ?: "")
      }
      array.put(obj)
    }
    return array.toString()
  }

  @TypeConverter
  fun toSubTaskList(data: String?): List<SubTask> {
    if (data.isNullOrBlank()) return emptyList()
    val list = mutableListOf<SubTask>()
    try {
      val array = JSONArray(data)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        val completedAtStr = obj.optString("completedAt", "")
        list.add(
          SubTask(
            id = obj.getString("id"),
            title = obj.getString("title"),
            isCompleted = obj.optBoolean("isCompleted", false),
            effortMinutes = obj.optInt("effortMinutes", 15),
            completedAt = if (completedAtStr.isNotBlank()) completedAtStr else null
          )
        )
      }
    } catch (_: Exception) {
      // fallback
    }
    return list
  }

  @TypeConverter
  fun fromStringList(tags: List<String>?): String {
    if (tags.isNullOrEmpty()) return "[]"
    val array = JSONArray()
    for (tag in tags) {
      array.put(tag)
    }
    return array.toString()
  }

  @TypeConverter
  fun toStringList(data: String?): List<String> {
    if (data.isNullOrBlank()) return emptyList()
    val list = mutableListOf<String>()
    try {
      val array = JSONArray(data)
      for (i in 0 until array.length()) {
        list.add(array.getString(i))
      }
    } catch (_: Exception) {
      // fallback
    }
    return list
  }

  @TypeConverter
  fun fromTaskPriority(priority: TaskPriority?): String {
    return priority?.name ?: TaskPriority.MED.name
  }

  @TypeConverter
  fun toTaskPriority(name: String?): TaskPriority {
    return try {
      if (name != null) TaskPriority.valueOf(name) else TaskPriority.MED
    } catch (_: Exception) {
      TaskPriority.MED
    }
  }

  @TypeConverter
  fun fromKanbanColumn(column: KanbanColumn?): String {
    return column?.name ?: KanbanColumn.TO_DO.name
  }

  @TypeConverter
  fun toKanbanColumn(name: String?): KanbanColumn {
    return try {
      if (name != null) KanbanColumn.valueOf(name) else KanbanColumn.TO_DO
    } catch (_: Exception) {
      KanbanColumn.TO_DO
    }
  }

  @TypeConverter
  fun fromAttachmentList(attachments: List<com.example.data.model.AttachmentItem>?): String {
    if (attachments.isNullOrEmpty()) return "[]"
    val array = JSONArray()
    for (item in attachments) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("ownerId", item.ownerId)
        put("fileName", item.fileName)
        put("fileSizeFormatted", item.fileSizeFormatted)
        put("fileSizeBytes", item.fileSizeBytes)
        put("mimeType", item.mimeType)
        put("localFilePath", item.localFilePath)
        put("createdAt", item.createdAt)
      }
      array.put(obj)
    }
    return array.toString()
  }

  @TypeConverter
  fun toAttachmentList(data: String?): List<com.example.data.model.AttachmentItem> {
    if (data.isNullOrBlank()) return emptyList()
    val list = mutableListOf<com.example.data.model.AttachmentItem>()
    try {
      val array = JSONArray(data)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          com.example.data.model.AttachmentItem(
            id = obj.getString("id"),
            ownerId = obj.optString("ownerId", ""),
            fileName = obj.getString("fileName"),
            fileSizeFormatted = obj.optString("fileSizeFormatted", ""),
            fileSizeBytes = obj.optLong("fileSizeBytes", 0L),
            mimeType = obj.optString("mimeType", "*/*"),
            localFilePath = obj.getString("localFilePath"),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
          )
        )
      }
    } catch (_: Exception) {
      // fallback
    }
    return list
  }
}
