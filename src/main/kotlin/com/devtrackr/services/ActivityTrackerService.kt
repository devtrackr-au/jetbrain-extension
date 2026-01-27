package com.devtrackr.services

import com.devtrackr.types.ActivityData
import com.devtrackr.types.ActivityEvent
import com.devtrackr.utils.DevTrackrLogger
import com.devtrackr.utils.ProjectInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VirtualFile
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Service that tracks coding activity and sends data to the DevTrackr API
 */
@Service
class ActivityTrackerService {
    private var lastActivity: Long = System.currentTimeMillis()
    private var isTracking: Boolean = false
    private val IDLE_THRESHOLD: Long = 60 * 1000 // 1 minute of inactivity
    private val HEARTBEAT_INTERVAL: Long = 30 * 1000 // Send heartbeat every 30 seconds
    
    private var apiKey: String = ""
    private val machineId: String = getOrCreateMachineId()
    private val sessionId: String = UUID.randomUUID().toString()
    
    private val scheduler = Executors.newScheduledThreadPool(2)
    private var idleCheckTask: ScheduledFuture<*>? = null
    private var heartbeatTask: ScheduledFuture<*>? = null
    
    private val logger = Logger.getInstance(ActivityTrackerService::class.java)

    init {
        loadApiKey()
        startTimers()
        DevTrackrLogger.log("ActivityTrackerService initialized. API key exists: ${apiKey.isNotBlank()}")
    }

    private fun getOrCreateMachineId(): String {
        val properties = System.getProperties()
        val machineIdKey = "devtrackr.machineId"
        var machineId = properties.getProperty(machineIdKey)
        
        if (machineId.isNullOrBlank()) {
            machineId = UUID.randomUUID().toString()
            properties.setProperty(machineIdKey, machineId)
        }
        
        return machineId
    }

    fun loadApiKey() {
        apiKey = com.devtrackr.settings.DevTrackrSettings.getInstance().apiKey ?: ""
    }

    fun setApiKey(key: String) {
        apiKey = key
        com.devtrackr.settings.DevTrackrSettings.getInstance().apiKey = key
        if (key.isNotBlank() && !isTracking) {
            startTracking()
        }
    }

    fun getApiKey(): String = apiKey

    private fun createActivityData(event: ActivityEvent, project: Project?, file: VirtualFile?): ActivityData {
        val now = Instant.now()
        val timezone = ZoneId.systemDefault().id
        
        return ActivityData(
            timestamp = now.toString(),
            event = event,
            project = ProjectInfo.getCurrentProjectName(project),
            fileName = ProjectInfo.getCurrentFileName(file),
            filePath = ProjectInfo.getRelativeFilePath(project, file),
            language = ProjectInfo.getCurrentLanguage(file),
            apiKey = apiKey,
            machineId = machineId,
            sessionId = sessionId,
            editorName = "DevTrackr for JetBrains",
            editorVersion = com.intellij.openapi.application.ApplicationInfo.getInstance().fullVersion,
            gitBranch = ProjectInfo.getCurrentGitBranch(project),
            gitRepo = ProjectInfo.getGitRepoUrl(project),
            osType = System.getProperty("os.name", "unknown"),
            timezone = timezone
        )
    }

    fun startTracking() {
        DevTrackrLogger.log("startTracking called. Current state: isTracking=$isTracking, hasApiKey=${apiKey.isNotBlank()}")
        
        if (!isTracking && apiKey.isNotBlank()) {
            isTracking = true
            lastActivity = System.currentTimeMillis()
            
            val activeProject = ProjectManager.getInstance().openProjects.firstOrNull()
            val activeFile = getActiveFile()
            
            DevTrackrLogger.log("Tracking started, sending initial event")
            ApiService.sendToApi(createActivityData(ActivityEvent.START, activeProject, activeFile))
        } else {
            DevTrackrLogger.log("Not starting tracking because: ${if (apiKey.isBlank()) "no API key" else "already tracking"}")
        }
    }

    fun stopTracking() {
        if (isTracking) {
            isTracking = false
            val activeProject = ProjectManager.getInstance().openProjects.firstOrNull()
            val activeFile = getActiveFile()
            ApiService.sendToApi(createActivityData(ActivityEvent.END, activeProject, activeFile))
        }
    }

    fun handleEditorChange(project: Project?, file: VirtualFile?) {
        DevTrackrLogger.log("Editor changed: ${file?.name ?: "none"}")
        lastActivity = System.currentTimeMillis()

        if (file != null && isTracking) {
            ApiService.sendToApi(createActivityData(ActivityEvent.SWITCH, project, file))
        } else if (file != null && !isTracking && apiKey.isNotBlank()) {
            startTracking()
        }
    }

    fun handleDocumentChange(document: Document) {
        val file = FileDocumentManager.getInstance().getFile(document)
        val project = getProjectForFile(file)
        
        DevTrackrLogger.log("Document changed: ${file?.name ?: "unknown"}")
        lastActivity = System.currentTimeMillis()

        if (!isTracking && apiKey.isNotBlank()) {
            DevTrackrLogger.log("Document changed while not tracking, starting tracking")
            startTracking()
        }
    }

    private fun getActiveFile(): VirtualFile? {
        val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return null
        val fileEditorManager = com.intellij.openapi.fileEditor.FileEditorManager.getInstance(project)
        val selectedFiles = fileEditorManager.selectedFiles
        return selectedFiles.firstOrNull()
    }

    private fun getProjectForFile(file: VirtualFile?): Project? {
        if (file == null) return null
        return ProjectManager.getInstance().openProjects.firstOrNull { project ->
            com.intellij.openapi.vfs.VfsUtil.isAncestor(project.baseDir ?: return@firstOrNull false, file, false)
        }
    }

    private fun startTimers() {
        // Check for idle state
        idleCheckTask = scheduler.scheduleAtFixedRate({
            val now = System.currentTimeMillis()
            if (isTracking && (now - lastActivity > IDLE_THRESHOLD)) {
                val activeProject = ProjectManager.getInstance().openProjects.firstOrNull()
                val activeFile = getActiveFile()
                ApiService.sendToApi(createActivityData(ActivityEvent.IDLE, activeProject, activeFile))
                isTracking = false
            }
        }, 60, 60, TimeUnit.SECONDS)

        // Send regular heartbeats for active periods
        heartbeatTask = scheduler.scheduleAtFixedRate({
            val now = System.currentTimeMillis()
            if (isTracking && (now - lastActivity < IDLE_THRESHOLD)) {
                val activeProject = ProjectManager.getInstance().openProjects.firstOrNull()
                val activeFile = getActiveFile()
                ApiService.sendToApi(createActivityData(ActivityEvent.ACTIVE, activeProject, activeFile))
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS)
    }

    fun dispose() {
        idleCheckTask?.cancel(false)
        heartbeatTask?.cancel(false)
        scheduler.shutdown()
        
        if (isTracking) {
            val activeProject = ProjectManager.getInstance().openProjects.firstOrNull()
            val activeFile = getActiveFile()
            ApiService.sendToApi(createActivityData(ActivityEvent.END, activeProject, activeFile))
        }
    }
}
