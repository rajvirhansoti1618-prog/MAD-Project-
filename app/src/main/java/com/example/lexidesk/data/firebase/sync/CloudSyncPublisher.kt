package com.example.lexidesk.data.firebase.sync

import android.util.Log
import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.DraftTemplateEntity
import com.example.lexidesk.data.entity.NoteEntity
import com.example.lexidesk.data.entity.TaskEntity
import com.example.lexidesk.data.firebase.CloudDataSource
import com.example.lexidesk.data.firebase.CloudPaths
import com.example.lexidesk.data.firebase.model.toCloudDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

interface CloudSyncPublisher {
    fun publishClientUpsert(client: ClientEntity, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
    fun publishClientDelete(clientId: Long, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
    fun publishCaseUpsert(caseEntity: CaseEntity, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
    fun publishCaseDelete(caseId: Long, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
    fun publishTaskUpsert(task: TaskEntity, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
    fun publishTaskDelete(taskId: Long, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
    fun publishNoteUpsert(note: NoteEntity, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
    fun publishNoteDelete(noteId: Long, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
    fun publishDraftUpsert(draft: DraftTemplateEntity, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
    fun publishDraftDelete(draftId: Long, workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID)
}

class DefaultCloudSyncPublisher(
    private val cloudDataSource: CloudDataSource,
    private val queueManager: OfflineSyncQueueManager? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : CloudSyncPublisher {

    private val tag = "CloudSyncPublisher"

    override fun publishClientUpsert(client: ClientEntity, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.clientsPath(workspaceId)
            val id = CloudPaths.clientRecordId(client.id)
            val dto = client.toCloudDto()
            try {
                cloudDataSource.putRecord(path, id, dto.toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish client upsert: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "client",
                        recordId = client.id,
                        action = "UPSERT",
                        payloadJson = JSONObject(dto.toMap()).toString()
                    )
                )
            }
        }
    }

    override fun publishClientDelete(clientId: Long, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.clientsPath(workspaceId)
            val id = CloudPaths.clientRecordId(clientId)
            val tombstone = mapOf(
                "id" to clientId,
                "isDeleted" to true,
                "updatedAt" to System.currentTimeMillis()
            )
            try {
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish client delete: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "client",
                        recordId = clientId,
                        action = "DELETE"
                    )
                )
            }
        }
    }

    override fun publishCaseUpsert(caseEntity: CaseEntity, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.casesPath(workspaceId)
            val id = CloudPaths.caseRecordId(caseEntity.id)
            val dto = caseEntity.toCloudDto()
            try {
                cloudDataSource.putRecord(path, id, dto.toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish case upsert: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "case",
                        recordId = caseEntity.id,
                        action = "UPSERT",
                        payloadJson = JSONObject(dto.toMap()).toString()
                    )
                )
            }
        }
    }

    override fun publishCaseDelete(caseId: Long, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.casesPath(workspaceId)
            val id = CloudPaths.caseRecordId(caseId)
            val tombstone = mapOf(
                "id" to caseId,
                "isDeleted" to true,
                "updatedAt" to System.currentTimeMillis()
            )
            try {
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish case delete: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "case",
                        recordId = caseId,
                        action = "DELETE"
                    )
                )
            }
        }
    }

    override fun publishTaskUpsert(task: TaskEntity, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.tasksPath(workspaceId)
            val id = CloudPaths.taskRecordId(task.id)
            val dto = task.toCloudDto()
            try {
                cloudDataSource.putRecord(path, id, dto.toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish task upsert: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "task",
                        recordId = task.id,
                        action = "UPSERT",
                        payloadJson = JSONObject(dto.toMap()).toString()
                    )
                )
            }
        }
    }

    override fun publishTaskDelete(taskId: Long, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.tasksPath(workspaceId)
            val id = CloudPaths.taskRecordId(taskId)
            val tombstone = mapOf(
                "id" to taskId,
                "isDeleted" to true,
                "updatedAt" to System.currentTimeMillis()
            )
            try {
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish task delete: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "task",
                        recordId = taskId,
                        action = "DELETE"
                    )
                )
            }
        }
    }

    override fun publishNoteUpsert(note: NoteEntity, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.notesPath(workspaceId)
            val id = CloudPaths.noteRecordId(note.id)
            val dto = note.toCloudDto()
            try {
                cloudDataSource.putRecord(path, id, dto.toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish note upsert: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "note",
                        recordId = note.id,
                        action = "UPSERT",
                        payloadJson = JSONObject(dto.toMap()).toString()
                    )
                )
            }
        }
    }

    override fun publishNoteDelete(noteId: Long, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.notesPath(workspaceId)
            val id = CloudPaths.noteRecordId(noteId)
            val tombstone = mapOf(
                "id" to noteId,
                "isDeleted" to true,
                "updatedAt" to System.currentTimeMillis()
            )
            try {
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish note delete: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "note",
                        recordId = noteId,
                        action = "DELETE"
                    )
                )
            }
        }
    }

    override fun publishDraftUpsert(draft: DraftTemplateEntity, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.draftTemplatesPath(workspaceId)
            val id = CloudPaths.draftTemplateRecordId(draft.id)
            val dto = draft.toCloudDto()
            try {
                cloudDataSource.putRecord(path, id, dto.toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish draft upsert: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "draft",
                        recordId = draft.id,
                        action = "UPSERT",
                        payloadJson = JSONObject(dto.toMap()).toString()
                    )
                )
            }
        }
    }

    override fun publishDraftDelete(draftId: Long, workspaceId: String) {
        scope.launch {
            val path = CloudPaths.draftTemplatesPath(workspaceId)
            val id = CloudPaths.draftTemplateRecordId(draftId)
            val tombstone = mapOf(
                "id" to draftId,
                "isDeleted" to true,
                "updatedAt" to System.currentTimeMillis()
            )
            try {
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish draft delete: ${e.message}; queueing offline.")
                queueManager?.enqueue(
                    PendingMutation(
                        id = UUID.randomUUID().toString(),
                        entityType = "draft",
                        recordId = draftId,
                        action = "DELETE"
                    )
                )
            }
        }
    }
}
