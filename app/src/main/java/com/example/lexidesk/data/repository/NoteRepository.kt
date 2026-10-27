package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.NoteDao
import com.example.lexidesk.data.entity.NoteEntity
import com.example.lexidesk.data.firebase.sync.CloudSyncPublisher
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val syncPublisher: CloudSyncPublisher? = null
) {

    fun getNotesByClient(clientId: Long): Flow<List<NoteEntity>> = noteDao.getNotesByClient(clientId)

    fun getNotesByCase(caseId: Long): Flow<List<NoteEntity>> = noteDao.getNotesByCase(caseId)

    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)

    suspend fun insertNote(note: NoteEntity): Long {
        val id = noteDao.insertNote(note)
        val inserted = if (note.id == 0L) note.copy(id = id) else note
        syncPublisher?.publishNoteUpsert(inserted)
        return id
    }

    suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
        syncPublisher?.publishNoteUpsert(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
        syncPublisher?.publishNoteDelete(note.id)
    }
}
