package com.fappslab.difflinescounter.data

import com.fappslab.difflinescounter.model.DiffStat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

class GitDiffDataSource(
 private val executor: ProcessExecutor = ProcessExecutor(),
 private val dispatcher: CoroutineDispatcher = Dispatchers.IO
                       ) {
   companion object {
      fun provideRepository(basePath: String?): Repository? {
         return runCatching {
            val gitDir = File("$basePath/.git")
            
            require(gitDir.exists() && gitDir.isDirectory) {"Invalid Git directory"}
            
            val repository = FileRepositoryBuilder()
             .setGitDir(gitDir)
             .build()
            
            requireNotNull(repository.resolve("HEAD")) {"Invalid repository: HEAD not found."}
            
            repository
         }.getOrNull()
      }
      
      private val CHANGES_PATTERN =
       """(\d+)\s+files? changed(?:,\s+(\d+)\s+insertions?\(\+\))?(?:,\s+(\d+)\s+deletions?\(-\))?""".toRegex()
   }
   
   private fun toBoolean(fromGit: String?): Boolean? {
      return fromGit?.trim()?.let {
         val matchResult = CHANGES_PATTERN.find(it)
         
         val (files, insertions, deletions) = matchResult?.destructured ?: return null
         val iInss = insertions.toIntOrNull()
         val iDels = deletions.toIntOrNull()
         if (iInss == null || iDels == null)
            return false;
         return iInss > 0 || iDels > 0
      }
   }
   
   private fun toDiffStat(fromGit: String?): DiffStat? {
      return fromGit?.trim()?.let {
         val matchResult = CHANGES_PATTERN.find(it)
         
         val (files, insertions, deletions) = matchResult?.destructured ?: return null
         
         DiffStat(files.toIntOrNull(), insertions.toIntOrNull(), deletions.toIntOrNull())
      }
   }
   
   private fun getGitDiffData(basePath: String?): String? {
      val directory = basePath?.let(::File)
      
      val diffProcess = executor.run(directory, "git", "diff", "HEAD", "--stat")
      
      return diffProcess.inputStream.bufferedReader().useLines {it.lastOrNull()}
   }
   
   suspend fun queryNotZero(basePath: String?): Boolean? {
      return withContext(dispatcher) {
         runCatching {
            val changes = getGitDiffData(basePath)
            toBoolean(changes)
         }.getOrNull()
      }
   }
   
   suspend fun queryDiffStat(basePath: String?): DiffStat? {
      return withContext(dispatcher) {
         runCatching {
            val changes = getGitDiffData(basePath)
            toDiffStat(changes)
         }.getOrNull()
      }
   }
}
