package com.devtrackr.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*

/**
 * Persistent settings for DevTrackr plugin
 */
@State(
    name = "DevTrackrSettings",
    storages = [Storage("devtrackr.xml")]
)
@Service
class DevTrackrSettings : PersistentStateComponent<DevTrackrSettings.State> {
    data class State(
        var apiKey: String? = null,
        var debugMode: Boolean = false,
        var idleThreshold: Int = 300,
        var heartbeatInterval: Int = 30
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var apiKey: String?
        get() = state.apiKey
        set(value) {
            state.apiKey = value
        }

    var debugMode: Boolean
        get() = state.debugMode
        set(value) {
            state.debugMode = value
        }

    var idleThreshold: Int
        get() = state.idleThreshold
        set(value) {
            state.idleThreshold = value
        }

    var heartbeatInterval: Int
        get() = state.heartbeatInterval
        set(value) {
            state.heartbeatInterval = value
        }

    companion object {
        fun getInstance(): DevTrackrSettings =
            ApplicationManager.getApplication().getService(DevTrackrSettings::class.java)
    }
}
