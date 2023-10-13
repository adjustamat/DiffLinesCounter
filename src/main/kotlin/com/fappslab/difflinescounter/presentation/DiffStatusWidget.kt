package com.fappslab.difflinescounter.presentation

import com.fappslab.difflinescounter.domain.model.DiffStat
import com.fappslab.difflinescounter.domain.usecase.GetDiffStatUseCase
import com.fappslab.difflinescounter.extension.orZero
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.swing.JComponent
import javax.swing.JLabel

private const val DELAY = 60L
private const val ACTION_ID = "ChangesView.Refresh"
private const val ICON_PATH = "AllIcons.Actions.Refresh"
private const val TOOLTIP_FORMAT = "%d files changed, %d insertions(+), %d deletions(-)"
private const val TEXT_FORMAT = "changes: %d(+%d|-%d)"

class DiffStatusWidget(
    private val project: Project,
    private val getDiffStatUseCase: GetDiffStatUseCase
) : CustomStatusBarWidget, Runnable, MouseListener {

    private val component = Component()
    private val schedulePool = Executors.newScheduledThreadPool(1)
    private val pendingPool = Executors.newFixedThreadPool(1)

    init {
        component.addMouseListener(this)
        schedulePool.scheduleWithFixedDelay(this, DELAY, DELAY, TimeUnit.SECONDS)
        fileChangeTracker { pendingPool.submit(this::updateWidget) }
    }

    private fun fileChangeTracker(runnable: Runnable) {
        project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                runnable.run()
            }
        })
    }

    override fun getComponent(): JComponent {
        return component
    }

    override fun ID(): String {
        return this::class.java.name
    }

    override fun install(statusBar: StatusBar) {}

    override fun dispose() {
        schedulePool.shutdown()
    }

    override fun run() {
        runCatching { updateWidget() }
            .onFailure { it.printStackTrace() }
    }

    private fun updateWidget() {
        component.showChanges(stat = getDiff())
    }

    override fun mouseClicked(e: MouseEvent?) {
        ApplicationManager.getApplication()
            .invokeLater(this::refreshChanges)
    }

    override fun mousePressed(e: MouseEvent?) {}

    override fun mouseReleased(e: MouseEvent?) {}

    override fun mouseEntered(e: MouseEvent?) {}

    override fun mouseExited(e: MouseEvent?) {}

    private fun getDiff(): DiffStat? {
        return runCatching { getDiffStatUseCase(project.basePath) }
            .onFailure { it.printStackTrace() }
            .getOrNull()
    }

    private fun refreshChanges() {
        ApplicationManager.getApplication().runWriteAction {
            val dataContext = DataManager.getInstance().getDataContext(component)
            val action = ActionManager.getInstance().getAction(ACTION_ID)
            action.actionPerformed(createAnActionEvent(dataContext))
        }
    }

    private fun createAnActionEvent(dataContext: DataContext): AnActionEvent {
        val inputEvent = null
        val modifier = 0
        val place = ActionPlaces.UNKNOWN
        return AnActionEvent(inputEvent, dataContext, place, Presentation(), ActionManager.getInstance(), modifier)
    }

    private inner class Component : JLabel() {

        init {
            setIcon(IconLoader.getIcon(ICON_PATH, this::class.java))
            showChanges(null)
        }

        fun showChanges(stat: DiffStat?) {
            val statOrZero = stat.getOrZero()

            toolTipText = statOrZero.formatForTooltip()
            text = statOrZero.formatForText()
        }

        private fun DiffStat?.getOrZero(): DiffStat {
            return DiffStat(
                totalChanges = this?.totalChanges.orZero(),
                insertions = this?.insertions.orZero(),
                deletions = this?.deletions.orZero()
            )
        }

        private fun DiffStat.formatForTooltip(): String {
            return String.format(TOOLTIP_FORMAT, totalChanges, insertions, deletions)
        }

        private fun DiffStat.formatForText(): String {
            val totalChanges = insertions.orZero() + deletions.orZero()
            return String.format(TEXT_FORMAT, totalChanges, insertions, deletions)
        }
    }
}
