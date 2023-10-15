package com.fappslab.difflinescounter.presentation

import com.fappslab.difflinescounter.domain.usecase.GetDiffStatUseCase
import com.fappslab.difflinescounter.domain.usecase.ScheduleUpdatesUseCase
import com.fappslab.difflinescounter.presentation.action.FileAction
import com.fappslab.difflinescounter.presentation.action.MouseAction
import com.fappslab.difflinescounter.extension.getRefreshActionManager
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionManager
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

class DiffStatusWidget(
    private val project: Project,
    private val component: DiffStatusLabel,
    private val getDiffStatUseCase: GetDiffStatUseCase,
    private val scheduleUpdatesUseCase: ScheduleUpdatesUseCase
) : CustomStatusBarWidget {

    private val job = Job()
    private val actionManager = ActionManager.getInstance()
    private val dataContext = DataManager.getInstance().getDataContext(component)
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
        ApplicationManager.getApplication().invokeLater {
            actionManager.getRefreshActionManager(dataContext)
        }
    }
}
