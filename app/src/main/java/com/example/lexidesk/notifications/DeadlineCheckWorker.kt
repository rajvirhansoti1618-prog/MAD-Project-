package com.example.lexidesk.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.lexidesk.LexiDeskApp
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class DeadlineCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? LexiDeskApp ?: return Result.success()
        val now = System.currentTimeMillis()
        val oneDayLater = now + (24 * 60 * 60 * 1000L)

        // Check overdue or upcoming tasks
        val overdueTasks = app.taskRepository.getOverdueTasksCount(now).firstOrNull() ?: 0
        if (overdueTasks > 0) {
            NotificationHelper.showReminderNotification(
                context = applicationContext,
                notificationId = 9901,
                title = "Attention: $overdueTasks Overdue Task(s)",
                description = "You have $overdueTasks pending task(s) past due date. Review them in LexiDesk.",
                channelId = NotificationHelper.DEADLINE_CHANNEL_ID
            )
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "lexidesk_deadline_checker"

        fun schedulePeriodicCheck(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<DeadlineCheckWorker>(
                repeatInterval = 12,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
