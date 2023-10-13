package com.fappslab.difflinescounter.data.source

import com.fappslab.difflinescounter.domain.model.DiffStat
import com.fappslab.difflinescounter.data.git.GitRepository
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.FileTreeIterator
import java.io.ByteArrayOutputStream

class DiffLinesCounterDataSourceJGitImpl : DiffLinesCounterDataSource {

    override fun query(basePath: String?): DiffStat? {
        return runCatching {
            val repository = GitRepository.provider(basePath)

            val head = repository?.resolve("HEAD")
            val walk = RevWalk(repository)
            val commit = walk.parseCommit(head)
            val tree = commit.tree

            val oldTree = CanonicalTreeParser()
            val reader = repository?.newObjectReader()
            oldTree.reset(reader, tree.id)

            val newTree = FileTreeIterator(repository)
            val diffFormatter = DiffFormatter(ByteArrayOutputStream())
            diffFormatter.setRepository(repository)
            val entries = diffFormatter.scan(oldTree, newTree)

            var totalChanges = 0
            var insertions = 0
            var deletions = 0

            for (entry in entries) {
                totalChanges++
                val editList = diffFormatter.toFileHeader(entry).toEditList()
                for (edit in editList) {
                    insertions += edit.lengthB
                    deletions += edit.lengthA
                }
            }

            DiffStat(totalChanges, insertions, deletions)
        }.getOrNull()
    }
}
