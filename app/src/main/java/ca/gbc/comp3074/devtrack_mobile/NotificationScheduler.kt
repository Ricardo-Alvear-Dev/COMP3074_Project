
package ca.gbc.comp3074.devtrack_mobile

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleDeadlineChecks(context: Context) {

        val request =
            PeriodicWorkRequestBuilder<DeadlineWorker>(
                1,
                TimeUnit.HOURS
            ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "deadline_checker",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    fun checkNow(context: Context) {

        val request =
            OneTimeWorkRequestBuilder<DeadlineWorker>()
                .build()

        WorkManager.getInstance(context)
            .enqueue(request)
    }
}
