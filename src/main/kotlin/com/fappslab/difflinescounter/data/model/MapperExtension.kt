package com.fappslab.difflinescounter.data.model

import com.fappslab.difflinescounter.domain.model.DiffStat

private val CHANGES_PATTERN =
    """(\d+)\s+files? changed(?:,\s+(\d+)\s+insertions?\(\+\))?(?:,\s+(\d+)\s+deletions?\(-\))?""".toRegex()

fun String?.toDiffStat(): DiffStat? {
    return this?.trim()?.let {
        val matchResult = CHANGES_PATTERN.find(it)
        val (totalChanges, insertions, deletions) = matchResult?.destructured ?: return null
        DiffStat(totalChanges.toIntOrNull(), insertions.toIntOrNull(), deletions.toIntOrNull())
    }
}
