package com.devtrackr.services

import com.devtrackr.config.Config
import com.devtrackr.types.ActivityData
import com.devtrackr.utils.DevTrackrLogger
import com.intellij.openapi.application.ApplicationManager
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Service for sending activity data to the DevTrackr API
 */
object ApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Send activity data to the API asynchronously
     */
    fun sendToApi(data: ActivityData) {
        if (data.apiKey.isBlank()) {
            DevTrackrLogger.log("Not sending data: No API key")
            return
        }

        DevTrackrLogger.log("Preparing to send data to API: ${data.event}")

        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val serverConfig = Config.getServerConfig()
                val url = "${serverConfig.hostname}${serverConfig.path}"

                val jsonData = mapOf(
                    "timestamp" to data.timestamp,
                    "event" to data.event.name.lowercase(),
                    "project" to data.project,
                    "fileName" to data.fileName,
                    "filePath" to data.filePath,
                    "language" to data.language,
                    "apiKey" to data.apiKey,
                    "machineId" to data.machineId,
                    "sessionId" to data.sessionId,
                    "editorName" to data.editorName,
                    "editorVersion" to data.editorVersion,
                    "osType" to data.osType,
                    "timezone" to data.timezone
                ).plus(data.gitBranch?.let { mapOf("gitBranch" to it) } ?: emptyMap())
                 .plus(data.gitRepo?.let { mapOf("gitRepo" to it) } ?: emptyMap())

                val json = Gson().toJson(jsonData)
                val requestBody = json.toRequestBody(jsonMediaType)

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                
                DevTrackrLogger.log("API response status: ${response.code}")
                
                if (!response.isSuccessful) {
                    DevTrackrLogger.error("API request failed with status code: ${response.code}")
                }
                
                response.close()
            } catch (e: Exception) {
                DevTrackrLogger.error("Error sending data to API: ${e.message}", e)
            }
        }
    }
}
