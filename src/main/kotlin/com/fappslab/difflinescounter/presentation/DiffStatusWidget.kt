package com.fappslab.difflinescounter.presentation

import com.fappslab.difflinescounter.domain.usecase.GetDiffStatUseCase
import com.fappslab.difflinescounter.domain.usecase.ScheduleUpdatesUseCase
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JComponent

private const val ACTION_ID = "ChangesView.Refresh"
private const val REFRESH_DELAY = 60L

class DiffStatusWidget(
    private val project: Project,
    private val component: DiffStatusLabel,
    private val getDiffStatUseCase: GetDiffStatUseCase,
    private val scheduleUpdatesUseCase: ScheduleUpdatesUseCase
) : CustomStatusBarWidget {

    private val job = Job()
    private val presentation = Presentation()
    private val actionManager = ActionManager.getInstance()
    private val dataContext = DataManager.getInstance().getDataContext(component)
    private val coroutineScope = CoroutineScope(context = Dispatchers.Default + job)

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
        job.cancel()
    }

    override fun install(statusBar: StatusBar) {}

    private fun setupMouseListener() {
        component.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {
                refreshChangesAction()
            }

            // :ISP:?
            override fun mousePressed(e: MouseEvent?) {}
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
            // :ISP:?
        })
    }

    private fun setupScheduleUpdates() {
        coroutineScope.launch {
            scheduleUpdatesUseCase(delaySeconds = REFRESH_DELAY) {
                refreshChangesAction()
                println("<L> setupScheduleUpdates")
            }
        }
    }

    private fun setupFilesChangeTracker() {
        project.messageBus.connect().subscribe(
            VirtualFileManager.VFS_CHANGES,
            object : BulkFileListener {
                override fun after(events: MutableList<out VFileEvent>) {
                    coroutineScope.launch {
                        val diffStat = getDiffStatUseCase(project.basePath)
                        component.showChanges(diffStat)
                    }
                }
            }
        )
    }

    private fun refreshChangesAction() {
        ApplicationManager.getApplication().invokeLater {
            actionManager.getAction(ACTION_ID).actionPerformed(
                AnActionEvent(
                    null,
                    dataContext,
                    ActionPlaces.UNKNOWN,
                    presentation,
                    actionManager,
                    0
                )
            )
        }
    }
}
