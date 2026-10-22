package com.example.lexidesk.ui.cases

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

data class CasesListUiState(
    val cases: List<CaseEntity> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: String = "All", // All, In Progress, Completed, Waiting, On Hold
    val priorityFilter: String = "All",
    val sortBy: String = "Recent", // Recent, Due Date, Priority
    val isLoading: Boolean = false
)

data class CaseDetailUiState(
    val caseItem: CaseEntity? = null,
    val client: ClientEntity? = null,
    val tasks: List<TaskEntity> = emptyList(),
    val documents: List<DocumentEntity> = emptyList(),
    val notes: List<NoteEntity> = emptyList(),
    val isLoading: Boolean = false
)

class CasesViewModel(private val app: LexiDeskApp) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _priorityFilter = MutableStateFlow("All")
    val priorityFilter: StateFlow<String> = _priorityFilter.asStateFlow()

    private val _sortBy = MutableStateFlow("Recent")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    val clientsFlow: StateFlow<List<ClientEntity>> = app.clientRepository.allClients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val casesListUiState: StateFlow<CasesListUiState> = combine(
        app.caseRepository.allCases,
        _searchQuery,
        _statusFilter,
        _priorityFilter,
        _sortBy
    ) { allCases, query, status, priority, sort ->
        val filtered = allCases.filter { caseItem ->
            val matchesQuery = if (query.isBlank()) true else {
                caseItem.title.contains(query, ignoreCase = true) ||
                        caseItem.referenceNumber.contains(query, ignoreCase = true) ||
                        caseItem.category.contains(query, ignoreCase = true) ||
                        caseItem.courtOrLocation.contains(query, ignoreCase = true)
            }
            val matchesStatus = if (status == "All") true else caseItem.status.equals(status, ignoreCase = true)
            val matchesPriority = if (priority == "All") true else caseItem.priority.equals(priority, ignoreCase = true)

            matchesQuery && matchesStatus && matchesPriority
        }.let { list ->
            when (sort) {
                "Due Date" -> list.sortedWith(compareBy(nullsLast()) { it.dueDate })
                "Priority" -> list.sortedByDescending { it.priorityWeight }
                "Title" -> list.sortedBy { it.title.lowercase() }
                else -> list.sortedByDescending { it.updatedAt }
            }
        }

        CasesListUiState(
            cases = filtered,
            searchQuery = query,
            statusFilter = status,
            priorityFilter = priority,
            sortBy = sort,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CasesListUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onStatusFilterChanged(status: String) { _statusFilter.value = status }
    fun onPriorityFilterChanged(priority: String) { _priorityFilter.value = priority }
    fun onSortByChanged(sort: String) { _sortBy.value = sort }

    fun getCaseDetailState(caseId: Long): StateFlow<CaseDetailUiState> {
        return combine(
            app.caseRepository.getCaseById(caseId),
            app.taskRepository.getTasksByCase(caseId),
            app.documentRepository.getDocumentsByCase(caseId),
            app.noteRepository.getNotesByCase(caseId)
        ) { caseItem, tasks, docs, notes ->
            val client = if (caseItem?.clientId != null) app.clientRepository.getClientByIdOnce(caseItem.clientId) else null
            CaseDetailUiState(
                caseItem = caseItem,
                client = client,
                tasks = tasks,
                documents = docs,
                notes = notes,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CaseDetailUiState(isLoading = true)
        )
    }

    fun saveCase(
        id: Long = 0,
        clientId: Long?,
        title: String,
        category: String,
        description: String,
        referenceNumber: String,
        priority: String,
        status: String,
        startDate: Long?,
        dueDate: Long?,
        courtOrLocation: String,
        notes: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            // Preserve original createdAt when updating an existing case
            val existingCreatedAt = if (id != 0L) {
                app.caseRepository.getCaseByIdOnce(id)?.createdAt ?: now
            } else {
                now
            }
            val caseItem = CaseEntity(
                id = id,
                clientId = clientId,
                title = title.trim(),
                category = category.trim(),
                description = description.trim(),
                referenceNumber = referenceNumber.trim(),
                priority = priority,
                status = status,
                startDate = startDate,
                dueDate = dueDate,
                courtOrLocation = courtOrLocation.trim(),
                notes = notes.trim(),
                createdAt = existingCreatedAt,
                updatedAt = now
            )
            val newId = if (id == 0L) {
                app.caseRepository.insertCase(caseItem)
            } else {
                app.caseRepository.updateCase(caseItem)
                id
            }
            onSuccess(newId)
        }
    }

    fun deleteCase(caseId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            app.caseRepository.deleteCaseById(caseId)
            onDeleted()
        }
    }

    fun addNote(caseId: Long, title: String, content: String) {
        viewModelScope.launch {
            app.noteRepository.insertNote(
                NoteEntity(
                    caseId = caseId,
                    title = title.trim(),
                    content = content.trim()
                )
            )
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            app.noteRepository.updateNote(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            app.noteRepository.deleteNote(note)
        }
    }

    suspend fun getCaseByIdOnce(id: Long): CaseEntity? {
        return app.caseRepository.getCaseByIdOnce(id)
    }
}
