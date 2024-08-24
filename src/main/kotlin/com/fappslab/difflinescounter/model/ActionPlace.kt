package com.fappslab.difflinescounter.model

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.Presentation
import java.awt.event.InputEvent

private const val REFRESH_CHANGES_ACTION_ID = "ChangesView.Refresh"

sealed class ActionPlace(private val place: String) {
   data object StatusBar: ActionPlace(ActionPlaces.STATUS_BAR_PLACE)
   data object KeyboardShortcut: ActionPlace(ActionPlaces.KEYBOARD_SHORTCUT)
//   data object Unknown: ActionPlacesType(ActionPlaces.UNKNOWN)
   
   fun doRefreshChanges(
    dataContext: DataContext,
    inputEvent: InputEvent? = null,
    modifiers: Int = 0) {
      val actionManager = ActionManager.getInstance()
      val action = actionManager.getAction(REFRESH_CHANGES_ACTION_ID)
      val newEvent = AnActionEvent(
       inputEvent,
       dataContext,
       this.place,
       Presentation(),
       actionManager,
       modifiers)
      action?.actionPerformed(newEvent)
   }
}
