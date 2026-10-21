package com.example.lexidesk.ui.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lexidesk.data.entity.TaskEntity
import com.example.lexidesk.ui.common.EmptyStateView
import com.example.lexidesk.ui.common.LexiSearchBar
import com.example.lexidesk.ui.common.PriorityBadge
import com.example.lexidesk.ui.common.StatusBadge
import com.example.lexidesk.ui.theme.GreenSuccess
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.RedUrgent
import com.example.lexidesk.ui.theme.TealSecondary
import com.example.lexidesk.utils.DateUtils

@Composable
fun TasksListScreen(
    viewModel: TasksViewModel,
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit
) {
    val state by viewModel.tasksListUiState.collectAsState()
    val clients by viewModel.clientsFlow.collectAsState()
    val cases by viewModel.casesFlow.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = NavyPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tasks & Follow-ups",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                LexiSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = "Search tasks..."
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips (All, Today, Upcoming, Overdue, Completed)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filters = listOf("All", "Today", "Upcoming", "Overdue", "Completed")
                    items(filters) { filter ->
                        FilterChip(
                            selected = state.activeFilter == filter,
                            onClick = { viewModel.onActiveFilterChanged(filter) },
                            label = { Text(filter) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Priority Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Priority:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val priorities = listOf("All", "Urgent", "High", "Medium", "Low")
                        items(priorities) { prio ->
                            FilterChip(
                                selected = state.priorityFilter == prio,
                                onClick = { viewModel.onPriorityFilterChanged(prio) },
                                label = { Text(prio, fontSize = 12.sp) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Sort By Chips (Due Date, Priority, Title)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    listOf("Due Date", "Priority", "Title").forEach { sortOption ->
                        FilterChip(
                            selected = state.sortBy == sortOption,
                            onClick = { viewModel.onSortByChanged(sortOption) },
                            label = { Text(sortOption) }
                        )
                    }
                }
            }

            if (state.tasks.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Assignment,
                    title = if (state.searchQuery.isNotBlank()) "No Matching Tasks" else "No Tasks Found",
                    description = if (state.searchQuery.isNotBlank()) "Try altering your search or active filter." else "Create actionable tasks with deadlines and notification alerts.",
                    actionButtonText = if (state.searchQuery.isBlank()) "Create Task" else null,
                    onActionClick = onAddTaskClick
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.tasks) { task ->
                        val clientName = clients.find { it.id == task.clientId }?.fullName
                        val caseName = cases.find { it.id == task.caseId }?.title
                        TaskListItemCard(
                            task = task,
                            clientName = clientName,
                            caseName = caseName,
                            onToggleComplete = { viewModel.toggleTask(task) },
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskListItemCard(
    task: TaskEntity,
    clientName: String? = null,
    caseName: String? = null,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit
) {
    val isCompleted = task.status == "Completed"
    val isOverdue = DateUtils.isOverdue(task.dueDate) && !isCompleted

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        border = if (isOverdue) BorderStroke(1.dp, RedUrgent.copy(alpha = 0.5f)) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle completion",
                    tint = if (isCompleted) GreenSuccess else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                if (clientName != null || caseName != null) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = listOfNotNull(clientName, caseName).joinToString(" • "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (task.dueDate != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isOverdue) Icons.Default.Warning else Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = if (isOverdue) RedUrgent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${DateUtils.formatDate(task.dueDate)}${if (task.dueTime != null) ", ${task.dueTime}" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isOverdue) RedUrgent else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    if (task.reminderEnabled) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Reminder active",
                            tint = TealSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PriorityBadge(priority = task.priority)
                if (isOverdue) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = RedUrgent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "OVERDUE",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = RedUrgent,
                            fontSize = 9.sp
                        )
                    }
                }
            }


    }
}
}
