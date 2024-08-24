package com.fappslab.difflinescounter.usecase

import com.chriscarini.jetbrains.gitpushreminder.messages.MyStrings
import com.chriscarini.jetbrains.gitpushreminder.settings.SettingsManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class ScheduleReminder {
   suspend operator fun invoke(delayMinutes: Int) {
      while (coroutineContext.isActive) {
         delay(TimeUnit.MINUTES.toMillis(delayMinutes.toLong()))
         
         val noti = Notification("adjustamat.gitreminder", "Git Reminder",
          MyStrings.get("noti.reminder.uncommitted"), NotificationType.INFORMATION)
         
         if (SettingsManager.project == null) Notifications.Bus.notify(noti)
         else Notifications.Bus.notify(noti, SettingsManager.project)
      }
   }
   
   companion object {
      private val job = Job()
      private val coroutineScope = CoroutineScope(Dispatchers.Default + job)
      
      fun startOrStop() {
         coroutineScope.launch {
            val settings = SettingsManager.getInstance().state
            
            if (settings.reminderIntervalMinutes == 0) {
               if (job.isActive)
                  job.cancel()
            }
            else {
               if (!job.isActive)
                  ScheduleReminder().invoke(settings.reminderIntervalMinutes)
            }
         }
      }
   }
}