package com.example.lexidesk

import com.example.lexidesk.data.entity.TaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskEntityTest {

    @Test
    fun priorityWeight_returnsCorrectOrder() {
        val urgentTask = TaskEntity(title = "Urgent Hearing", priority = "Urgent")
        val highTask = TaskEntity(title = "High Priority Filing", priority = "High")
        val mediumTask = TaskEntity(title = "Review Contract", priority = "Medium")
        val lowTask = TaskEntity(title = "Archive File", priority = "Low")

        assertEquals(4, urgentTask.priorityWeight)
        assertEquals(3, highTask.priorityWeight)
        assertEquals(2, mediumTask.priorityWeight)
        assertEquals(1, lowTask.priorityWeight)

        assertTrue(urgentTask.priorityWeight > highTask.priorityWeight)
        assertTrue(highTask.priorityWeight > mediumTask.priorityWeight)
        assertTrue(mediumTask.priorityWeight > lowTask.priorityWeight)
    }

    @Test
    fun priorityWeight_isCaseInsensitive() {
        val uppercaseTask = TaskEntity(title = "Urgent", priority = "URGENT")
        val lowercaseTask = TaskEntity(title = "High", priority = "high")

        assertEquals(4, uppercaseTask.priorityWeight)
        assertEquals(3, lowercaseTask.priorityWeight)
    }

    @Test
    fun priorityWeight_withUnknownPriority_returnsZero() {
        val unknownTask = TaskEntity(title = "Draft", priority = "Unknown")
        assertEquals(0, unknownTask.priorityWeight)
    }

    @Test
    fun taskSorting_byPriorityDescending_ordersFromUrgentToLow() {
        val tasks = listOf(
            TaskEntity(title = "Low", priority = "Low"),
            TaskEntity(title = "Urgent", priority = "Urgent"),
            TaskEntity(title = "Medium", priority = "Medium"),
            TaskEntity(title = "High", priority = "High")
        )

        val sorted = tasks.sortedByDescending { it.priorityWeight }

        assertEquals("Urgent", sorted[0].priority)
        assertEquals("High", sorted[1].priority)
        assertEquals("Medium", sorted[2].priority)
        assertEquals("Low", sorted[3].priority)
    }
}
