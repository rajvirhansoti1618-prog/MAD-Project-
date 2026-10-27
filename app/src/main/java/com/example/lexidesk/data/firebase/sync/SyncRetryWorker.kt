package com.example.lexidesk.data.firebase.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.firebase.CloudPaths
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SyncRetryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val tag = "SyncRetryWorker"

    override suspend fun doWork(): Result {
        val app = applicationContext as? LexiDeskApp ?: return Result.success()
        val queueManager = OfflineSyncQueueManager(applicationContext)
        val cloudDataSource = app.cloudDataSource

        val pending = queueManager.peekAll()
        if (pending.isEmpty()) {
            return Result.success()
        }

        Log.i(tag, "Attempting to flush ${pending.size} pending offline mutations")
        var hasFailures = false

        for (mutation in pending) {
            try {
                val workspaceId = CloudPaths.DEFAULT_WORKSPACE_ID
                when (mutation.entityType) {
                    "client" -> {
                        val path = CloudPaths.clientsPath(workspaceId)
                        val id = CloudPaths.clientRecordId(mutation.recordId)
                        if (mutation.action == "DELETE") {
                            cloudDataSource.updateFields(path, id, mapOf("id" to mutation.recordId, "isDeleted" to true, "updatedAt" to mutation.timestamp))
                        } else {
                            val payloadMap = jsonToMap(mutation.payloadJson)
                            cloudDataSource.putRecord(path, id, payloadMap)
                        }
                    }
                    "case" -> {
                        val path = CloudPaths.casesPath(workspaceId)
                        val id = CloudPaths.caseRecordId(mutation.recordId)
                        if (mutation.action == "DELETE") {
                            cloudDataSource.updateFields(path, id, mapOf("id" to mutation.recordId, "isDeleted" to true, "updatedAt" to mutation.timestamp))
                        } else {
                            val payloadMap = jsonToMap(mutation.payloadJson)
                            cloudDataSource.putRecord(path, id, payloadMap)
                        }
                    }
                    "task" -> {
                        val path = CloudPaths.tasksPath(workspaceId)
                        val id = CloudPaths.taskRecordId(mutation.recordId)
                        if (mutation.action == "DELETE") {
                            cloudDataSource.updateFields(path, id, mapOf("id" to mutation.recordId, "isDeleted" to true, "updatedAt" to mutation.timestamp))
                        } else {
                            val payloadMap = jsonToMap(mutation.payloadJson)
                            cloudDataSource.putRecord(path, id, payloadMap)
                        }
                    }
                    "note" -> {
                        val path = CloudPaths.notesPath(workspaceId)
                        val id = CloudPaths.noteRecordId(mutation.recordId)
                        if (mutation.action == "DELETE") {
                            cloudDataSource.updateFields(path, id, mapOf("id" to mutation.recordId, "isDeleted" to true, "updatedAt" to mutation.timestamp))
                        } else {
                            val payloadMap = jsonToMap(mutation.payloadJson)
                            cloudDataSource.putRecord(path, id, payloadMap)
                        }
                    }
                    "draft" -> {
                        val path = CloudPaths.draftTemplatesPath(workspaceId)
                        val id = CloudPaths.draftTemplateRecordId(mutation.recordId)
                        if (mutation.action == "DELETE") {
                            cloudDataSource.updateFields(path, id, mapOf("id" to mutation.recordId, "isDeleted" to true, "updatedAt" to mutation.timestamp))
                        } else {
                            val payloadMap = jsonToMap(mutation.payloadJson)
                            cloudDataSource.putRecord(path, id, payloadMap)
                        }
                    }
                }
                queueManager.remove(mutation.id)
                Log.d(tag, "Successfully synced pending mutation ${mutation.id}")
            } catch (e: Exception) {
                Log.w(tag, "Failed syncing mutation ${mutation.id}: ${e.message}")
                queueManager.incrementRetry(mutation.id)
                hasFailures = true
            }
        }

        return if (hasFailures) Result.retry() else Result.success()
    }

    private fun jsonToMap(jsonString: String): Map<String, Any?> {
        val json = JSONObject(jsonString)
        val map = mutableMapOf<String, Any?>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = json.opt(key)
        }
        return map
    }

    companion object {
        private const val WORK_NAME = "lexidesk_cloud_sync_retry"

        fun schedulePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<SyncRetryWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
