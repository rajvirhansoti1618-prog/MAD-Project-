package com.example.lexidesk.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, 0)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "LexiDesk Reminder"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "You have a scheduled reminder."

        NotificationHelper.showReminderNotification(
            context = context,
            notificationId = reminderId,
            title = title,
            description = description
        )
    }

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_DESCRIPTION = "extra_description"
    }
}
