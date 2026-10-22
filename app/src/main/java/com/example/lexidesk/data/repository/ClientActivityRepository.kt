package com.example.lexidesk.data.repository

import com.example.lexidesk.data.model.ClientActivity
import com.example.lexidesk.data.repository.TaskRepository
import com.example.lexidesk.data.repository.CaseRepository
import com.example.lexidesk.data.repository.DocumentRepository
import com.example.lexidesk.data.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant

/**
 * Aggregates various client‑related entities into a chronological activity list.
 */
class ClientActivityRepository(
    private val taskRepository: TaskRepository,
    private val caseRepository: CaseRepository,
    private val documentRepository: DocumentRepository,
    private val noteRepository: NoteRepository
) {
    /**
     * Returns a Flow emitting a sorted list of activities for the given client.
     */
    fun getClientActivity(clientId: Long): Flow<List<ClientActivity>> =
        combine(
            taskRepository.getTasksByClient(clientId),
            caseRepository.getCasesByClient(clientId),
            documentRepository.getDocumentsByClient(clientId),
            noteRepository.getNotesByClient(clientId)
        ) { tasks, cases, documents, notes ->
            val activityList = mutableListOf<ClientActivity>()
            tasks.forEach { t ->
                activityList.add(
                    ClientActivity.TaskActivity(
                        timestamp = Instant.ofEpochMilli(t.createdAt),
                        description = "Task: ${t.title}",
                        taskId = t.id
                    )
                )
            }
            cases.forEach { c ->
                activityList.add(
                    ClientActivity.CaseActivity(
                        timestamp = Instant.ofEpochMilli(c.createdAt),
                        description = "Case: ${c.title}",
                        caseId = c.id
                    )
                )
            }
            documents.forEach { d ->
                activityList.add(
                    ClientActivity.DocumentActivity(
                        timestamp = Instant.ofEpochMilli(d.createdAt),
                        description = "Document: ${d.title}",
                        documentId = d.id
                    )
                )
            }
            notes.forEach { n ->
                activityList.add(
                    ClientActivity.NoteActivity(
                        timestamp = Instant.ofEpochMilli(n.timestamp),
                        description = "Note: ${n.title}",
                        noteId = n.id
                    )
                )
            }
            // Sort newest first
            activityList.sortByDescending { it.timestamp }
            activityList
        }
}
