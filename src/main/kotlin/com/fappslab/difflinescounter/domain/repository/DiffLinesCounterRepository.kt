package com.fappslab.difflinescounter.domain.repository

import com.fappslab.difflinescounter.domain.model.DiffStat

interface DiffLinesCounterRepository {
    fun query(basePath: String?): DiffStat?
}
