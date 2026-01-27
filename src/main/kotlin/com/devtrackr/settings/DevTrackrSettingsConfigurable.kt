package com.devtrackr.settings

import com.devtrackr.services.ActivityTrackerService
import com.devtrackr.utils.DevTrackrLogger
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Settings UI for DevTrackr plugin
 */
class DevTrackrSettingsConfigurable : SearchableConfigurable {
    private val settings = DevTrackrSettings.getInstance()
    private val apiKeyField = JBTextField()
    private val debugModeCheckbox = javax.swing.JCheckBox("Enable debug logging")

    override fun getId(): String = "com.devtrackr.settings"

    override fun getDisplayName(): String = "DevTrackr"

    override fun createComponent(): JComponent {
        reset()
        
        return FormBuilder.createFormBuilder()
            .addComponent(JBLabel("Enter your DevTrackr API key to start tracking your coding activity."))
            .addLabeledComponent("API Key:", apiKeyField, 1, false)
            .addComponent(debugModeCheckbox)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        return apiKeyField.text != (settings.apiKey ?: "") ||
               debugModeCheckbox.isSelected != settings.debugMode
    }

    override fun apply() {
        val newApiKey = apiKeyField.text.trim()
        settings.apiKey = if (newApiKey.isBlank()) null else newApiKey
        settings.debugMode = debugModeCheckbox.isSelected
        
        if (settings.debugMode) {
            DevTrackrLogger.enable()
        } else {
            DevTrackrLogger.disable()
        }
        
        // Update the activity tracker service
        val activityTracker = ApplicationManager.getApplication().getService(ActivityTrackerService::class.java)
        activityTracker.setApiKey(newApiKey)
    }

    override fun reset() {
        apiKeyField.text = settings.apiKey ?: ""
        debugModeCheckbox.isSelected = settings.debugMode
        
        if (settings.debugMode) {
            DevTrackrLogger.enable()
        } else {
            DevTrackrLogger.disable()
        }
    }
}
