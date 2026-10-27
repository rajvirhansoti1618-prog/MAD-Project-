package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.CaseDao
import com.example.lexidesk.data.entity.CaseEntity
import kotlinx.coroutines.flow.Flow

class CaseRepository(
    private val caseDao: CaseDao,
    private val syncPublisher: com.example.lexidesk.data.firebase.sync.CloudSyncPublisher? = null
) {

    val allCases: Flow<List<CaseEntity>> = caseDao.getAllCases()
    val activeCasesCount: Flow<Int> = caseDao.getActiveCasesCount()
    val completedCasesCount: Flow<Int> = caseDao.getCompletedCasesCount()
    val totalCasesCount: Flow<Int> = caseDao.getTotalCasesCount()

    fun getCaseById(id: Long): Flow<CaseEntity?> = caseDao.getCaseById(id)

    suspend fun getCaseByIdOnce(id: Long): CaseEntity? = caseDao.getCaseByIdOnce(id)

    fun getCasesByClient(clientId: Long): Flow<List<CaseEntity>> = caseDao.getCasesByClient(clientId)

    fun searchCases(query: String): Flow<List<CaseEntity>> = caseDao.searchCases(query)

    fun getUpcomingCases(limit: Int = 5): Flow<List<CaseEntity>> = caseDao.getUpcomingCases(limit)

    suspend fun insertCase(caseItem: CaseEntity): Long {
        val id = caseDao.insertCase(caseItem)
        val inserted = if (caseItem.id == 0L) caseItem.copy(id = id) else caseItem
        syncPublisher?.publishCaseUpsert(inserted)
        return id
    }

    suspend fun updateCase(caseItem: CaseEntity) {
        caseDao.updateCase(caseItem)
        syncPublisher?.publishCaseUpsert(caseItem)
    }

    suspend fun deleteCase(caseItem: CaseEntity) {
        caseDao.deleteCase(caseItem)
        syncPublisher?.publishCaseDelete(caseItem.id)
    }

    suspend fun deleteCaseById(id: Long) {
        caseDao.deleteCaseById(id)
        syncPublisher?.publishCaseDelete(id)
    }
}
