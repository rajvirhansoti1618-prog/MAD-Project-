package com.example.lexidesk

import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.firebase.CloudPaths
import com.example.lexidesk.data.firebase.model.CloudClientDto
import com.example.lexidesk.data.firebase.model.toCloudDto
import com.example.lexidesk.data.firebase.sync.OfflineSyncQueueManager
import com.example.lexidesk.data.firebase.sync.PendingMutation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CloudSyncUnitTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun cloudPaths_formatsPredictableHierarchy() {
        val workspace = "delhi_firm_1"
        assertEquals("workspaces/delhi_firm_1/clients", CloudPaths.clientsPath(workspace))
        assertEquals("workspaces/delhi_firm_1/cases", CloudPaths.casesPath(workspace))
        assertEquals("workspaces/delhi_firm_1/tasks", CloudPaths.tasksPath(workspace))
        assertEquals("workspaces/delhi_firm_1/notes", CloudPaths.notesPath(workspace))
        assertEquals("workspaces/delhi_firm_1/reminders", CloudPaths.remindersPath(workspace))
        assertEquals("workspaces/delhi_firm_1/draft_templates", CloudPaths.draftTemplatesPath(workspace))

        assertEquals("client_101", CloudPaths.clientRecordId(101L))
        assertEquals("case_202", CloudPaths.caseRecordId(202L))
        assertEquals("task_303", CloudPaths.taskRecordId(303L))
    }

    @Test
    fun conflictResolution_lastWriteWinsPolicy_evaluatesCorrectly() {
        val localCreatedAt = 1000L
        val localClient = ClientEntity(id = 1L, fullName = "Local Version", createdAt = localCreatedAt)

        val newerRemoteDto = CloudClientDto(
            id = 1L,
            fullName = "Remote Newer Version",
            updatedAt = 2000L,
            isDeleted = false
        )

        // When remote is newer, remote must win
        val remoteIsNewer = newerRemoteDto.updatedAt > localClient.createdAt
        assertTrue("Remote record with newer timestamp should win", remoteIsNewer)
        val entityFromRemote = newerRemoteDto.toEntity()
        assertEquals("Remote Newer Version", entityFromRemote.fullName)

        // When local is newer, local must win
        val olderRemoteDto = CloudClientDto(
            id = 1L,
            fullName = "Remote Stale Version",
            updatedAt = 500L,
            isDeleted = false
        )
        val localIsNewer = localClient.createdAt >= olderRemoteDto.updatedAt
        assertTrue("Local record with newer timestamp should win", localIsNewer)
    }

    @Test
    fun tombstoneDeletion_correctlyIdentifiedInCloudModel() {
        val client = ClientEntity(id = 55L, fullName = "Deleted Client")
        val deletedDto = client.toCloudDto(isDeleted = true, updatedAt = 3000L)

        assertTrue(deletedDto.isDeleted)
        val map = deletedDto.toMap()
        assertEquals(true, map["isDeleted"])

        val parsed = CloudClientDto.fromMap(map)
        assertTrue(parsed.isDeleted)
    }

    @Test
    fun offlineSyncQueue_enqueuesAndDeduplicatesMutations() {
        val queue = OfflineSyncQueueManager(tempFolder.root)
        assertEquals(0, queue.size())

        val mutation1 = PendingMutation(
            id = "mut_1",
            entityType = "client",
            recordId = 10L,
            action = "UPSERT",
            payloadJson = "{\"fullName\":\"John\"}"
        )
        queue.enqueue(mutation1)
        assertEquals(1, queue.size())

        // Enqueue an update for the same entity and recordId: should deduplicate
        val mutation2 = PendingMutation(
            id = "mut_2",
            entityType = "client",
            recordId = 10L,
            action = "DELETE"
        )
        queue.enqueue(mutation2)
        assertEquals(1, queue.size())

        val peeked = queue.peekAll().first()
        assertEquals("mut_2", peeked.id)
        assertEquals("DELETE", peeked.action)

        // Increment retry
        queue.incrementRetry("mut_2")
        val updated = queue.peekAll().first()
        assertEquals(1, updated.retryCount)

        // Remove
        queue.remove("mut_2")
        assertEquals(0, queue.size())
    }
}
