package com.example.lexidesk.data.firebase.sync

import android.content.Context
import java.io.File
import java.util.Base64

data class PendingMutation(
    val id: String,
    val entityType: String, // "client", "case", "task", "note", "draft"
    val recordId: Long,
    val action: String, // "UPSERT", "DELETE"
    val payloadJson: String = "{}",
    val timestamp: Long = System.currentTimeMillis(),
    val retryCount: Int = 0
)

class OfflineSyncQueueManager(
    private val storageDirectory: File
) {
    constructor(context: Context) : this(context.filesDir)

    private val queueFile: File by lazy {
        File(storageDirectory, "pending_sync_mutations.txt")
    }

    @Synchronized
    fun enqueue(mutation: PendingMutation) {
        val current = loadQueueInternal().toMutableList()
        // Deduplicate mutations for the same entity and recordId: keep the latest action
        current.removeAll { it.entityType == mutation.entityType && it.recordId == mutation.recordId }
        current.add(mutation)
        saveQueueInternal(current)
    }

    @Synchronized
    fun peekAll(): List<PendingMutation> {
        return loadQueueInternal()
    }

    @Synchronized
    fun remove(mutationId: String) {
        val current = loadQueueInternal().toMutableList()
        current.removeAll { it.id == mutationId }
        saveQueueInternal(current)
    }

    @Synchronized
    fun incrementRetry(mutationId: String) {
        val current = loadQueueInternal().map {
            if (it.id == mutationId) it.copy(retryCount = it.retryCount + 1) else it
        }
        saveQueueInternal(current)
    }

    @Synchronized
    fun size(): Int = loadQueueInternal().size

    private fun loadQueueInternal(): List<PendingMutation> {
        if (!queueFile.exists()) return emptyList()
        return try {
            val lines = queueFile.readLines()
            lines.mapNotNull { line ->
                val parts = line.split("|", limit = 7)
                if (parts.size >= 7) {
                    val decodedPayload = try {
                        String(Base64.getDecoder().decode(parts[6]), Charsets.UTF_8)
                    } catch (_: Exception) {
                        "{}"
                    }
                    PendingMutation(
                        id = parts[0],
                        entityType = parts[1],
                        recordId = parts[2].toLongOrNull() ?: 0L,
                        action = parts[3],
                        timestamp = parts[4].toLongOrNull() ?: System.currentTimeMillis(),
                        retryCount = parts[5].toIntOrNull() ?: 0,
                        payloadJson = decodedPayload
                    )
                } else null
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveQueueInternal(list: List<PendingMutation>) {
        try {
            val content = list.joinToString("\n") { m ->
                val encodedPayload = Base64.getEncoder().encodeToString(m.payloadJson.toByteArray(Charsets.UTF_8))
                "${m.id}|${m.entityType}|${m.recordId}|${m.action}|${m.timestamp}|${m.retryCount}|$encodedPayload"
            }
            queueFile.writeText(content)
        } catch (_: Exception) {
            // Ignored in headless/offline
        }
    }
}
