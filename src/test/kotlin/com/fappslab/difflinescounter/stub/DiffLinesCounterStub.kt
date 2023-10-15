package com.fappslab.difflinescounter.stub

import com.fappslab.difflinescounter.domain.model.DiffStat

fun diffStatStub() =
    DiffStat(totalChanges = 5, insertions = 3, deletions = 2)
