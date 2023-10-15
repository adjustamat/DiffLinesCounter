package com.fappslab.difflinescounter.data.repository

import com.fappslab.difflinescounter.data.service.ProcessExecutor
import com.fappslab.difflinescounter.data.source.DiffLinesCounterDataSourceCmdImpl
import com.fappslab.difflinescounter.stub.diffStatStub
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class DiffLinesCounterRepositoryImplIntegrationTest {

    private val executor = mockk<ProcessExecutor>()
    private val subject = DiffLinesCounterRepositoryImpl(
        DiffLinesCounterDataSourceCmdImpl(executor)
    )

    @Test
    fun `getDiffStat Should return DiffStat When invoke query`() {
        runTest {
            // Given
            val expectedDiffStat = diffStatStub()
            every { executor.run(any(), "git", "diff", "HEAD", "--stat") } returns getSuccessProcess()

            // When
            val result = subject.query(basePath = "/mock/basePath")

            // Then
            assertEquals(expectedDiffStat, result)
            verify { executor.run(any(), "git", "diff", "HEAD", "--stat") }
        }
    }

    @Test
    fun `getDiffStat Should return null When IOException occurs`() {
        runTest {
            // Given
            val expectedResult = null
            every { executor.run(any(), "git", "diff", "HEAD", "--stat") } returns getFailureProcess()

            // When
            val result = subject.query(basePath = "/mock/basePath")

            // Then
            assertEquals(expectedResult, result)
            verify { executor.run(any(), "git", "diff", "HEAD", "--stat") }
        }
    }
}
