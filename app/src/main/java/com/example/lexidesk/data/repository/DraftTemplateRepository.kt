package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.DraftTemplateDao
import com.example.lexidesk.data.entity.DraftTemplateEntity
import kotlinx.coroutines.flow.Flow

class DraftTemplateRepository(private val draftDao: DraftTemplateDao) {

    val allDrafts: Flow<List<DraftTemplateEntity>> = draftDao.getAllDrafts()

    fun getDraftById(id: Long): Flow<DraftTemplateEntity?> = draftDao.getDraftById(id)

    fun searchDrafts(query: String): Flow<List<DraftTemplateEntity>> = draftDao.searchDrafts(query)

    suspend fun getDraftByIdOnce(id: Long): DraftTemplateEntity? = draftDao.getDraftByIdOnce(id)

    suspend fun insertDraft(draft: DraftTemplateEntity): Long = draftDao.insertDraft(draft)

    suspend fun updateDraft(draft: DraftTemplateEntity) = draftDao.updateDraft(draft)

    suspend fun deleteDraft(draft: DraftTemplateEntity) = draftDao.deleteDraft(draft)
}
