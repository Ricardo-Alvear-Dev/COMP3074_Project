
package ca.gbc.comp3074.devtrack_mobile

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.LocalDate

class DeadlineWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        NotificationHelper.createNotificationChannel(
            applicationContext
        )

        val tasks = TaskStorage.getTasks(applicationContext)

        val tomorrow = LocalDate.now()
            .plusDays(1)
            .toString()

        val preferences = applicationContext
            .getSharedPreferences(
                "DeadlineNotifications",
                Context.MODE_PRIVATE
            )

        tasks.forEach { task ->

            val notificationKey =
                "${task.id}_${task.dueDate}"

            val alreadyNotified = preferences.getBoolean(
                notificationKey,
                false
            )

            if (
                !task.isCompleted &&
                task.dueDate == tomorrow &&
                !alreadyNotified
            ) {

                if (androidx.core.app.NotificationManagerCompat
                        .from(applicationContext)
                        .areNotificationsEnabled()
                ) {
                    NotificationHelper.showDeadlineNotification(
                        applicationContext,
                        task
                    )

                    preferences.edit()
                        .putBoolean(notificationKey, true)
                        .apply()
                }
            }
        }

        return Result.success()
    }
}
