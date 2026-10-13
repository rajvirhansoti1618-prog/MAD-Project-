package com.example.lexidesk.ui.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.DocumentEntity
import com.example.lexidesk.data.entity.NoteEntity
import com.example.lexidesk.data.entity.TaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ClientsListUiState(
    val clients: List<ClientEntity> = emptyList(),
    val searchQuery: String = "",
    val filterType: String = "All", // All, Individual, Business
    val sortBy: String = "Recent", // Recent, Name
    val isLoading: Boolean = false
)

data class ClientDetailUiState(
    val client: ClientEntity? = null,
    val cases: List<CaseEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val documents: List<DocumentEntity> = emptyList(),
    val notes: List<NoteEntity> = emptyList(),
    val isLoading: Boolean = false
)

class ClientsViewModel(private val app: LexiDeskApp) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow("All")
    val filterType: StateFlow<String> = _filterType.asStateFlow()

    private val _sortBy = MutableStateFlow("Recent")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    val clientsListUiState: StateFlow<ClientsListUiState> = combine(
        app.clientRepository.allClients,
        _searchQuery,
        _filterType,
        _sortBy
    ) { allClients, query, filter, sort ->
        val filtered = allClients.filter { client ->
            val matchesQuery = if (query.isBlank()) true else {
                client.fullName.contains(query, ignoreCase = true) ||
                        client.phone.contains(query, ignoreCase = true) ||
                        client.email.contains(query, ignoreCase = true) ||
                        client.companyName.contains(query, ignoreCase = true) ||
                        client.tags.contains(query, ignoreCase = true)
            }
            val matchesType = when (filter) {
                "Individual" -> client.clientType.equals("Individual", ignoreCase = true)
                "Business" -> client.clientType.equals("Business", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesType
        }.let { list ->
            when (sort) {
                "Name" -> list.sortedBy { it.fullName.lowercase() }
                else -> list.sortedByDescending { it.createdAt }
            }
        }

        ClientsListUiState(
            clients = filtered,
            searchQuery = query,
            filterType = filter,
            sortBy = sort,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ClientsListUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterTypeChanged(filter: String) {
        _filterType.value = filter
    }

    fun onSortByChanged(sort: String) {
        _sortBy.value = sort
    }

    fun getClientDetailState(clientId: Long): StateFlow<ClientDetailUiState> {
        return combine(
            app.clientRepository.getClientById(clientId),
            app.caseRepository.getCasesByClient(clientId),
            app.taskRepository.getTasksByClient(clientId),
            app.documentRepository.getDocumentsByClient(clientId),
            app.noteRepository.getNotesByClient(clientId)
        ) { client, cases, tasks, docs, notes ->
            ClientDetailUiState(
                client = client,
                cases = cases,
                tasks = tasks,
                documents = docs,
                notes = notes,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ClientDetailUiState(isLoading = true)
        )
    }

    fun saveClient(
        id: Long = 0,
        fullName: String,
        clientType: String,
        phone: String,
        email: String,
        address: String,
        companyName: String,
        notes: String,
        tags: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val client = ClientEntity(
                id = id,
                fullName = fullName.trim(),
                clientType = clientType,
                phone = phone.trim(),
                email = email.trim(),
                address = address.trim(),
                companyName = companyName.trim(),
                notes = notes.trim(),
                tags = tags.trim(),
                createdAt = if (id == 0L) System.currentTimeMillis() else System.currentTimeMillis()
            )
            val newId = if (id == 0L) {
                app.clientRepository.insertClient(client)
            } else {
                app.clientRepository.updateClient(client)
                id
            }
            onSuccess(newId)
        }
    }

    fun deleteClient(clientId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            app.clientRepository.deleteClientById(clientId)
            onDeleted()
        }
    }

    fun addNote(clientId: Long, title: String, content: String, followUpDate: Long? = null) {
        viewModelScope.launch {
            app.noteRepository.insertNote(
                NoteEntity(
                    clientId = clientId,
                    title = title.trim(),
                    content = content.trim(),
                    followUpDate = followUpDate
                )
            )
        }
    }

    suspend fun getClientByIdOnce(id: Long): ClientEntity? {
        return app.clientRepository.getClientByIdOnce(id)
    }
}
