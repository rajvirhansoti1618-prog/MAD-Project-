package com.example.lexidesk

import com.example.lexidesk.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun formatDate_returnsFormattedString() {
        val timestamp = 1776240000000L // Specific timestamp
        val formatted = DateUtils.formatDate(timestamp)
        assertNotNull(formatted)
        assertFalse(formatted == "Not set")
    }

    @Test
    fun formatDate_withNull_returnsNotSet() {
        val formatted = DateUtils.formatDate(null)
        assertEquals("Not set", formatted)
    }

    @Test
    fun isToday_withCurrentTime_returnsTrue() {
        val now = System.currentTimeMillis()
        assertTrue(DateUtils.isToday(now))
    }

    @Test
    fun isToday_withPastDate_returnsFalse() {
        val past = System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000L)
        assertFalse(DateUtils.isToday(past))
    }

    @Test
    fun isOverdue_withOldPastTimestamp_returnsTrue() {
        val past = System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000L)
        assertTrue(DateUtils.isOverdue(past))
    }

    @Test
    fun isOverdue_withFutureTimestamp_returnsFalse() {
        val future = System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000L)
        assertFalse(DateUtils.isOverdue(future))
    }

    @Test
    fun daysUntil_withFutureTimestamp_returnsPositiveDays() {
        val now = 1000000000L
        val target = now + (3 * 24 * 60 * 60 * 1000L)
        assertEquals(3L, DateUtils.daysUntil(target, now))
    }

    @Test
    fun daysUntil_withNullTimestamp_returnsNull() {
        assertEquals(null, DateUtils.daysUntil(null))
    }

    @Test
    fun isWithinDays_withTimestampInsideWindow_returnsTrue() {
        val now = 1000000000L
        val target = now + (2 * 24 * 60 * 60 * 1000L) // 2 days in future
        assertTrue(DateUtils.isWithinDays(target, days = 3, fromTime = now))
    }

    @Test
    fun isWithinDays_withTimestampOutsideWindow_returnsFalse() {
        val now = 1000000000L
        val target = now + (5 * 24 * 60 * 60 * 1000L) // 5 days in future
        assertFalse(DateUtils.isWithinDays(target, days = 3, fromTime = now))
    }

    @Test
    fun isWithinDays_withPastTimestampOrZeroDays_returnsFalse() {
        val now = 1000000000L
        val past = now - 1000L
        assertFalse(DateUtils.isWithinDays(past, days = 3, fromTime = now))
        assertFalse(DateUtils.isWithinDays(now + 1000L, days = 0, fromTime = now))
        assertFalse(DateUtils.isWithinDays(null, days = 3, fromTime = now))
    }

    @Test
    fun isSameDay_withSameCalendarDay_returnsTrue() {
        val cal = Calendar.getInstance()
        val morning = cal.apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
        }.timeInMillis
        val evening = cal.apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 30)
        }.timeInMillis

        assertTrue(DateUtils.isSameDay(morning, evening))
    }

    @Test
    fun isSameDay_withDifferentDaysOrNull_returnsFalse() {
        val cal = Calendar.getInstance()
        val today = cal.timeInMillis
        val tomorrow = cal.apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis

        assertFalse(DateUtils.isSameDay(today, tomorrow))
        assertFalse(DateUtils.isSameDay(today, null))
        assertFalse(DateUtils.isSameDay(null, today))
        assertFalse(DateUtils.isSameDay(null, null))
    }
}
