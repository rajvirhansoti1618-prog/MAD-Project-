package com.example.lexidesk.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lexidesk.ui.common.PriorityBadge
import com.example.lexidesk.ui.common.StatusBadge
import com.example.lexidesk.ui.common.SummaryMetricCard
import com.example.lexidesk.ui.theme.AmberAccent
import com.example.lexidesk.ui.theme.BlueInfo
import com.example.lexidesk.ui.theme.GreenSuccess
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.RedUrgent
import com.example.lexidesk.ui.theme.SlateBorder
import com.example.lexidesk.ui.theme.TealSecondary
import com.example.lexidesk.utils.DateUtils

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToClients: () -> Unit,
    onNavigateToCases: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onAddClient: () -> Unit,
    onAddCase: () -> Unit,
    onAddTask: () -> Unit,
    onCaseClick: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Header with greeting and profile
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (state.userProfile.name.isNotBlank()) "Hello, ${state.userProfile.name}" else "Welcome to LexiDesk",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (state.userProfile.firmName.isNotBlank()) state.userProfile.firmName else "${state.userProfile.profession} Workspace",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onNavigateToSearch,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 2. Demo Data / Viva Helper Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isSampleDataLoaded) TealSecondary.copy(alpha = 0.08f) else AmberAccent.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (state.isSampleDataLoaded) "Demo Dataset Active" else "Quick Viva / Demo Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (state.isSampleDataLoaded) "Preloaded sample clients, cases & tasks ready." else "Load sample data to test all features instantly.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!state.isSampleDataLoaded) {
                        Button(
                            onClick = { viewModel.loadSampleData() },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Load Demo", fontSize = 12.sp)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.clearAllData() },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reset Data", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 3. Metric Summary Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryMetricCard(
                        title = "Clients",
                        value = state.clientCount.toString(),
                        icon = Icons.Default.People,
                        iconColor = NavyPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToClients
                    )
                    SummaryMetricCard(
                        title = "Active Cases",
                        value = state.activeCasesCount.toString(),
                        icon = Icons.Default.Folder,
                        iconColor = TealSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCases
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryMetricCard(
                        title = "Pending Tasks",
                        value = state.pendingTasksCount.toString(),
                        icon = Icons.Default.Assignment,
                        iconColor = BlueInfo,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToTasks
                    )
                    SummaryMetricCard(
                        title = "Overdue",
                        value = state.overdueTasksCount.toString(),
                        icon = Icons.Default.Warning,
                        iconColor = if (state.overdueTasksCount > 0) RedUrgent else SlateBorder,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToTasks
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryMetricCard(
                        title = "Vault Documents",
                        value = state.documentCount.toString(),
                        icon = Icons.Default.Description,
                        iconColor = NavyPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDocuments
                    )
                    SummaryMetricCard(
                        title = "Today's Agenda",
                        value = (state.todayTasks.size + state.todayReminders.size).toString(),
                        icon = Icons.Default.CalendarMonth,
                        iconColor = TealSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCalendar
                    )
                }
            }
        }

        // 4. Quick Actions Row
        item {
            Column {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        QuickActionButton(
                            icon = Icons.Default.People,
                            label = "+ Client",
                            onClick = onAddClient
                        )
                    }
                    item {
                        QuickActionButton(
                            icon = Icons.Default.Gavel,
                            label = "+ Case",
                            onClick = onAddCase
                        )
                    }
                    item {
                        QuickActionButton(
                            icon = Icons.Default.Assignment,
                            label = "+ Task",
                            onClick = onAddTask
                        )
                    }
                    item {
                        QuickActionButton(
                            icon = Icons.Default.Description,
                            label = "Vault",
                            onClick = onNavigateToDocuments
                        )
                    }
                    item {
                        QuickActionButton(
                            icon = Icons.Default.CameraAlt,
                            label = "Scan",
                            onClick = onNavigateToScanner
                        )
                    }
                    item {
                        QuickActionButton(
                            icon = Icons.Default.CalendarMonth,
                            label = "Calendar",
                            onClick = onNavigateToCalendar
                        )
                    }
                }
            }
        }

        // 5. Today's Agenda
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Agenda",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${state.todayTasks.size + state.todayReminders.size} Scheduled",
                        style = MaterialTheme.typography.labelLarge,
                        color = TealSecondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (state.todayTasks.isEmpty() && state.todayReminders.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "No appointments or tasks scheduled for today. You're all caught up!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(state.todayReminders) { reminder ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AmberAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reminder.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${reminder.reminderType} • ${DateUtils.formatTime(reminder.eventTimestamp)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(state.todayTasks) { task ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleTask(task) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (task.status == "Completed") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Toggle status",
                            tint = if (task.status == "Completed") GreenSuccess else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (task.dueTime != null) {
                            Text(
                                text = "Due at ${task.dueTime}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    PriorityBadge(priority = task.priority)
                }
            }
        }

        // 6. Upcoming Deadlines & Cases
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Deadlines",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelLarge,
                        color = TealSecondary,
                        modifier = Modifier.clickable { onNavigateToCases() }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (state.upcomingCases.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "No upcoming case deadlines recorded. Add deadlines in the Work section.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(state.upcomingCases) { caseItem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCaseClick(caseItem.id) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = caseItem.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        PriorityBadge(priority = caseItem.priority)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${caseItem.category} • Ref: ${caseItem.referenceNumber.ifBlank { "N/A" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Due: ${DateUtils.formatDate(caseItem.dueDate)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (DateUtils.isOverdue(caseItem.dueDate)) RedUrgent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        StatusBadge(status = caseItem.status)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(NavyPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = NavyPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
