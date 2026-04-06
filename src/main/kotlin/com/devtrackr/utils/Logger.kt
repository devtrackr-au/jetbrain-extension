package com.devtrackr.utils

import com.intellij.openapi.diagnostic.Logger

/**
 * Logger utility for DevTrackr plugin
 */
object DevTrackrLogger {
    private val logger = Logger.getInstance("DevTrackr")
    private var debugMode: Boolean = false

    fun enable() {
        debugMode = true
    }

    fun disable() {
        debugMode = false
    }

    fun log(message: String) {
        if (debugMode) {
            logger.info(message)
        }
    }

    fun error(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            logger.error(message, throwable)
        } else {
            logger.error(message)
        }
    }
}
