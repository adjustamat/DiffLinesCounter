package com.fappslab.difflinescounter.presentation

import com.fappslab.difflinescounter.data.GitDiffDataSource
import com.fappslab.difflinescounter.model.ActionPlace
import com.fappslab.difflinescounter.presentation.action.FileAction
import com.fappslab.difflinescounter.presentation.action.MouseAction
import com.fappslab.difflinescounter.usecase.GetDiffStatUseCase
import com.fappslab.difflinescounter.usecase.ScheduleUseCase
import com.intellij.ide.DataManager
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

private const val REFRESH_DELAY_SECONDS = 30L

class DiffStatusWidget(
 private val project: Project,
 private val component: DiffStatusLabel = DiffStatusLabel(),
 private val getDiffStat: GetDiffStatUseCase = GetDiffStatUseCase(GitDiffDataSource()),
 private val scheduleUpdates: ScheduleUseCase = ScheduleUseCase()
                      ): CustomStatusBarWidget {
   
   private val job = Job()
   private val coroutineScope = CoroutineScope(Dispatchers.Default + job)
   private val connection = project.messageBus.connect(this)
   private val mouseAction = MouseAction(::clickedOrScheduledRefresh)
   
   init {
      setupFilesChangeTracker()
      setupScheduleUpdates()
      setupMouseListener()
   }
   
   override fun getComponent(): JComponent {
      return component
   }
   
   override fun ID(): String {
      return ID
   }
   
   override fun dispose() {
      component.removeMouseListener(mouseAction)
      connection.disconnect()
      job.cancel()
   }
   
   override fun install(statusBar: StatusBar) {}
   
   private fun setupFilesChangeTracker() {
      connection.subscribe(
       VirtualFileManager.VFS_CHANGES,
       FileAction {
          coroutineScope.launch {
             val diffStat = getDiffStat(project.basePath)
             component.showChanges(diffStat)
          }
       })
   }
   
   private fun setupMouseListener() {
      component.addMouseListener(mouseAction)
   }
   
   private fun setupScheduleUpdates() {
      coroutineScope.launch {
         scheduleUpdates(REFRESH_DELAY_SECONDS, ::clickedOrScheduledRefresh)
      }
   }
   
   private fun clickedOrScheduledRefresh() {
      if (GitDiffDataSource.provideRepository(project.basePath) == null) {
         // if there is no git repository, show nothing:
         component.showChanges(diffStat = null)
      }
      else {
         ApplicationManager.getApplication().invokeLater {
            val dataContext = DataManager.getInstance().getDataContext(component)
            ActionPlace.StatusBar.doRefreshChanges(dataContext)
         }
      }
   }
}
