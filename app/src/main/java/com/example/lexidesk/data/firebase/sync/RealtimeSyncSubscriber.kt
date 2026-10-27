package com.example.lexidesk.data.firebase.sync

import android.util.Log
import com.example.lexidesk.data.dao.CaseDao
import com.example.lexidesk.data.dao.ClientDao
import com.example.lexidesk.data.dao.DraftTemplateDao
import com.example.lexidesk.data.dao.NoteDao
import com.example.lexidesk.data.dao.TaskDao
import com.example.lexidesk.data.firebase.CloudDataSource
import com.example.lexidesk.data.firebase.CloudPaths
import com.example.lexidesk.data.firebase.model.CloudCaseDto
import com.example.lexidesk.data.firebase.model.CloudClientDto
import com.example.lexidesk.data.firebase.model.CloudDraftTemplateDto
import com.example.lexidesk.data.firebase.model.CloudNoteDto
import com.example.lexidesk.data.firebase.model.CloudTaskDto
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RealtimeSyncSubscriber(
    private val cloudDataSource: CloudDataSource,
    private val clientDao: ClientDao,
    private val caseDao: CaseDao,
    private val taskDao: TaskDao,
    private val noteDao: NoteDao,
    private val draftTemplateDao: DraftTemplateDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val tag = "RealtimeSyncSubscriber"
    private val activeListeners = mutableListOf<Pair<DatabaseReference, ChildEventListener>>()

    @Synchronized
    fun startListening(workspaceId: String = CloudPaths.DEFAULT_WORKSPACE_ID) {
        stopListening()

        listenClients(workspaceId)
        listenCases(workspaceId)
        listenTasks(workspaceId)
        listenNotes(workspaceId)
        listenDraftTemplates(workspaceId)
        Log.i(tag, "Started realtime sync listeners for workspace: $workspaceId")
    }

    @Synchronized
    fun stopListening() {
        for ((ref, listener) in activeListeners) {
            try {
                ref.removeEventListener(listener)
            } catch (e: Exception) {
                Log.w(tag, "Failed removing listener: ${e.message}")
            }
        }
        activeListeners.clear()
        Log.i(tag, "Stopped all realtime sync listeners.")
    }

    private fun listenClients(workspaceId: String) {
        val ref = cloudDataSource.getDatabaseReference(CloudPaths.clientsPath(workspaceId))
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                processClientSnapshot(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                processClientSnapshot(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val dto = parseClient(snapshot) ?: return
                scope.launch { clientDao.deleteClientById(dto.id) }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.w(tag, "Client listener cancelled: ${error.message}")
            }
        }
        ref.addChildEventListener(listener)
        activeListeners.add(ref to listener)
    }

    private fun processClientSnapshot(snapshot: DataSnapshot) {
        val dto = parseClient(snapshot) ?: return
        scope.launch {
            try {
                val local = clientDao.getClientByIdOnce(dto.id)
                if (dto.isDeleted) {
                    if (local != null) clientDao.deleteClientById(dto.id)
                    return@launch
                }

                if (local == null || dto.updatedAt > local.createdAt) {
                    clientDao.insertClient(dto.toEntity())
                }
            } catch (e: Exception) {
                Log.w(tag, "Error processing client update: ${e.message}")
            }
        }
    }

    private fun parseClient(snapshot: DataSnapshot): CloudClientDto? {
        @Suppress("UNCHECKED_CAST")
        val map = snapshot.value as? Map<String, Any?> ?: return null
        return CloudClientDto.fromMap(map)
    }

    private fun listenCases(workspaceId: String) {
        val ref = cloudDataSource.getDatabaseReference(CloudPaths.casesPath(workspaceId))
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                processCaseSnapshot(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                processCaseSnapshot(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val dto = parseCase(snapshot) ?: return
                scope.launch { caseDao.deleteCaseById(dto.id) }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.w(tag, "Case listener cancelled: ${error.message}")
            }
        }
        ref.addChildEventListener(listener)
        activeListeners.add(ref to listener)
    }

    private fun processCaseSnapshot(snapshot: DataSnapshot) {
        val dto = parseCase(snapshot) ?: return
        scope.launch {
            try {
                val local = caseDao.getCaseByIdOnce(dto.id)
                if (dto.isDeleted) {
                    if (local != null) caseDao.deleteCaseById(dto.id)
                    return@launch
                }

                if (local == null || dto.updatedAt > local.updatedAt) {
                    caseDao.insertCase(dto.toEntity())
                }
            } catch (e: Exception) {
                Log.w(tag, "Error processing case update: ${e.message}")
            }
        }
    }

    private fun parseCase(snapshot: DataSnapshot): CloudCaseDto? {
        @Suppress("UNCHECKED_CAST")
        val map = snapshot.value as? Map<String, Any?> ?: return null
        return CloudCaseDto.fromMap(map)
    }

    private fun listenTasks(workspaceId: String) {
        val ref = cloudDataSource.getDatabaseReference(CloudPaths.tasksPath(workspaceId))
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                processTaskSnapshot(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                processTaskSnapshot(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val dto = parseTask(snapshot) ?: return
                scope.launch { taskDao.deleteTaskById(dto.id) }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.w(tag, "Task listener cancelled: ${error.message}")
            }
        }
        ref.addChildEventListener(listener)
        activeListeners.add(ref to listener)
    }

    private fun processTaskSnapshot(snapshot: DataSnapshot) {
        val dto = parseTask(snapshot) ?: return
        scope.launch {
            try {
                val local = taskDao.getTaskByIdOnce(dto.id)
                if (dto.isDeleted) {
                    if (local != null) taskDao.deleteTaskById(dto.id)
                    return@launch
                }

                if (local == null || dto.updatedAt > local.createdAt) {
                    taskDao.insertTask(dto.toEntity())
                }
            } catch (e: Exception) {
                Log.w(tag, "Error processing task update: ${e.message}")
            }
        }
    }

    private fun parseTask(snapshot: DataSnapshot): CloudTaskDto? {
        @Suppress("UNCHECKED_CAST")
        val map = snapshot.value as? Map<String, Any?> ?: return null
        return CloudTaskDto.fromMap(map)
    }

    private fun listenNotes(workspaceId: String) {
        val ref = cloudDataSource.getDatabaseReference(CloudPaths.notesPath(workspaceId))
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                processNoteSnapshot(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                processNoteSnapshot(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val dto = parseNote(snapshot) ?: return
                scope.launch { noteDao.deleteNote(dto.toEntity()) }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.w(tag, "Note listener cancelled: ${error.message}")
            }
        }
        ref.addChildEventListener(listener)
        activeListeners.add(ref to listener)
    }

    private fun processNoteSnapshot(snapshot: DataSnapshot) {
        val dto = parseNote(snapshot) ?: return
        scope.launch {
            try {
                if (dto.isDeleted) {
                    noteDao.deleteNote(dto.toEntity())
                    return@launch
                }
                noteDao.insertNote(dto.toEntity())
            } catch (e: Exception) {
                Log.w(tag, "Error processing note update: ${e.message}")
            }
        }
    }

    private fun parseNote(snapshot: DataSnapshot): CloudNoteDto? {
        @Suppress("UNCHECKED_CAST")
        val map = snapshot.value as? Map<String, Any?> ?: return null
        return CloudNoteDto.fromMap(map)
    }

    private fun listenDraftTemplates(workspaceId: String) {
        val ref = cloudDataSource.getDatabaseReference(CloudPaths.draftTemplatesPath(workspaceId))
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                processDraftSnapshot(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                processDraftSnapshot(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val dto = parseDraft(snapshot) ?: return
                scope.launch { draftTemplateDao.deleteDraft(dto.toEntity()) }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.w(tag, "Draft listener cancelled: ${error.message}")
            }
        }
        ref.addChildEventListener(listener)
        activeListeners.add(ref to listener)
    }

    private fun processDraftSnapshot(snapshot: DataSnapshot) {
        val dto = parseDraft(snapshot) ?: return
        scope.launch {
            try {
                val local = draftTemplateDao.getDraftByIdOnce(dto.id)
                if (dto.isDeleted) {
                    if (local != null) draftTemplateDao.deleteDraft(dto.toEntity())
                    return@launch
                }

                if (local == null || dto.updatedAt > local.lastModified) {
                    draftTemplateDao.insertDraft(dto.toEntity())
                }
            } catch (e: Exception) {
                Log.w(tag, "Error processing draft template update: ${e.message}")
            }
        }
    }

    private fun parseDraft(snapshot: DataSnapshot): CloudDraftTemplateDto? {
        @Suppress("UNCHECKED_CAST")
        val map = snapshot.value as? Map<String, Any?> ?: return null
        return CloudDraftTemplateDto.fromMap(map)
    }
}
