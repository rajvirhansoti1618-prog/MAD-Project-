package com.example.lexidesk.data.firebase.sync

import com.example.lexidesk.data.dao.CaseDao
import com.example.lexidesk.data.dao.ClientDao
import com.example.lexidesk.data.dao.DraftTemplateDao
import com.example.lexidesk.data.dao.NoteDao
import com.example.lexidesk.data.dao.ReminderDao
import com.example.lexidesk.data.dao.TaskDao
import com.example.lexidesk.data.firebase.CloudDataSource
import com.example.lexidesk.data.firebase.CloudPaths
import com.example.lexidesk.data.firebase.model.CloudCaseDto
import com.example.lexidesk.data.firebase.model.CloudClientDto
import com.example.lexidesk.data.firebase.model.CloudDraftTemplateDto
import com.example.lexidesk.data.firebase.model.CloudNoteDto
import com.example.lexidesk.data.firebase.model.CloudReminderDto
import com.example.lexidesk.data.firebase.model.CloudTaskDto
import com.example.lexidesk.data.firebase.model.toCloudDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

data class SyncResult(
    val uploadedCount: Int = 0,
    val downloadedCount: Int = 0,
    val conflictResolvedCount: Int = 0,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)

class InitialSyncManager(
    private val cloudDataSource: CloudDataSource,
    private val clientDao: ClientDao,
    private val caseDao: CaseDao,
    private val taskDao: TaskDao,
    private val noteDao: NoteDao,
    private val reminderDao: ReminderDao,
    private val draftTemplateDao: DraftTemplateDao
) {

    suspend fun runInitialSync(
        workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID
    ): SyncResult = withContext(Dispatchers.IO) {
        var uploaded = 0
        var downloaded = 0
        var conflicts = 0

        try {
            // 1. Sync Clients
            val clientPath = CloudPaths.clientsPath(workspaceId)
            val remoteClients = cloudDataSource.fetchAllRecords(clientPath)
            val localClients = clientDao.getAllClients().first()

            val remoteClientMap = remoteClients.mapValues { CloudClientDto.fromMap(it.value) }

            for (local in localClients) {
                val recordId = CloudPaths.clientRecordId(local.id)
                val remote = remoteClientMap[recordId]
                if (remote == null) {
                    cloudDataSource.putRecord(clientPath, recordId, local.toCloudDto().toMap())
                    uploaded++
                } else if (remote.updatedAt > local.createdAt) {
                    if (!remote.isDeleted) {
                        clientDao.insertClient(remote.toEntity())
                        downloaded++
                    }
                    conflicts++
                } else {
                    cloudDataSource.putRecord(clientPath, recordId, local.toCloudDto(updatedAt = local.createdAt).toMap())
                    uploaded++
                }
            }

            for ((recordId, remote) in remoteClientMap) {
                val matchingLocal = localClients.find { CloudPaths.clientRecordId(it.id) == recordId }
                if (matchingLocal == null && !remote.isDeleted) {
                    clientDao.insertClient(remote.toEntity())
                    downloaded++
                }
            }

            // 2. Sync Cases
            val casePath = CloudPaths.casesPath(workspaceId)
            val remoteCases = cloudDataSource.fetchAllRecords(casePath).mapValues { CloudCaseDto.fromMap(it.value) }
            val localCases = caseDao.getAllCases().first()

            for (local in localCases) {
                val recordId = CloudPaths.caseRecordId(local.id)
                val remote = remoteCases[recordId]
                if (remote == null) {
                    cloudDataSource.putRecord(casePath, recordId, local.toCloudDto().toMap())
                    uploaded++
                } else if (remote.updatedAt > local.updatedAt) {
                    if (!remote.isDeleted) {
                        caseDao.insertCase(remote.toEntity())
                        downloaded++
                    }
                    conflicts++
                } else {
                    cloudDataSource.putRecord(casePath, recordId, local.toCloudDto().toMap())
                    uploaded++
                }
            }

            for ((recordId, remote) in remoteCases) {
                val matchingLocal = localCases.find { CloudPaths.caseRecordId(it.id) == recordId }
                if (matchingLocal == null && !remote.isDeleted) {
                    caseDao.insertCase(remote.toEntity())
                    downloaded++
                }
            }

            // 3. Sync Tasks
            val taskPath = CloudPaths.tasksPath(workspaceId)
            val remoteTasks = cloudDataSource.fetchAllRecords(taskPath).mapValues { CloudTaskDto.fromMap(it.value) }
            val localTasks = taskDao.getAllTasks().first()

            for (local in localTasks) {
                val recordId = CloudPaths.taskRecordId(local.id)
                val remote = remoteTasks[recordId]
                if (remote == null) {
                    cloudDataSource.putRecord(taskPath, recordId, local.toCloudDto().toMap())
                    uploaded++
                } else if (remote.updatedAt > local.createdAt) {
                    if (!remote.isDeleted) {
                        taskDao.insertTask(remote.toEntity())
                        downloaded++
                    }
                    conflicts++
                } else {
                    cloudDataSource.putRecord(taskPath, recordId, local.toCloudDto(updatedAt = local.createdAt).toMap())
                    uploaded++
                }
            }

            for ((recordId, remote) in remoteTasks) {
                val matchingLocal = localTasks.find { CloudPaths.taskRecordId(it.id) == recordId }
                if (matchingLocal == null && !remote.isDeleted) {
                    taskDao.insertTask(remote.toEntity())
                    downloaded++
                }
            }

            // 4. Sync Draft Templates
            val draftPath = CloudPaths.draftTemplatesPath(workspaceId)
            val remoteDrafts = cloudDataSource.fetchAllRecords(draftPath).mapValues { CloudDraftTemplateDto.fromMap(it.value) }
            val localDrafts = draftTemplateDao.getAllDrafts().first()

            for (local in localDrafts) {
                val recordId = CloudPaths.draftTemplateRecordId(local.id)
                val remote = remoteDrafts[recordId]
                if (remote == null) {
                    cloudDataSource.putRecord(draftPath, recordId, local.toCloudDto().toMap())
                    uploaded++
                } else if (remote.updatedAt > local.lastModified) {
                    if (!remote.isDeleted) {
                        draftTemplateDao.insertDraft(remote.toEntity())
                        downloaded++
                    }
                    conflicts++
                } else {
                    cloudDataSource.putRecord(draftPath, recordId, local.toCloudDto().toMap())
                    uploaded++
                }
            }

            for ((recordId, remote) in remoteDrafts) {
                val matchingLocal = localDrafts.find { CloudPaths.draftTemplateRecordId(it.id) == recordId }
                if (matchingLocal == null && !remote.isDeleted) {
                    draftTemplateDao.insertDraft(remote.toEntity())
                    downloaded++
                }
            }

            SyncResult(
                uploadedCount = uploaded,
                downloadedCount = downloaded,
                conflictResolvedCount = conflicts,
                isSuccess = true
            )
        } catch (e: Exception) {
            SyncResult(
                uploadedCount = uploaded,
                downloadedCount = downloaded,
                conflictResolvedCount = conflicts,
                isSuccess = false,
                errorMessage = e.message
            )
        }
    }
}
