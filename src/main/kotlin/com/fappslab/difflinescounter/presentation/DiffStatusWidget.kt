package com.fappslab.difflinescounter.presentation

import com.fappslab.difflinescounter.data.git.GitRepository
import com.fappslab.difflinescounter.domain.usecase.GetDiffStatUseCase
import com.fappslab.difflinescounter.domain.usecase.ScheduleUpdatesUseCase
import com.fappslab.difflinescounter.extension.isNull
import com.fappslab.difflinescounter.presentation.action.FileAction
import com.fappslab.difflinescounter.presentation.action.MouseAction
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.swing.JComponent

private const val REFRESH_DELAY = 30L
private const val ACTION_ID = "ChangesView.Refresh"

class DiffStatusWidget(
    private val project: Project,
    private val component: DiffStatusLabel,
    private val getDiffStatUseCase: GetDiffStatUseCase,
    private val scheduleUpdatesUseCase: ScheduleUpdatesUseCase
) : CustomStatusBarWidget {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + job)
    private val connection = project.messageBus.connect(this)
    private val mouseAction = MouseAction(::refreshChangesAction)

    init {
        setupFilesChangeTracker()
        setupScheduleUpdates()
        setupMouseListener()
    }

    override fun getComponent(): JComponent {
        return component
    }

    override fun ID(): String {
        return PLUGIN_NAME
    }

    override fun dispose() {
        component.removeMouseListener(mouseAction)
        connection.disconnect()
        job.cancel()
    }

    override fun install(statusBar: StatusBar) {}

    private fun setupMouseListener() {
        component.addMouseListener(mouseAction)
    }

    private fun setupScheduleUpdates() {
        coroutineScope.launch {
            scheduleUpdatesUseCase(REFRESH_DELAY, ::refreshChangesAction)
        }
    }

    private fun setupFilesChangeTracker() {
        connection.subscribe(
            VirtualFileManager.VFS_CHANGES, FileAction {
                coroutineScope.launch {
                    val diffStat = getDiffStatUseCase(project.basePath)
                    component.showChanges(diffStat)
                }
            }
        )
    }

    private fun refreshChangesAction() {
        if (GitRepository.provider(project.basePath).isNull()) {
            component.showChanges(diffStat = null)
        } else {
            ApplicationManager.getApplication().invokeLater {
                val actionManager = ActionManager.getInstance()
                val dataContext = DataManager.getInstance().getDataContext(component)
                actionManager.getAction(ACTION_ID).actionPerformed(
                    AnActionEvent(
                        null,
                        dataContext,
                        ActionPlaces.UNKNOWN,
                        Presentation(),
                        actionManager,
                        0
                    )
                )
            }
        }
    }
}
