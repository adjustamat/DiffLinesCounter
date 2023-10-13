package com.fappslab.difflinescounter.domain.usecase

import com.fappslab.difflinescounter.domain.repository.DiffLinesCounterRepository
import com.fappslab.difflinescounter.domain.model.DiffStat

class GetDiffStatUseCase(
    private val repository: DiffLinesCounterRepository
) {

    operator fun invoke(basePath: String?): DiffStat? =
        repository.query(basePath)
}
