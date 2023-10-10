package com.fappslab.difflinescounter.interactor

import com.fappslab.difflinescounter.extension.orZero
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import java.io.File

private const val ACTION_ID = "ChangesView.Refresh"
private const val IS_INSIDE_WORK_TREE = "true"
private const val CHANGES_PATTERN =
    "\\d+ files? changed(?:, (\\d+) insertions?\\(\\+\\))?(?:, (\\d+) deletions?\\(-\\))?"

class GitDiffInteractor(private val repoDirectory: File) {

    private fun isGitRepository(): Boolean {
        return runCatching {
            val process = ProcessBuilder("git", "rev-parse", "--is-inside-work-tree")
                .directory(repoDirectory)
                .start()
            process.waitFor()
            process.inputStream.bufferedReader().use { it.readLine() }
        }.mapCatching { it == IS_INSIDE_WORK_TREE }
            .getOrDefault(defaultValue = false)
    }

    fun getCurrentBranch(): String? {
        return if (isGitRepository()) {
            runCatching {
                val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
                    .directory(repoDirectory)
                    .start()
                process.waitFor()
                process.inputStream.bufferedReader().use { it.readLine() }
            }.getOrNull()

        } else null
    }

    fun getChangedLines(currentBranch: String): String? {
        return runCatching {
            val process = ProcessBuilder("git", "diff", "--shortstat", currentBranch)
                .directory(repoDirectory)
                .start()
            process.waitFor()
            process.inputStream.bufferedReader().use { it.readText() }
        }.getOrNull()
    }

    fun createDataContext(project: Project?): DataContext {
        return DataContext { dataId ->
            if (CommonDataKeys.PROJECT.name == dataId) {
                project
            } else null
        }
    }

    fun getInteractionsPair(output: String): Pair<Int, Int> {
        return CHANGES_PATTERN.toRegex().find(output).run {
            this?.groups?.get(1)?.value?.toInt().orZero() to this?.groups?.get(2)?.value?.toInt().orZero()
        }
    }

    fun refreshChanges(dataContext: DataContext) {
        val action = ActionManager.getInstance().getAction(ACTION_ID) ?: return
        val anActionEvent = createAnActionEvent(dataContext)
        action.actionPerformed(anActionEvent)
    }

    private fun createAnActionEvent(dataContext: DataContext): AnActionEvent {
        val inputEvent = null
        val modifier = 0
        val place = ActionPlaces.UNKNOWN
        return AnActionEvent(inputEvent, dataContext, place, Presentation(), ActionManager.getInstance(), modifier)
    }
}
