package com.fappslab.difflinescounter.model

import com.chriscarini.jetbrains.gitpushreminder.messages.MyStrings

data class DiffStat(
 var files: Int?,
 var insertions: Int?,
 var deletions: Int?
                   ) {
   fun formatForTooltip(): String {
      return MyStrings.get("difflines.tooltipformat", files, insertions, deletions)
   }
   
   fun formatForStatusLabel(): String {
      return MyStrings.get("difflines.statusformat", files, insertions, deletions)
   }
}
