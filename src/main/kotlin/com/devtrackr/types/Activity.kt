package com.devtrackr.types

/**
 * Activity event types
 */
enum class ActivityEvent {
    START,
    ACTIVE,
    IDLE,
    SWITCH,
    END
}

/**
 * Activity data structure sent to the API
 */
data class ActivityData(
    val timestamp: String,
    val event: ActivityEvent,
    val project: String,
    val fileName: String,
    val filePath: String,
    val language: String,
    val apiKey: String,
    val machineId: String,
    val sessionId: String,
    val editorName: String,
    val editorVersion: String,
    val gitBranch: String? = null,
    val gitRepo: String? = null,
    val osType: String,
    val timezone: String
)
