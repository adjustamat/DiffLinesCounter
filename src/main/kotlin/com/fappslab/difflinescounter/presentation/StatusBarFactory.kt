package com.fappslab.difflinescounter.presentation

import com.chriscarini.jetbrains.gitpushreminder.settings.SettingsManager
import com.fappslab.difflinescounter.data.GitDiffDataSource
import com.fappslab.difflinescounter.usecase.GetDiffStatUseCase
import com.fappslab.difflinescounter.usecase.ScheduleUseCase
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

const val PLUGIN_NAME = "DiffLinesCounter"
const val ID = "com.fappslab.difflinescounter.presentation.StatusBarFactory"

class StatusBarFactory: StatusBarWidgetFactory {
   override fun getId(): String {
      return ID
   }
   
   override fun getDisplayName(): String {
      return PLUGIN_NAME
   }
   
   override fun isAvailable(project: Project): Boolean {
      // GitDiffDataSource.provideRepository(project.basePath) != null
      val settings = SettingsManager.getInstance().state
      return settings.showStatusbar
   }
   
   override fun createWidget(project: Project): StatusBarWidget {
      return DiffStatusWidget(project)
   }
   
   override fun disposeWidget(widget: StatusBarWidget) {
      Disposer.dispose(widget)
   }
   
   override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
      return true
   }
   
   override fun isEnabledByDefault(): Boolean {
      return true
   }
   
   fun showOrHide() {
      StatusBarWidgetsManager(SettingsManager.project,
       CoroutineScope(Dispatchers.Default)).updateWidget(this)
   }
}
