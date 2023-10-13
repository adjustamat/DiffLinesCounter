package com.fappslab.difflinescounter.data.source

import com.fappslab.difflinescounter.domain.model.DiffStat

interface DiffLinesCounterDataSource {
    fun query(basePath: String?): DiffStat?
}
