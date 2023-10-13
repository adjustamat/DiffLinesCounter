package com.fappslab.difflinescounter.data.repository

import com.fappslab.difflinescounter.data.source.DiffLinesCounterDataSource
import com.fappslab.difflinescounter.domain.repository.DiffLinesCounterRepository
import com.fappslab.difflinescounter.domain.model.DiffStat

class DiffLinesCounterRepositoryImpl(
    private val dataSource: DiffLinesCounterDataSource
) : DiffLinesCounterRepository {

    override fun query(basePath: String?): DiffStat? =
        dataSource.query(basePath)
}
