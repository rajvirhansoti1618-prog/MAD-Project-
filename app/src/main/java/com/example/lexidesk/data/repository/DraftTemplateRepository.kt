package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.DraftTemplateDao
import com.example.lexidesk.data.entity.DraftTemplateEntity
import com.example.lexidesk.data.firebase.sync.CloudSyncPublisher
import kotlinx.coroutines.flow.Flow

class DraftTemplateRepository(
    private val draftDao: DraftTemplateDao,
    private val syncPublisher: CloudSyncPublisher? = null
) {

    val allDrafts: Flow<List<DraftTemplateEntity>> = draftDao.getAllDrafts()

    fun getDraftById(id: Long): Flow<DraftTemplateEntity?> = draftDao.getDraftById(id)

    suspend fun getDraftByIdOnce(id: Long): DraftTemplateEntity? = draftDao.getDraftByIdOnce(id)

    fun searchDrafts(query: String): Flow<List<DraftTemplateEntity>> = draftDao.searchDrafts(query)

    suspend fun insertDraft(draft: DraftTemplateEntity): Long {
        val id = draftDao.insertDraft(draft)
        val inserted = if (draft.id == 0L) draft.copy(id = id) else draft
        syncPublisher?.publishDraftUpsert(inserted)
        return id
    }

    suspend fun updateDraft(draft: DraftTemplateEntity) {
        draftDao.updateDraft(draft)
        syncPublisher?.publishDraftUpsert(draft)
    }

    suspend fun deleteDraft(draft: DraftTemplateEntity) {
        draftDao.deleteDraft(draft)
        syncPublisher?.publishDraftDelete(draft.id)
    }
}
