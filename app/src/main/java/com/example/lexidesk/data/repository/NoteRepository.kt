package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.NoteDao
import com.example.lexidesk.data.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    fun getNotesByClient(clientId: Long): Flow<List<NoteEntity>> = noteDao.getNotesByClient(clientId)

    fun getNotesByCase(caseId: Long): Flow<List<NoteEntity>> = noteDao.getNotesByCase(caseId)

    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
}
