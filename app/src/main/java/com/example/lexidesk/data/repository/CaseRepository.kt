package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.CaseDao
import com.example.lexidesk.data.entity.CaseEntity
import kotlinx.coroutines.flow.Flow

class CaseRepository(private val caseDao: CaseDao) {

    val allCases: Flow<List<CaseEntity>> = caseDao.getAllCases()
    val activeCasesCount: Flow<Int> = caseDao.getActiveCasesCount()
    val completedCasesCount: Flow<Int> = caseDao.getCompletedCasesCount()
    val totalCasesCount: Flow<Int> = caseDao.getTotalCasesCount()

    fun getCaseById(id: Long): Flow<CaseEntity?> = caseDao.getCaseById(id)

    suspend fun getCaseByIdOnce(id: Long): CaseEntity? = caseDao.getCaseByIdOnce(id)

    fun getCasesByClient(clientId: Long): Flow<List<CaseEntity>> = caseDao.getCasesByClient(clientId)

    fun searchCases(query: String): Flow<List<CaseEntity>> = caseDao.searchCases(query)

    fun getUpcomingCases(limit: Int = 5): Flow<List<CaseEntity>> = caseDao.getUpcomingCases(limit)

    suspend fun insertCase(caseItem: CaseEntity): Long = caseDao.insertCase(caseItem)

    suspend fun updateCase(caseItem: CaseEntity) = caseDao.updateCase(caseItem)

    suspend fun deleteCase(caseItem: CaseEntity) = caseDao.deleteCase(caseItem)

    suspend fun deleteCaseById(id: Long) = caseDao.deleteCaseById(id)
}
