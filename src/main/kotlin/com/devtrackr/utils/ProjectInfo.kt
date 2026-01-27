package com.devtrackr.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

/**
 * Utility class to extract project information
 */
object ProjectInfo {
    fun getCurrentProjectName(project: Project?): String {
        return project?.name ?: "Unknown Project"
    }

    fun getCurrentFileName(file: VirtualFile?): String {
        return file?.name ?: "Unknown File"
    }

    fun getRelativeFilePath(project: Project?, file: VirtualFile?): String {
        if (project == null || file == null) return ""
        
        val projectBaseDir = project.basePath ?: return file.path
        val filePath = file.path
        
        return if (filePath.startsWith(projectBaseDir)) {
            filePath.substring(projectBaseDir.length).trimStart(File.separatorChar)
        } else {
            filePath
        }
    }

    fun getCurrentLanguage(file: VirtualFile?): String {
        return file?.extension ?: "unknown"
    }

    fun getCurrentGitBranch(project: Project?): String? {
        if (project == null) return null
        
        try {
            val gitUtil = Class.forName("git4idea.GitUtil")
            val getRepositoryManager = gitUtil.getMethod("getRepositoryManager", Project::class.java)
            val repositoryManager = getRepositoryManager.invoke(null, project)
            val repositories = repositoryManager?.javaClass?.getMethod("getRepositories")?.invoke(repositoryManager) as? Collection<*>
            
            if (repositories.isNullOrEmpty()) return null
            
            val repository = repositories.first()
            val currentBranchName = repository?.javaClass?.getMethod("getCurrentBranchName")?.invoke(repository) as? String
            return currentBranchName
        } catch (e: Exception) {
            // Git plugin not available
            return null
        }
    }

    fun getGitRepoUrl(project: Project?): String? {
        if (project == null) return null
        
        try {
            val gitUtil = Class.forName("git4idea.GitUtil")
            val getRepositoryManager = gitUtil.getMethod("getRepositoryManager", Project::class.java)
            val repositoryManager = getRepositoryManager.invoke(null, project)
            val repositories = repositoryManager?.javaClass?.getMethod("getRepositories")?.invoke(repositoryManager) as? Collection<*>
            
            if (repositories.isNullOrEmpty()) return null
            
            val repository = repositories.first()
            val remotes = repository?.javaClass?.getMethod("getRemotes")?.invoke(repository) as? Collection<*>
            val remote = remotes?.firstOrNull()
            val urls = remote?.javaClass?.getMethod("getUrls")?.invoke(remote) as? Collection<*>
            return urls?.firstOrNull() as? String
        } catch (e: Exception) {
            // Git plugin not available
            return null
        }
    }
}
