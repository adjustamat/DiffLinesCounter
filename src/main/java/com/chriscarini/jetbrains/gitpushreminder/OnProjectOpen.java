package com.chriscarini.jetbrains.gitpushreminder;

import com.chriscarini.jetbrains.gitpushreminder.settings.SettingsManager;
import com.fappslab.difflinescounter.usecase.ScheduleReminder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OnProjectOpen
 implements ProjectActivity
{
@Nullable
@Override
public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation)
{
   SettingsManager.project = project;
   ScheduleReminder.Companion.startOrStop();
   return null;
}
}