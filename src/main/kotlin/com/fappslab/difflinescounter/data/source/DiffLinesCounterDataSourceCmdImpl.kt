package com.fappslab.difflinescounter.data.source

import com.fappslab.difflinescounter.data.model.toDiffStat
import com.fappslab.difflinescounter.domain.model.DiffStat
import java.io.File

class DiffLinesCounterDataSourceCmdImpl : DiffLinesCounterDataSource {

    override fun query(basePath: String?): DiffStat? {
        return runCatching {
            val process = ProcessBuilder("git", "diff", "HEAD", "--stat")
                .directory(basePath?.let(::File))
                .start()
            //process.waitFor()

            val changes = process.inputStream.bufferedReader().useLines { it.lastOrNull() }
            changes.toDiffStat()
        }.getOrNull()
    }
}
