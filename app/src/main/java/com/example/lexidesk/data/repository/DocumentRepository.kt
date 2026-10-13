package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.DocumentDao
import com.example.lexidesk.data.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

class DocumentRepository(private val documentDao: DocumentDao) {

    val allDocuments: Flow<List<DocumentEntity>> = documentDao.getAllDocuments()
    val documentCount: Flow<Int> = documentDao.getDocumentCount()

    fun getDocumentById(id: Long): Flow<DocumentEntity?> = documentDao.getDocumentById(id)

    suspend fun getDocumentByIdOnce(id: Long): DocumentEntity? = documentDao.getDocumentByIdOnce(id)

    fun getDocumentsByClient(clientId: Long): Flow<List<DocumentEntity>> =
        documentDao.getDocumentsByClient(clientId)

    fun getDocumentsByCase(caseId: Long): Flow<List<DocumentEntity>> =
        documentDao.getDocumentsByCase(caseId)

    fun searchDocuments(query: String): Flow<List<DocumentEntity>> =
        documentDao.searchDocuments(query)

    suspend fun insertDocument(document: DocumentEntity): Long =
        documentDao.insertDocument(document)

    suspend fun updateDocument(document: DocumentEntity) =
        documentDao.updateDocument(document)

    suspend fun deleteDocument(document: DocumentEntity) =
        documentDao.deleteDocument(document)

    suspend fun deleteDocumentById(id: Long) =
        documentDao.deleteDocumentById(id)
}
