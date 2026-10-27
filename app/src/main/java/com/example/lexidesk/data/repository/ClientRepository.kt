package com.example.lexidesk.data.repository

import com.example.lexidesk.data.dao.ClientDao
import com.example.lexidesk.data.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

class ClientRepository(
    private val clientDao: ClientDao,
    private val syncPublisher: com.example.lexidesk.data.firebase.sync.CloudSyncPublisher? = null
) {

    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()
    val clientCount: Flow<Int> = clientDao.getClientCount()

    fun getClientById(id: Long): Flow<ClientEntity?> = clientDao.getClientById(id)

    suspend fun getClientByIdOnce(id: Long): ClientEntity? = clientDao.getClientByIdOnce(id)

    fun searchClients(query: String): Flow<List<ClientEntity>> = clientDao.searchClients(query)

    suspend fun insertClient(client: ClientEntity): Long {
        val id = clientDao.insertClient(client)
        val inserted = if (client.id == 0L) client.copy(id = id) else client
        syncPublisher?.publishClientUpsert(inserted)
        return id
    }

    suspend fun updateClient(client: ClientEntity) {
        clientDao.updateClient(client)
        syncPublisher?.publishClientUpsert(client)
    }

    suspend fun deleteClient(client: ClientEntity) {
        clientDao.deleteClient(client)
        syncPublisher?.publishClientDelete(client.id)
    }

    suspend fun deleteClientById(id: Long) {
        clientDao.deleteClientById(id)
        syncPublisher?.publishClientDelete(id)
    }
}
