package com.fappslab.difflinescounter.data.source

import com.fappslab.difflinescounter.data.model.toDiffStat
import com.fappslab.difflinescounter.data.service.ProcessExecutor
import com.fappslab.difflinescounter.domain.model.DiffStat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DiffLinesCounterDataSourceCmdImpl(
    private val executor: ProcessExecutor,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DiffLinesCounterDataSource {

    override suspend fun query(basePath: String?): DiffStat? {
        return withContext(dispatcher) {
            runCatching {
                val directory = basePath?.let(::File)
                val diffProcess = executor.run(directory, "git", "diff", "HEAD", "--stat")

                val changes = diffProcess.inputStream.bufferedReader().useLines { it.lastOrNull() }
                changes.toDiffStat()
            }.getOrNull()
        }
    }
}
