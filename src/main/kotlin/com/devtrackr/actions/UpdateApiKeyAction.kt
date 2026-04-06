package com.devtrackr.actions

import com.devtrackr.settings.DevTrackrSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.DialogWrapper
import com.devtrackr.services.ActivityTrackerService
import com.intellij.openapi.application.ApplicationManager

/**
 * Action to update the API key
 */
class UpdateApiKeyAction : AnAction("DevTrackr - Set API Key", "Set your DevTrackr API key", null) {
    override fun actionPerformed(e: AnActionEvent) {
        val settings = DevTrackrSettings.getInstance()
        val currentKey = settings.apiKey ?: ""
        
        val newKey = Messages.showInputDialog(
            e.project,
            "Enter your DevTrackr API key:",
            "DevTrackr API Key",
            Messages.getQuestionIcon(),
            currentKey,
            object : InputValidator {
                override fun checkInput(inputString: String?): Boolean {
                    return !inputString.isNullOrBlank()
                }

                override fun canClose(inputString: String?): Boolean {
                    return checkInput(inputString)
                }
            }
        )
        
        if (newKey != null) {
            settings.apiKey = newKey
            val activityTracker = ApplicationManager.getApplication().getService(ActivityTrackerService::class.java)
            activityTracker.setApiKey(newKey)
            Messages.showInfoMessage(e.project, "API key saved successfully!", "DevTrackr")
        }
    }
}
