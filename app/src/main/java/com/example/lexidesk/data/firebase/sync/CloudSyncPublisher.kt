package com.example.lexidesk.data.firebase.sync

import android.util.Log
import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.DraftTemplateEntity
import com.example.lexidesk.data.entity.NoteEntity
import com.example.lexidesk.data.entity.ReminderEntity
import com.example.lexidesk.data.entity.TaskEntity
import com.example.lexidesk.data.firebase.CloudDataSource
import com.example.lexidesk.data.firebase.CloudPaths
import com.example.lexidesk.data.firebase.model.toCloudDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : CloudSyncPublisher {

    private val tag = "CloudSyncPublisher"

    override fun publishClientUpsert(client: ClientEntity, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.clientsPath(workspaceId)
                val id = CloudPaths.clientRecordId(client.id)
                cloudDataSource.putRecord(path, id, client.toCloudDto().toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish client upsert: ${e.message}")
            }
        }
    }

    override fun publishClientDelete(clientId: Long, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.clientsPath(workspaceId)
                val id = CloudPaths.clientRecordId(clientId)
                val tombstone = mapOf(
                    "id" to clientId,
                    "isDeleted" to true,
                    "updatedAt" to System.currentTimeMillis()
                )
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish client delete: ${e.message}")
            }
        }
    }

    override fun publishCaseUpsert(caseEntity: CaseEntity, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.casesPath(workspaceId)
                val id = CloudPaths.caseRecordId(caseEntity.id)
                cloudDataSource.putRecord(path, id, caseEntity.toCloudDto().toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish case upsert: ${e.message}")
            }
        }
    }

    override fun publishCaseDelete(caseId: Long, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.casesPath(workspaceId)
                val id = CloudPaths.caseRecordId(caseId)
                val tombstone = mapOf(
                    "id" to caseId,
                    "isDeleted" to true,
                    "updatedAt" to System.currentTimeMillis()
                )
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish case delete: ${e.message}")
            }
        }
    }

    override fun publishTaskUpsert(task: TaskEntity, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.tasksPath(workspaceId)
                val id = CloudPaths.taskRecordId(task.id)
                cloudDataSource.putRecord(path, id, task.toCloudDto().toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish task upsert: ${e.message}")
            }
        }
    }

    override fun publishTaskDelete(taskId: Long, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.tasksPath(workspaceId)
                val id = CloudPaths.taskRecordId(taskId)
                val tombstone = mapOf(
                    "id" to taskId,
                    "isDeleted" to true,
                    "updatedAt" to System.currentTimeMillis()
                )
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish task delete: ${e.message}")
            }
        }
    }

    override fun publishNoteUpsert(note: NoteEntity, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.notesPath(workspaceId)
                val id = CloudPaths.noteRecordId(note.id)
                cloudDataSource.putRecord(path, id, note.toCloudDto().toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish note upsert: ${e.message}")
            }
        }
    }

    override fun publishNoteDelete(noteId: Long, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.notesPath(workspaceId)
                val id = CloudPaths.noteRecordId(noteId)
                val tombstone = mapOf(
                    "id" to noteId,
                    "isDeleted" to true,
                    "updatedAt" to System.currentTimeMillis()
                )
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish note delete: ${e.message}")
            }
        }
    }

    override fun publishDraftUpsert(draft: DraftTemplateEntity, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.draftTemplatesPath(workspaceId)
                val id = CloudPaths.draftTemplateRecordId(draft.id)
                cloudDataSource.putRecord(path, id, draft.toCloudDto().toMap())
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish draft upsert: ${e.message}")
            }
        }
    }

    override fun publishDraftDelete(draftId: Long, workspaceId: String) {
        scope.launch {
            try {
                val path = CloudPaths.draftTemplatesPath(workspaceId)
                val id = CloudPaths.draftTemplateRecordId(draftId)
                val tombstone = mapOf(
                    "id" to draftId,
                    "isDeleted" to true,
                    "updatedAt" to System.currentTimeMillis()
                )
                cloudDataSource.updateFields(path, id, tombstone)
            } catch (e: Exception) {
                Log.w(tag, "Failed to publish draft delete: ${e.message}")
            }
        }
    }
}
