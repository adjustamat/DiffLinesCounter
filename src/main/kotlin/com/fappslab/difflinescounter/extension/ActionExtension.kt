package com.fappslab.difflinescounter.extension

import com.fappslab.difflinescounter.domain.model.ActionPlacesType
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.Presentation
import java.awt.event.InputEvent

private const val ACTION_ID = "ChangesView.Refresh"

fun DataContext.refreshChangesActions(
    placeType: ActionPlacesType,
    inputEvent: InputEvent? = null,
    modifiers: Int = 0
) {
    val actionManager = ActionManager.getInstance()
    val action = actionManager.getAction(ACTION_ID)
    val newEvent = AnActionEvent(
        inputEvent,
        this,
        placeType.place,
        Presentation(),
        actionManager,
        modifiers
    )
    action?.actionPerformed(newEvent)
}
