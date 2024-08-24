package com.fappslab.difflinescounter.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class ScheduleUseCase {
   suspend operator fun invoke(delaySeconds: Long, scheduleBlock: () -> Unit) {
      while (coroutineContext.isActive) {
         scheduleBlock()
         delay(TimeUnit.SECONDS.toMillis(delaySeconds))
      }
   }
}
