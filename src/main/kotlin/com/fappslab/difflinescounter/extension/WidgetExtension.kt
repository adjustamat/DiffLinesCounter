package com.fappslab.difflinescounter.extension

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.Presentation

private const val ACTION_ID = "ChangesView.Refresh"

fun ActionManager.getRefreshActionManager(dataContext: DataContext) {
    getAction(ACTION_ID).actionPerformed(
        AnActionEvent(null, dataContext, ActionPlaces.UNKNOWN, Presentation(), this, 0)
    )
}
