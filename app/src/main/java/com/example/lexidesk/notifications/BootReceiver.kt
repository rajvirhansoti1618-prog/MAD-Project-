package com.example.lexidesk.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.lexidesk.LexiDeskApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val app = context.applicationContext as? LexiDeskApp ?: return
            val reminderRepository = app.reminderRepository

            CoroutineScope(Dispatchers.IO).launch {
                val now = System.currentTimeMillis()
                val upcoming = reminderRepository.getUpcomingRemindersOnce(now)
                for (reminder in upcoming) {
                    ReminderScheduler.scheduleReminder(context, reminder)
                }
            }
        }
    }
}
