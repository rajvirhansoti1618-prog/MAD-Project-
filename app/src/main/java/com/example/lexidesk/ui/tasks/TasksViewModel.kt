package com.example.lexidesk.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.ReminderEntity
import com.example.lexidesk.data.entity.TaskEntity
import com.example.lexidesk.notifications.ReminderScheduler
import com.example.lexidesk.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import android.util.Log
import kotlinx.coroutines.launch

data class TasksListUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val searchQuery: String = "",
    val activeFilter: String = "All", // All, Today, Upcoming, Overdue, Completed
    val priorityFilter: String = "All",
    val sortBy: String = "Due Date", // Due Date, Priority, Title
    val isLoading: Boolean = false
)

class TasksViewModel(private val app: LexiDeskApp) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow("All")
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    private val _priorityFilter = MutableStateFlow("All")
    val priorityFilter: StateFlow<String> = _priorityFilter.asStateFlow()

    private val _sortBy = MutableStateFlow("Due Date")
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    val clientsFlow: StateFlow<List<ClientEntity>> = app.clientRepository.allClients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val casesFlow: StateFlow<List<CaseEntity>> = app.caseRepository.allCases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasksListUiState: StateFlow<TasksListUiState> = combine(
        app.taskRepository.allTasks,
        _searchQuery,
        _activeFilter,
        _priorityFilter,
        _sortBy
    ) { allTasks, query, filter, priority, sort ->
        val now = System.currentTimeMillis()
        val startOfToday = DateUtils.getStartOfDay(now)
        val endOfToday = DateUtils.getEndOfDay(now)

        val filtered = allTasks.filter { task ->
            val matchesQuery = if (query.isBlank()) true else {
                task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
            }
            val matchesPriority = if (priority == "All") true else task.priority.equals(priority, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Today" -> (task.dueDate != null && task.dueDate in startOfToday..endOfToday)
                "Upcoming" -> (task.dueDate != null && task.dueDate > endOfToday && task.status != "Completed")
                "Overdue" -> (task.dueDate != null && task.dueDate < startOfToday && task.status != "Completed")
                "Completed" -> task.status == "Completed"
                else -> true // All
            }

            matchesQuery && matchesPriority && matchesFilter
        }

        val sorted = when (sort) {
            "Priority" -> filtered.sortedWith(
                compareBy<TaskEntity> { it.status == "Completed" }
                    .thenByDescending { it.priorityWeight }
            )
            "Title" -> filtered.sortedWith(
                compareBy<TaskEntity> { it.status == "Completed" }
                    .thenBy { it.title.lowercase() }
            )
            "Newest" -> filtered.sortedWith(
                compareBy<TaskEntity> { it.status == "Completed" }
                    .thenByDescending { it.createdAt }
            )
            else -> filtered.sortedWith( // "Due Date"
                compareBy<TaskEntity> { it.status == "Completed" }
                    .thenBy(nullsLast()) { it.dueDate }
            )
        }

        TasksListUiState(
            tasks = sorted,
            searchQuery = query,
            activeFilter = filter,
            priorityFilter = priority,
            sortBy = sort,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksListUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onActiveFilterChanged(filter: String) { _activeFilter.value = filter }
    fun onPriorityFilterChanged(priority: String) { _priorityFilter.value = priority }
    fun onSortByChanged(sortBy: String) { _sortBy.value = sortBy }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                app.taskRepository.toggleTaskCompleted(task.id, task.status)
            } catch (e: Exception) {
                Log.e("TasksViewModel", "Failed to toggle task status", e)
            }
        }
    }

    fun saveTask(
        id: Long = 0,
        title: String,
        description: String,
        clientId: Long?,
        caseId: Long?,
        priority: String,
        status: String,
        dueDate: Long?,
        dueTime: String?,
        reminderEnabled: Boolean,
        reminderMinutesBefore: Int = 60,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            try {
            // Preserve original createdAt when updating an existing task
            val existingCreatedAt = if (id != 0L) {
                app.taskRepository.getTaskByIdOnce(id)?.createdAt ?: System.currentTimeMillis()
            } else {
                System.currentTimeMillis()
            }
            val task = TaskEntity(
                id = id,
                title = title.trim(),
                description = description.trim(),
                clientId = clientId,
                caseId = caseId,
                priority = priority,
                status = status,
                dueDate = dueDate,
                dueTime = dueTime,
                reminderEnabled = reminderEnabled,
                createdAt = existingCreatedAt
            )
            val newId = if (id == 0L) {
                app.taskRepository.insertTask(task)
            } else {
                app.taskRepository.updateTask(task)
                id
            }

            // If reminder enabled, schedule notification
            if (reminderEnabled && dueDate != null) {
                val reminder = ReminderEntity(
                    title = "Task Due: ${task.title}",
                    description = task.description.ifBlank { "Task deadline is today." },
                    reminderType = "Assignment Deadline",
                    eventTimestamp = dueDate,
                    minutesBefore = reminderMinutesBefore,
                    clientId = clientId,
                    caseId = caseId,
                    taskId = newId
                )
                val reminderId = app.reminderRepository.insertReminder(reminder)
                ReminderScheduler.scheduleReminder(app, reminder.copy(id = reminderId))
            }

            onSuccess(newId)
            } catch (e: Exception) {
                Log.e("TasksViewModel", "Failed to save task", e)
            }
        }
    }

    fun deleteTask(taskId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                app.taskRepository.deleteTaskById(taskId)
                onDeleted()
            } catch (e: Exception) {
                Log.e("TasksViewModel", "Failed to delete task", e)
            }
        }
    }

    suspend fun getTaskByIdOnce(id: Long): TaskEntity? {
        return app.taskRepository.getTaskByIdOnce(id)
    }
}
