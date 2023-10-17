package com.fappslab.difflinescounter.presentation

import com.fappslab.difflinescounter.data.repository.DiffLinesCounterRepositoryImpl
import com.fappslab.difflinescounter.data.service.ProcessExecutorImpl
import com.fappslab.difflinescounter.data.source.DiffLinesCounterDataSourceCmdImpl
import com.fappslab.difflinescounter.domain.repository.DiffLinesCounterRepository
import com.fappslab.difflinescounter.domain.usecase.GetDiffStatUseCase
import com.fappslab.difflinescounter.domain.usecase.ScheduleUpdatesUseCase
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

const val PLUGIN_NAME = "DiffLinesCounter"

class StatusBarFactory : StatusBarWidgetFactory {

    override fun getId(): String {
        return PLUGIN_NAME
    }

    override fun getDisplayName(): String {
        return PLUGIN_NAME
    }

    override fun isAvailable(project: Project): Boolean {
        // GitRepository.provider(project.basePath).isNotNull()
        return true
    }

    override fun createWidget(project: Project): StatusBarWidget {
        val repository = provideRepository()
        return DiffStatusWidget(
            project = project,
            component = DiffStatusLabel(),
            getDiffStatUseCase = GetDiffStatUseCase(repository),
            scheduleUpdatesUseCase = ScheduleUpdatesUseCase()
        )
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
            dataSource = DiffLinesCounterDataSourceCmdImpl(
                executor = ProcessExecutorImpl()
            )
        )
    }
}
