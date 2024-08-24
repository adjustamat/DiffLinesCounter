package com.chriscarini.jetbrains.gitpushreminder.settings;

import com.chriscarini.jetbrains.gitpushreminder.messages.MyStrings;
import com.chriscarini.jetbrains.gitpushreminder.settings.SettingsManager.MySettings;
import com.fappslab.difflinescounter.presentation.StatusBarFactory;
import com.fappslab.difflinescounter.usecase.ScheduleReminder;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.NlsContexts.ConfigurableName;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Configurable} for Git Push Reminder plugin.
 */
public class SettingsConfigurable
 implements Configurable
{
private final JPanel mainPanel = new JBPanel<>();
private final JBCheckBox checkAllBranchesField = new JBCheckBox();
private final JBCheckBox countUntrackedBranchAsPushedField = new JBCheckBox();
private final JBCheckBox showClosePushDialogField = new JBCheckBox();
private final JBCheckBox showCloseCommitDialogField = new JBCheckBox();
private final JBCheckBox showStatusbarField = new JBCheckBox();
private final JBTextField intervalField = new IntegerField();
private final JBCheckBox showSwitchDialogField = new JBCheckBox();

public SettingsConfigurable()
{
   buildMainPanel();
}

private void buildMainPanel()
{
   mainPanel.setLayout(new VerticalFlowLayout(true, false));
   
   mainPanel.add(
    FormBuilder.createFormBuilder()
     .addLabeledComponent(MyStrings.get("settings.show.difflines.label"),
      showStatusbarField)
     .addTooltip(MyStrings.get("settings.show.difflines.tooltip"))
     .addSeparator()
     
     .addLabeledComponent(MyStrings.get("settings.show.reminder.label"),
      intervalField)
     .addTooltip(MyStrings.get("settings.show.reminder.tooltip"))
     .addSeparator()
     
     .addSeparator()
     
     .addLabeledComponent(MyStrings.get("settings.show.closing.uncommitted.dialog.label"),
      showCloseCommitDialogField)
     .addTooltip(MyStrings.get("settings.show.closing.uncommitted.dialog.tooltip"))
     .addSeparator()
     
     .addLabeledComponent(MyStrings.get("settings.show.closing.unpushed.dialog.label"),
      showClosePushDialogField)
     .addTooltip(MyStrings.get("settings.show.closing.unpushed.dialog.tooltip"))
     .addSeparator()
     
     .addSeparator()
     
     .addLabeledComponent(MyStrings.get("settings.show.switching.dialog.label"),
      showSwitchDialogField)
     .addTooltip(MyStrings.get("settings.show.switching.dialog.tooltip"))
     
     .addLabeledComponent(MyStrings.get("settings.check.all.branches.label"),
      checkAllBranchesField)
     .addTooltip(MyStrings.get("settings.check.all.branches.tooltip"))
     .addSeparator()
     
     .addLabeledComponent(MyStrings.get("settings.allow.untracked.branches.label"),
      countUntrackedBranchAsPushedField)
     .addTooltip(MyStrings.get("settings.allow.untracked.branches.tooltip"))
     .getPanel()
   );
}

@Nullable
@Override
public JComponent createComponent()
{
   // Set the user input field to contain the currently saved settings
   setUserInputFieldsFromSavedSettings();
   return mainPanel;
}

@Override
public boolean isModified()
{
   return !getSettingsFromUserInput().equals(getSettings());
}

/**
 * Apply the settings; saves the current user input list to the {@link SettingsManager}, and updates
 * the table.
 */
@Override
public void apply()
{
   final MySettings settingsState = getSettingsFromUserInput();
   SettingsManager.getInstance().loadState(settingsState);
   setUserInputFieldsFromSavedSettings();
   new StatusBarFactory().showOrHide();
   ScheduleReminder.Companion.startOrStop();
}

@NotNull
private SettingsManager.MySettings getSettingsFromUserInput()
{
   final MySettings settingsState = new MySettings();
   
   settingsState.checkAllBranches = checkAllBranchesField.isSelected();
   settingsState.countUntrackedBranchAsPushed = countUntrackedBranchAsPushedField.isSelected();
   settingsState.showClosePushDialog = showClosePushDialogField.isSelected();
   settingsState.showCloseCommitDialog = showCloseCommitDialogField.isSelected();
   settingsState.showSwitchDialog = showSwitchDialogField.isSelected();
   settingsState.showStatusbar = showStatusbarField.isSelected();
   try {
      settingsState.reminderIntervalMinutes = Integer.parseInt(intervalField.getText());
   }
   catch(NumberFormatException | NullPointerException ignored) {
      settingsState.reminderIntervalMinutes = 0;
   }
   return settingsState;
}

/**
 * Get the saved settings and update the user input field.
 */
private void setUserInputFieldsFromSavedSettings()
{
   updateUserInputFields(getSettings());
}

/**
 * Update the user input field based on the input value provided by {@code val}
 * @param settings
 *  The {@link MySettings} for the plugin.
 */
private void updateUserInputFields(
 @Nullable final SettingsManager.MySettings settings)
{
   if(settings == null) {
      return;
   }
   showStatusbarField.setSelected(settings.showStatusbar);
   intervalField.setText(String.valueOf(settings.reminderIntervalMinutes));
   checkAllBranchesField.setSelected(settings.checkAllBranches);
   countUntrackedBranchAsPushedField.setSelected(settings.countUntrackedBranchAsPushed);
   showCloseCommitDialogField.setSelected(settings.showCloseCommitDialog);
   showClosePushDialogField.setSelected(settings.showClosePushDialog);
   showSwitchDialogField.setSelected(settings.showSwitchDialog);
}

@NotNull
private SettingsManager.MySettings getSettings()
{
   return SettingsManager.getInstance().getState();
}

@Override
public @ConfigurableName String getDisplayName()
{
   return MyStrings.get("settings.displayname");
}
}
