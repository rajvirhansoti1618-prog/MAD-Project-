package com.example.lexidesk.data.model

import java.time.Instant

/**
 * Represents a single activity entry related to a client.
 * The sealed class allows type‑safe handling of the different activity kinds.
 */
sealed class ClientActivity {
    abstract val timestamp: Instant
    abstract val description: String

    data class TaskActivity(
        override val timestamp: Instant,
        override val description: String,
        val taskId: Long
    ) : ClientActivity()

    data class CaseActivity(
        override val timestamp: Instant,
        override val description: String,
        val caseId: Long
    ) : ClientActivity()

    data class DocumentActivity(
        override val timestamp: Instant,
        override val description: String,
        val documentId: Long
    ) : ClientActivity()

    data class NoteActivity(
        override val timestamp: Instant,
        override val description: String,
        val noteId: Long
    ) : ClientActivity()
}
