package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.SubTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

object GeminiTaskBreakdownService {

  private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .build()

  /**
   * Generates 3-5 structured subtasks for a given task title using Google Gemini AI,
   * with automatic offline heuristic fallback.
   */
  suspend fun breakDownTask(taskTitle: String): List<SubTask> = withContext(Dispatchers.IO) {
    val cleanTitle = taskTitle.trim()
    if (cleanTitle.isEmpty()) return@withContext emptyList()

    val apiKey = try {
      val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
      val key = field.get(null) as? String
      if (!key.isNullOrBlank() && key != "MY_GEMINI_API_KEY") key else null
    } catch (_: Exception) {
      null
    }

    if (!apiKey.isNullOrBlank()) {
      val candidateModels = listOf("gemini-flash-latest", "gemini-3.8-flash")
      val prompt = """
        You are an architectural productivity assistant.
        Break down the following task into 3 to 4 sequential, actionable subtasks with estimated minutes (15, 25, 30, or 45).
        Task: "$cleanTitle"
        Respond ONLY with a valid JSON array of objects with keys "title" and "effortMinutes".
        Example format:
        [
          {"title": "Review specifications and setup canvas", "effortMinutes": 15},
          {"title": "Implement core logic and wire data flows", "effortMinutes": 35},
          {"title": "Verify contrast, responsiveness, and review", "effortMinutes": 20}
        ]
      """.trimIndent()

      val payload = JSONObject().apply {
        val contents = JSONArray().apply {
          val part = JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().apply { put("text", prompt) })
            })
          }
          put(part)
        }
        put("contents", contents)
        put("generationConfig", JSONObject().apply {
          put("temperature", 0.2)
          put("responseMimeType", "application/json")
        })
      }

      val requestBody = payload.toString().toRequestBody("application/json".toMediaType())

      for (modelName in candidateModels) {
        try {
          val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"
          val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .build()

          val response = client.newCall(request).execute()
          if (response.isSuccessful) {
            val responseBody = response.body?.string()
            if (!responseBody.isNullOrBlank()) {
              val parsed = parseGeminiResponse(responseBody)
              if (parsed.isNotEmpty()) {
                return@withContext parsed
              }
            }
          }
        } catch (_: Exception) {
          // Try next model or fallback
        }
      }
    }

    // High quality offline heuristic fallback
    generateOfflineHeuristic(cleanTitle)
  }

  private fun parseGeminiResponse(jsonString: String): List<SubTask> {
    val subtasks = mutableListOf<SubTask>()
    try {
      val root = JSONObject(jsonString)
      val candidates = root.getJSONArray("candidates")
      val text = candidates.getJSONObject(0)
        .getJSONObject("content")
        .getJSONArray("parts")
        .getJSONObject(0)
        .getString("text")

      val array = JSONArray(text.trim())
      for (i in 0 until array.length()) {
        val item = array.getJSONObject(i)
        subtasks.add(
          SubTask(
            id = UUID.randomUUID().toString(),
            title = item.getString("title"),
            effortMinutes = item.optInt("effortMinutes", 25),
            isCompleted = false
          )
        )
      }
    } catch (_: Exception) {
    }
    return subtasks
  }

  private fun generateOfflineHeuristic(title: String): List<SubTask> {
    return listOf(
      SubTask(
        id = UUID.randomUUID().toString(),
        title = "Research & draft blueprint for $title",
        effortMinutes = 15,
        isCompleted = false
      ),
      SubTask(
        id = UUID.randomUUID().toString(),
        title = "Execute core implementation & modular architecture",
        effortMinutes = 35,
        isCompleted = false
      ),
      SubTask(
        id = UUID.randomUUID().toString(),
        title = "Run verification audit & refine edge cases",
        effortMinutes = 20,
        isCompleted = false
      )
    )
  }
}
