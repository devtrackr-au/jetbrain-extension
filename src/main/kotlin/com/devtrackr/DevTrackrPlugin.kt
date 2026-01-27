package com.devtrackr

import com.devtrackr.services.ActivityTrackerService
import com.devtrackr.settings.DevTrackrSettings
import com.devtrackr.utils.DevTrackrLogger
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFile

/**
 * Main plugin class that initializes the DevTrackr plugin
 */
class DevTrackrPlugin : StartupActivity {
    override fun runActivity(project: Project) {
        val settings = DevTrackrSettings.getInstance()
        
        if (settings.debugMode) {
            DevTrackrLogger.enable()
        }
        
        DevTrackrLogger.log("DevTrackr plugin activated for project: ${project.name}")
        
        val activityTracker = ApplicationManager.getApplication().getService(ActivityTrackerService::class.java)
        
        // If API key is set, start tracking
        if (activityTracker.getApiKey().isNotBlank()) {
            activityTracker.startTracking()
        }
        
        val connection = project.messageBus.connect()
        
        // Register file editor listener
        connection.subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                    activityTracker.handleEditorChange(project, file)
                    
                    // Add document listener when file is opened
                    val document = FileDocumentManager.getInstance().getDocument(file)
                    document?.addDocumentListener(object : DocumentListener {
                        override fun documentChanged(event: DocumentEvent) {
                            activityTracker.handleDocumentChange(event.document)
                        }
                    })
                }
            }
        )
    }
}
