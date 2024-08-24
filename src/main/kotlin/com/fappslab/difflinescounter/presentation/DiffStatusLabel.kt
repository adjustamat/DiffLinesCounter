package com.fappslab.difflinescounter.presentation

import com.fappslab.difflinescounter.model.DiffStat
import com.intellij.openapi.util.IconLoader
import javax.swing.JLabel

private const val REFRESH_ICON = "AllIcons.Actions.Refresh"
// private const val TEXT_FORMAT =
//    "<html>%d(<font color='#499C54'>%d↑</font> : <font color='#FF6347'>%d↓</font>)</html>"

class DiffStatusLabel: JLabel() {
   init {
      icon = IconLoader.getIcon(REFRESH_ICON, this::class.java)
      showChanges(null)
   }
   
   fun showChanges(diffStat: DiffStat?) {
      val statOrZero = diffStat.getOrZero()
      
      toolTipText = statOrZero.formatForTooltip()
      
      text = statOrZero.formatForStatusLabel()
   }
}

private fun DiffStat?.getOrZero(): DiffStat {
   return DiffStat(
    files = this?.files.orZero(),
    insertions = this?.insertions.orZero(),
    deletions = this?.deletions.orZero())
}

fun Int?.orZero(): Int = this ?: 0
