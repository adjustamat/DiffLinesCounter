package com.chriscarini.jetbrains.gitpushreminder;

import java.util.List;
import java.util.stream.Collectors;
import com.chriscarini.jetbrains.gitpushreminder.GitHelper.RepositoryAndBranch;
import com.chriscarini.jetbrains.gitpushreminder.messages.MyStrings;
import com.chriscarini.jetbrains.gitpushreminder.settings.SettingsManager;
import com.chriscarini.jetbrains.gitpushreminder.settings.SettingsManager.MySettings;
import com.fappslab.difflinescounter.data.GitDiffDataSource;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectCloseHandler;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class OnProjectClose
 implements ProjectCloseHandler
{

@Override
public boolean canClose(@NotNull Project project)
{
   MySettings mySettings = SettingsManager.getInstance().getState();
   
   // only show dialog if one of the settings is on
   if(!mySettings.showClosePushDialog && !mySettings.showCloseCommitDialog)
      return true;
   
   
   
   final List<RepositoryAndBranch> branchesWithUnpushedCommits =
    GitHelper.getBranchesWithUnpushedCommits(project, mySettings.checkAllBranches);
   
   GitDiffDataSource gitDiffDataSource = new GitDiffDataSource();
   // TODO
   // also TODO: make new description on github and somewhere else.
   
   // If there are NO branches with outgoing/un-pushed commits, then we can close.
   if(branchesWithUnpushedCommits.isEmpty())
      return true;
   
   
   
   final int dialogResult = Messages.showOkCancelDialog(project,
    MyStrings.get("dialog.closing.unpushed.textbody_branches",
     branchesWithUnpushedCommits.stream()
      .sorted((o1, o2) -> {
         if(o1.repository().getRoot().getName().equals(o2.repository().getRoot().getName())) {
            return o1.branch().getName().compareTo(o2.branch().getName());
         }
         return o1.repository().getRoot().getName().compareTo(o2.repository().getRoot().getName());
      })
      .map(repoAndBranch -> String.format(
        "%s (%s)", //NON-NLS
        repoAndBranch.branch().getName(),
        repoAndBranch.repository().getRoot().getName()
       )
      )
      .collect(Collectors.joining("</li><li>"))
    ),
    MyStrings.get("dialog.closing.unpushed.title_commits", branchesWithUnpushedCommits.size()),
    MyStrings.get("dialog.closing.button.close"),
    MyStrings.get("dialog.closing.button.keepopen"),
    Messages.getWarningIcon()
   );
   
   return dialogResult == MessageConstants.OK;
}
}
