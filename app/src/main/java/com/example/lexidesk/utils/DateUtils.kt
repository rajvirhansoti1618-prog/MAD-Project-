package com.example.lexidesk.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatDate(timestamp: Long?): String {
        if (timestamp == null || timestamp == 0L) return "Not set"
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long?): String {
        if (timestamp == null || timestamp == 0L) return "Not set"
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long?): String {
        if (timestamp == null || timestamp == 0L) return "Not set"
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun isToday(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }

    fun isOverdue(timestamp: Long?): Boolean {
        if (timestamp == null) return false
        return timestamp < System.currentTimeMillis() && !isToday(timestamp)
    }

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    /**
     * Calculates the number of days remaining until the target timestamp from current time.
     * Returns negative values if the deadline has already passed.
     */
    fun daysUntil(timestamp: Long?, fromTime: Long = System.currentTimeMillis()): Long? {
        if (timestamp == null) return null
        val diffMillis = timestamp - fromTime
        return diffMillis / (24 * 60 * 60 * 1000L)
    }

    /**
     * Checks if a target timestamp falls within the upcoming number of specified days.
     */
    fun isWithinDays(timestamp: Long?, days: Int, fromTime: Long = System.currentTimeMillis()): Boolean {
        if (timestamp == null || days <= 0) return false
        val maxTime = fromTime + (days * 24 * 60 * 60 * 1000L)
        return timestamp in fromTime..maxTime
    }

    /**
     * Checks if two epoch millisecond timestamps fall on the exact same calendar day.
     */
    fun isSameDay(timestamp1: Long?, timestamp2: Long?): Boolean {
        if (timestamp1 == null || timestamp2 == null) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Formats a timestamp into a relative day representation ("Today", "Tomorrow", "Yesterday"),
     * or defaults to standard formatted date if outside immediate range.
     */
    fun getRelativeDayString(timestamp: Long?, fromTime: Long = System.currentTimeMillis()): String {
        if (timestamp == null || timestamp == 0L) return "Not set"
        val targetCal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val baseCal = Calendar.getInstance().apply {
            timeInMillis = fromTime
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffDays = ((targetCal.timeInMillis - baseCal.timeInMillis) / (24 * 60 * 60 * 1000L))

        return when (diffDays) {
            0L -> "Today"
            1L -> "Tomorrow"
            -1L -> "Yesterday"
            else -> formatDate(timestamp)
        }
    }
}
