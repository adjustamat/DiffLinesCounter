package com.fappslab.difflinescounter.presentation.action

import com.fappslab.difflinescounter.domain.model.ActionPlacesType
import com.fappslab.difflinescounter.extension.refreshChangesActions
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager

class RefreshAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        ApplicationManager.getApplication().invokeLater {
            event.dataContext.refreshChangesActions(
                placeType = ActionPlacesType.KeyboardShortcut,
                inputEvent = event.inputEvent,
                modifiers = event.modifiers
            )
        }
    }
}
