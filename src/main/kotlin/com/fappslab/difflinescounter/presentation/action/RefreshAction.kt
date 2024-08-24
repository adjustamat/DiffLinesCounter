package com.fappslab.difflinescounter.presentation.action

import com.fappslab.difflinescounter.model.ActionPlace
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager

class RefreshAction: AnAction() {
   override fun actionPerformed(event: AnActionEvent) {
      ApplicationManager.getApplication().invokeLater {
         ActionPlace.KeyboardShortcut.doRefreshChanges(
          event.dataContext,
          event.inputEvent,
          event.modifiers)
      }
   }
}
