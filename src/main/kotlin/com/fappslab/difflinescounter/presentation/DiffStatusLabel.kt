package com.fappslab.difflinescounter.presentation

import com.fappslab.difflinescounter.domain.model.DiffStat
import com.fappslab.difflinescounter.extension.orZero
import com.intellij.openapi.util.IconLoader
import javax.swing.JLabel

private const val ICON_PATH = "AllIcons.Actions.Refresh"
private const val TOOLTIP_FORMAT = "%d files changed, %d insertions(+), %d deletions(-)"

private const val TEXT_FORMAT = "%d(+%d : -%d)"
// private const val TEXT_FORMAT =
//    "<html>%d(<font color='#499C54'>%d↑</font> : <font color='#FF6347'>%d↓</font>)</html>"

class DiffStatusLabel : JLabel() {

    init {
        icon = IconLoader.getIcon(ICON_PATH, this::class.java)
        showChanges(null)
    }

    fun showChanges(diffStat: DiffStat?) {
        val statOrZero = diffStat.getOrZero()

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
        return TEXT_FORMAT.format(totalChanges, insertions, deletions)
    }
}
