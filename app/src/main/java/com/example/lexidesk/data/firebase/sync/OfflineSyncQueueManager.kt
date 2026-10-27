package com.example.lexidesk.data.firebase.sync

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

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
    private val context: Context
) {
    private val tag = "OfflineSyncQueue"
    private val queueFile: File by lazy {
        File(context.filesDir, "pending_sync_mutations.json")
    }

    @Synchronized
    fun enqueue(mutation: PendingMutation) {
        val current = loadQueueInternal().toMutableList()
        // Deduplicate mutations for the same entity and recordId: keep the latest action
        current.removeAll { it.entityType == mutation.entityType && it.recordId == mutation.recordId }
        current.add(mutation)
        saveQueueInternal(current)
        Log.d(tag, "Enqueued offline mutation: ${mutation.entityType}_${mutation.recordId} (${mutation.action})")
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
            val content = queueFile.readText()
            if (content.isBlank()) return emptyList()
            val array = JSONArray(content)
            val list = mutableListOf<PendingMutation>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    PendingMutation(
                        id = obj.getString("id"),
                        entityType = obj.getString("entityType"),
                        recordId = obj.getLong("recordId"),
                        action = obj.getString("action"),
                        payloadJson = obj.optString("payloadJson", "{}"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        retryCount = obj.optInt("retryCount", 0)
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.w(tag, "Failed reading queue file: ${e.message}")
            emptyList()
        }
    }

    private fun saveQueueInternal(list: List<PendingMutation>) {
        try {
            val array = JSONArray()
            for (m in list) {
                val obj = JSONObject().apply {
                    put("id", m.id)
                    put("entityType", m.entityType)
                    put("recordId", m.recordId)
                    put("action", m.action)
                    put("payloadJson", m.payloadJson)
                    put("timestamp", m.timestamp)
                    put("retryCount", m.retryCount)
                }
                array.put(obj)
            }
            queueFile.writeText(array.toString(2))
        } catch (e: Exception) {
            Log.w(tag, "Failed saving queue file: ${e.message}")
        }
    }
}
