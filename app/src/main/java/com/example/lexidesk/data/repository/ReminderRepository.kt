package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.ReminderDao
import com.example.lexidesk.data.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {

    val allReminders: Flow<List<ReminderEntity>> = reminderDao.getAllReminders()

    fun getUpcomingReminders(currentTime: Long): Flow<List<ReminderEntity>> =
        reminderDao.getUpcomingReminders(currentTime)

    suspend fun getUpcomingRemindersOnce(currentTime: Long): List<ReminderEntity> =
        reminderDao.getUpcomingRemindersOnce(currentTime)

    fun getReminderById(id: Long): Flow<ReminderEntity?> = reminderDao.getReminderById(id)

    suspend fun getReminderByIdOnce(id: Long): ReminderEntity? = reminderDao.getReminderByIdOnce(id)

    fun getRemindersForDate(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>> =
        reminderDao.getRemindersForDate(startOfDay, endOfDay)

    suspend fun insertReminder(reminder: ReminderEntity): Long = reminderDao.insertReminder(reminder)

    suspend fun updateReminder(reminder: ReminderEntity) = reminderDao.updateReminder(reminder)

    suspend fun deleteReminder(reminder: ReminderEntity) = reminderDao.deleteReminder(reminder)

    suspend fun deleteReminderById(id: Long) = reminderDao.deleteReminderById(id)

    suspend fun setReminderCompleted(id: Long, completed: Boolean) =
        reminderDao.setReminderCompleted(id, completed)
}
