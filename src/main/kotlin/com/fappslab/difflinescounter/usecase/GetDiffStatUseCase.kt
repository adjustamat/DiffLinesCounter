package com.fappslab.difflinescounter.usecase

import com.fappslab.difflinescounter.model.DiffStat
import com.fappslab.difflinescounter.data.GitDiffDataSource

class GetDiffStatUseCase(
 private val repository: GitDiffDataSource
                        ) {
   suspend operator fun invoke(basePath: String?): DiffStat? =
    repository.queryDiffStat(basePath)
}
