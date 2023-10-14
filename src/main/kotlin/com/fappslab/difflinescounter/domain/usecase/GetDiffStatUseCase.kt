package com.fappslab.difflinescounter.domain.usecase

import com.fappslab.difflinescounter.domain.model.DiffStat
import com.fappslab.difflinescounter.domain.repository.DiffLinesCounterRepository

class GetDiffStatUseCase(
    private val repository: DiffLinesCounterRepository
) {

    suspend operator fun invoke(basePath: String?): DiffStat? =
        repository.query(basePath)
}
