package com.example.lexidesk

import com.example.lexidesk.data.entity.CaseEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CaseEntityTest {

    @Test
    fun priorityWeight_returnsCorrectOrder() {
        val urgentCase = CaseEntity(title = "Urgent Injunction", priority = "Urgent")
        val highCase = CaseEntity(title = "High Priority Litigation", priority = "High")
        val mediumCase = CaseEntity(title = "Draft Advisory", priority = "Medium")
        val lowCase = CaseEntity(title = "Old Record Review", priority = "Low")

        assertEquals(4, urgentCase.priorityWeight)
        assertEquals(3, highCase.priorityWeight)
        assertEquals(2, mediumCase.priorityWeight)
        assertEquals(1, lowCase.priorityWeight)

        assertTrue(urgentCase.priorityWeight > highCase.priorityWeight)
        assertTrue(highCase.priorityWeight > mediumCase.priorityWeight)
        assertTrue(mediumCase.priorityWeight > lowCase.priorityWeight)
    }

    @Test
    fun priorityWeight_isCaseInsensitive() {
        val uppercaseCase = CaseEntity(title = "Urgent", priority = "URGENT")
        val lowercaseCase = CaseEntity(title = "High", priority = "high")

        assertEquals(4, uppercaseCase.priorityWeight)
        assertEquals(3, lowercaseCase.priorityWeight)
    }

    @Test
    fun priorityWeight_withUnknownPriority_returnsZero() {
        val unknownCase = CaseEntity(title = "Case", priority = "Custom")
        assertEquals(0, unknownCase.priorityWeight)
    }

    @Test
    fun caseSorting_byPriorityDescending_ordersFromUrgentToLow() {
        val cases = listOf(
            CaseEntity(title = "Low", priority = "Low"),
            CaseEntity(title = "Urgent", priority = "Urgent"),
            CaseEntity(title = "Medium", priority = "Medium"),
            CaseEntity(title = "High", priority = "High")
        )

        val sorted = cases.sortedByDescending { it.priorityWeight }
        assertEquals("Urgent", sorted[0].priority)
        assertEquals("High", sorted[1].priority)
        assertEquals("Medium", sorted[2].priority)
        assertEquals("Low", sorted[3].priority)
    }
}
