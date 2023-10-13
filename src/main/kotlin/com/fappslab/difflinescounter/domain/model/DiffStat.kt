package com.fappslab.difflinescounter.domain.model

data class DiffStat(
    var totalChanges: Int?,
    var insertions: Int?,
    var deletions: Int?
)
