package com.example.lexidesk.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.ReminderEntity
import com.example.lexidesk.data.entity.TaskEntity
import com.example.lexidesk.data.local.UserProfile
import com.example.lexidesk.utils.DateUtils
import com.example.lexidesk.utils.SampleDataHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val userProfile: UserProfile = UserProfile(),
    val clientCount: Int = 0,
    val activeCasesCount: Int = 0,
    val pendingTasksCount: Int = 0,
    val overdueTasksCount: Int = 0,
    val documentCount: Int = 0,
    val todayTasks: List<TaskEntity> = emptyList(),
    val todayReminders: List<ReminderEntity> = emptyList(),
    val upcomingCases: List<CaseEntity> = emptyList(),
    val upcomingTasks: List<TaskEntity> = emptyList(),
    val recentClients: List<ClientEntity> = emptyList(),
    val isSampleDataLoaded: Boolean = false,
    val isLoading: Boolean = false
)

class DashboardViewModel(private val app: LexiDeskApp) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val startOfDay = DateUtils.getStartOfDay(now)
    private val endOfDay = DateUtils.getEndOfDay(now)

    val uiState: StateFlow<DashboardUiState> = combine(
        app.userPreferencesRepository.userProfileFlow,
        app.clientRepository.clientCount,
        app.caseRepository.activeCasesCount,
        app.taskRepository.pendingTasksCount,
        app.taskRepository.getOverdueTasksCount(now),
        app.documentRepository.documentCount,
        app.taskRepository.getTasksForDate(startOfDay, endOfDay),
        app.reminderRepository.getRemindersForDate(startOfDay, endOfDay),
        app.caseRepository.getUpcomingCases(5),
        app.clientRepository.allClients,
        app.userPreferencesRepository.isSampleDataLoadedFlow
    ) { params ->
        val profile = params[0] as UserProfile
        val clients = params[1] as Int
        val activeCases = params[2] as Int
        val pendingTasks = params[3] as Int
        val overdueTasks = params[4] as Int
        val docs = params[5] as Int
        val tTasks = params[6] as List<TaskEntity>
        val tReminders = params[7] as List<ReminderEntity>
        val uCases = params[8] as List<CaseEntity>
        val allClients = params[9] as List<ClientEntity>
        val isSampleLoaded = params[10] as Boolean

        DashboardUiState(
            userProfile = profile,
            clientCount = clients,
            activeCasesCount = activeCases,
            pendingTasksCount = pendingTasks,
            overdueTasksCount = overdueTasks,
            documentCount = docs,
            todayTasks = tTasks,
            todayReminders = tReminders,
            upcomingCases = uCases,
            recentClients = allClients.take(5),
            isSampleDataLoaded = isSampleLoaded,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun loadSampleData() {
        viewModelScope.launch {
            SampleDataHelper.loadSampleData(app)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            SampleDataHelper.clearAllData(app)
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            app.taskRepository.toggleTaskCompleted(task.id, task.status)
        }
    }
}
