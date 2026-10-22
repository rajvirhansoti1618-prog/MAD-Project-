package com.example.lexidesk.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.entity.CaseEntity
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
import kotlinx.coroutines.launch
import java.util.Calendar

data class CalendarEventItem(
    val id: Long,
    val title: String,
    val type: String, // "Court Hearing", "Meeting", "Task Deadline", "Case Due", "Custom"
    val timestamp: Long,
    val relatedId: Long? = null,
    val isCompleted: Boolean = false
)

data class CalendarUiState(
    val selectedYear: Int,
    val selectedMonth: Int,
    val selectedDay: Int,
    val selectedDateMillis: Long,
    val eventsForSelectedDate: List<CalendarEventItem> = emptyList(),
    val allEventsInMonth: List<CalendarEventItem> = emptyList(),
    val isLoading: Boolean = false
)

class CalendarViewModel(private val app: LexiDeskApp) : ViewModel() {

    private val nowCal = Calendar.getInstance()
    private val _selectedYear = MutableStateFlow(nowCal.get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow(nowCal.get(Calendar.MONTH))
    private val _selectedDay = MutableStateFlow(nowCal.get(Calendar.DAY_OF_MONTH))

    private val _selectedDateMillis = MutableStateFlow(DateUtils.getStartOfDay(nowCal.timeInMillis))

    val uiState: StateFlow<CalendarUiState> = combine(
        _selectedYear,
        _selectedMonth,
        _selectedDay,
        _selectedDateMillis,
        app.reminderRepository.allReminders,
        app.taskRepository.allTasks,
        app.caseRepository.allCases
    ) { params ->
        val year = params[0] as Int
        val month = params[1] as Int
        val day = params[2] as Int
        val selectedMillis = params[3] as Long
        val reminders = params[4] as List<ReminderEntity>
        val tasks = params[5] as List<TaskEntity>
        val cases = params[6] as List<CaseEntity>

        // Aggregate unified events
        val eventList = mutableListOf<CalendarEventItem>()

        for (r in reminders) {
            eventList.add(
                CalendarEventItem(
                    id = r.id,
                    title = r.title,
                    type = r.reminderType,
                    timestamp = r.eventTimestamp,
                    relatedId = r.id,
                    isCompleted = r.isCompleted
                )
            )
        }

        for (t in tasks) {
            if (t.dueDate != null) {
                eventList.add(
                    CalendarEventItem(
                        id = t.id,
                        title = t.title,
                        type = "Task: ${t.priority}",
                        timestamp = t.dueDate,
                        relatedId = t.id,
                        isCompleted = t.status == "Completed"
                    )
                )
            }
        }

        for (c in cases) {
            if (c.dueDate != null) {
                eventList.add(
                    CalendarEventItem(
                        id = c.id,
                        title = "${c.title} (Deadline)",
                        type = "Case Hearing / Filing",
                        timestamp = c.dueDate,
                        relatedId = c.id,
                        isCompleted = c.status == "Completed"
                    )
                )
            }
        }

        val startOfDay = DateUtils.getStartOfDay(selectedMillis)
        val endOfDay = DateUtils.getEndOfDay(selectedMillis)

        val selectedEvents = eventList.filter {
            it.timestamp in startOfDay..endOfDay
        }.sortedBy { it.timestamp }

        CalendarUiState(
            selectedYear = year,
            selectedMonth = month,
            selectedDay = day,
            selectedDateMillis = selectedMillis,
            eventsForSelectedDate = selectedEvents,
            allEventsInMonth = eventList,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState(
            selectedYear = nowCal.get(Calendar.YEAR),
            selectedMonth = nowCal.get(Calendar.MONTH),
            selectedDay = nowCal.get(Calendar.DAY_OF_MONTH),
            selectedDateMillis = nowCal.timeInMillis,
            isLoading = true
        )
    )

    fun onDateSelected(year: Int, month: Int, day: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
        _selectedDay.value = day

        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _selectedDateMillis.value = cal.timeInMillis
    }

    fun onMonthChanged(delta: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, _selectedYear.value)
            set(Calendar.MONTH, _selectedMonth.value)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, delta)
        }
        _selectedYear.value = cal.get(Calendar.YEAR)
        _selectedMonth.value = cal.get(Calendar.MONTH)
        _selectedDay.value = 1
        _selectedDateMillis.value = cal.timeInMillis
    }

    fun addAppointment(
        title: String,
        description: String,
        type: String,
        timestamp: Long,
        minutesBefore: Int
    ) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                title = title.trim(),
                description = description.trim(),
                reminderType = type,
                eventTimestamp = timestamp,
                minutesBefore = minutesBefore
            )
            val id = app.reminderRepository.insertReminder(reminder)
            ReminderScheduler.scheduleReminder(app, reminder.copy(id = id))
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(app, id)
            app.reminderRepository.deleteReminderById(id)
        }
    }
}
