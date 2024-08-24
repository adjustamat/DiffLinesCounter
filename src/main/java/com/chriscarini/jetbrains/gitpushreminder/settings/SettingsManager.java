package com.chriscarini.jetbrains.gitpushreminder.settings;

import java.util.Objects;
import com.chriscarini.jetbrains.gitpushreminder.settings.SettingsManager.MySettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Settings for this plugin will be stored in and read from {@code gitReminder.xml}.
 */
@State(name="gitReminder", storages=@Storage(value="gitReminder.xml"))
public class SettingsManager
 implements PersistentStateComponent<MySettings>
{
private MySettings persistentSettings;
public static Project project;

public static SettingsManager getInstance()
{
   return ApplicationManager.getApplication().getService(SettingsManager.class);
}

@NotNull
@Override
public SettingsManager.MySettings getState()
{
   if(persistentSettings == null) {
      persistentSettings = new MySettings();
   }
   return persistentSettings;
}

@Override
public void loadState(@NotNull final SettingsManager.MySettings mySettings)
{
   persistentSettings = mySettings;
}

public static class MySettings
{
   public boolean checkAllBranches;
   public boolean countUntrackedBranchAsPushed;
   public boolean showClosePushDialog;
   public boolean showCloseCommitDialog;
   public boolean showStatusbar;
   public int reminderIntervalMinutes;
   public boolean showSwitchDialog;
   
   public MySettings()
   {
      this.checkAllBranches = false;
      this.countUntrackedBranchAsPushed = false;
      this.showClosePushDialog = true;
      this.showCloseCommitDialog = true;
      this.showStatusbar = true;
      this.reminderIntervalMinutes = 0;
      this.showSwitchDialog = false;
   }
   
   @Override
   public boolean equals(Object o)
   {
      if(this == o) return true;
      if(o == null || getClass() != o.getClass()) return false;
      MySettings other = (MySettings) o;
      return checkAllBranches == other.checkAllBranches &&
              countUntrackedBranchAsPushed == other.countUntrackedBranchAsPushed &&
              showClosePushDialog == other.showClosePushDialog &&
              showCloseCommitDialog == other.showCloseCommitDialog &&
              showStatusbar == other.showStatusbar &&
              reminderIntervalMinutes == other.reminderIntervalMinutes &&
              showSwitchDialog == other.showSwitchDialog;
   }
   
   @Override
   public int hashCode()
   {
      return Objects.hash(checkAllBranches, countUntrackedBranchAsPushed, showClosePushDialog,
       showCloseCommitDialog, showSwitchDialog, showStatusbar, reminderIntervalMinutes);
   }
}
}
