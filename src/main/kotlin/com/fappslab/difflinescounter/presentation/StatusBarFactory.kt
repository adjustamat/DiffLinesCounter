package com.fappslab.difflinescounter.presentation

import com.fappslab.difflinescounter.data.git.GitRepository
import com.fappslab.difflinescounter.data.repository.DiffLinesCounterRepositoryImpl
import com.fappslab.difflinescounter.data.source.DiffLinesCounterDataSourceCmdImpl
import com.fappslab.difflinescounter.domain.repository.DiffLinesCounterRepository
import com.fappslab.difflinescounter.domain.usecase.GetDiffStatUseCase
import com.fappslab.difflinescounter.extension.orFalse
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

const val DISPLAY_NAME = "DiffLinesCounter"

class StatusBarFactory : StatusBarWidgetFactory {

    override fun getId(): String {
        return this::class.java.name
    }

    override fun getDisplayName(): String {
        return DISPLAY_NAME
    }

    override fun isAvailable(project: Project): Boolean {
        return runCatching {
            GitRepository.provider(project.basePath)
        }.getOrNull().orFalse()
    }

    override fun createWidget(project: Project): StatusBarWidget {
        return DiffStatusWidget(project, GetDiffStatUseCase(provideRepository()))
    }

    override fun disposeWidget(widget: StatusBarWidget) {
        Disposer.dispose(widget)
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        return true
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }

    private fun provideRepository(): DiffLinesCounterRepository {
        return DiffLinesCounterRepositoryImpl(
            dataSource = DiffLinesCounterDataSourceCmdImpl()
        )
    }
}
