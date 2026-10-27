package com.example.lexidesk

import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.DraftTemplateEntity
import com.example.lexidesk.data.entity.NoteEntity
import com.example.lexidesk.data.entity.ReminderEntity
import com.example.lexidesk.data.entity.TaskEntity
import com.example.lexidesk.data.firebase.model.CloudCaseDto
import com.example.lexidesk.data.firebase.model.CloudClientDto
import com.example.lexidesk.data.firebase.model.CloudDraftTemplateDto
import com.example.lexidesk.data.firebase.model.CloudNoteDto
import com.example.lexidesk.data.firebase.model.CloudReminderDto
import com.example.lexidesk.data.firebase.model.CloudTaskDto
import com.example.lexidesk.data.firebase.model.toCloudDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudModelMappingTest {

    @Test
    fun clientEntity_toCloudDto_and_toMap_and_back_preservesData() {
        val client = ClientEntity(
            id = 42L,
            fullName = "Advocate Rajesh Sharma",
            clientType = "Business",
            phone = "+91 9876543210",
            email = "rajesh@example.com",
            address = "High Court Chambers, Room 12",
            companyName = "Apex Legal LLP",
            notes = "Retainer agreement signed",
            tags = "corporate, active",
            createdAt = 1700000000000L
        )

        val cloudDto = client.toCloudDto(updatedAt = 1700000500000L)
        assertEquals(42L, cloudDto.id)
        assertEquals("Advocate Rajesh Sharma", cloudDto.fullName)

        val map = cloudDto.toMap()
        assertEquals(42L, map["id"])
        assertEquals("Apex Legal LLP", map["companyName"])
        assertFalse(map["isDeleted"] as Boolean)

        val restoredDto = CloudClientDto.fromMap(map)
        val restoredEntity = restoredDto.toEntity()

        assertEquals(client.id, restoredEntity.id)
        assertEquals(client.fullName, restoredEntity.fullName)
        assertEquals(client.companyName, restoredEntity.companyName)
        assertEquals(client.phone, restoredEntity.phone)
        assertEquals(client.email, restoredEntity.email)
    }

    @Test
    fun caseEntity_roundTripMapping_preservesForeignKeysAndNullableFields() {
        val caseEntity = CaseEntity(
            id = 101L,
            clientId = 42L,
            title = "State vs Enterprise Corp",
            category = "Criminal Defence",
            description = "Bail hearing preparation",
            referenceNumber = "BAIL/2026/884",
            priority = "Urgent",
            status = "In Progress",
            startDate = 1710000000000L,
            dueDate = 1710500000000L,
            courtOrLocation = "Courtroom 3B",
            notes = "File urgent affidavit",
            createdAt = 1709000000000L,
            updatedAt = 1709100000000L
        )

        val cloudDto = caseEntity.toCloudDto()
        val map = cloudDto.toMap()
        val restored = CloudCaseDto.fromMap(map).toEntity()

        assertEquals(caseEntity.id, restored.id)
        assertEquals(caseEntity.clientId, restored.clientId)
        assertEquals(caseEntity.title, restored.title)
        assertEquals(caseEntity.priority, restored.priority)
        assertEquals(caseEntity.dueDate, restored.dueDate)
    }

    @Test
    fun taskEntity_roundTripMapping_handlesNullablesAndTombstones() {
        val task = TaskEntity(
            id = 7L,
            title = "Draft Written Statement",
            description = "Respond to paragraph 14-22",
            clientId = 42L,
            caseId = 101L,
            priority = "High",
            status = "Pending",
            dueDate = 1712000000000L,
            dueTime = "11:30 AM",
            reminderEnabled = true,
            createdAt = 1711000000000L
        )

        val cloudDto = task.toCloudDto(isDeleted = true)
        assertTrue(cloudDto.isDeleted)

        val map = cloudDto.toMap()
        val restoredDto = CloudTaskDto.fromMap(map)
        assertTrue(restoredDto.isDeleted)

        val restoredEntity = restoredDto.toEntity()
        assertEquals(task.title, restoredEntity.title)
        assertEquals(task.dueTime, restoredEntity.dueTime)
        assertTrue(restoredEntity.reminderEnabled)
    }

    @Test
    fun noteAndReminderAndDraft_serialization_handlesEmptyMapsSafely() {
        val emptyClientDto = CloudClientDto.fromMap(emptyMap())
        assertEquals(0L, emptyClientDto.id)
        assertEquals("", emptyClientDto.fullName)

        val note = NoteEntity(id = 5L, clientId = 1L, title = "Evidence list", content = "Marked A through D")
        val noteMap = note.toCloudDto().toMap()
        val restoredNote = CloudNoteDto.fromMap(noteMap).toEntity()
        assertEquals(note.title, restoredNote.title)

        val reminder = ReminderEntity(id = 9L, title = "Hearing reminder", eventTimestamp = 1720000000000L)
        val reminderMap = reminder.toCloudDto().toMap()
        val restoredReminder = CloudReminderDto.fromMap(reminderMap).toEntity()
        assertEquals(reminder.title, restoredReminder.title)

        val draft = DraftTemplateEntity(id = 3L, templateType = "NDA", title = "Standard NDA", content = "Non-disclosure terms")
        val draftMap = draft.toCloudDto().toMap()
        val restoredDraft = CloudDraftTemplateDto.fromMap(draftMap).toEntity()
        assertEquals(draft.title, restoredDraft.title)
    }
}
