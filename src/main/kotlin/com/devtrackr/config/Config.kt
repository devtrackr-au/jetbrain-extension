package com.devtrackr.config

/**
 * Server configuration for the DevTrackr API
 */
data class ServerConfig(
    val hostname: String,
    val path: String
)

/**
 * Default server configuration
 */
object Config {
    val defaultServerConfig = ServerConfig(
        hostname = "https://api.devtrackr.com",
        path = "/save-activity"
    )

    fun getServerConfig(): ServerConfig {
        return defaultServerConfig
    }
}
