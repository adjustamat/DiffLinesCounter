package com.fappslab.difflinescounter

import com.fappslab.difflinescounter.interactor.GitDiffInteractor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import java.io.File

class ShowDiffAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val projectDir = event.project?.basePath ?: System.getProperty("user.dir")
        val interactor = GitDiffInteractor(File(projectDir))

        val dataContext = interactor.createDataContext(event.project)
        interactor.refreshChanges(dataContext)

        val currentBranch = interactor.getCurrentBranch()

        if (currentBranch == null) {
            Messages.showInfoMessage(
                "Please initialize your project as a Git repository to use this feature.",
                "Git Initialization Required"
            )
            return
        }

        val changedLines = interactor.getChangedLines(currentBranch)
        changedLines?.let { output ->
            val (insertions, deletions) = interactor.getInteractionsPair(output)
            showInfo(output, insertions, deletions)
        }
    }

    private fun showInfo(output: String, insertions: Int, deletions: Int) {
        val totalChanges = insertions + deletions
        val message = """
            $output
            Total changes: $totalChanges
        """.trimIndent()

        Messages.showInfoMessage(message, "Diff Lines Counter")
    }
}
