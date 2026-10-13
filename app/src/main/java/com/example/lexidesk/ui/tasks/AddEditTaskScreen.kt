package com.example.lexidesk.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lexidesk.ui.common.ConfirmationDialog
import com.example.lexidesk.ui.common.DatePickerModalDialog
import com.example.lexidesk.ui.common.TimePickerModalDialog
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.RedUrgent
import com.example.lexidesk.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: Long?,
    initialClientId: Long?,
    initialCaseId: Long?,
    viewModel: TasksViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: (Long) -> Unit
) {
    val clients by viewModel.clientsFlow.collectAsState()
    val cases by viewModel.casesFlow.collectAsState()

    var selectedClientId by remember { mutableStateOf<Long?>(initialClientId) }
    var selectedCaseId by remember { mutableStateOf<Long?>(initialCaseId) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var status by remember { mutableStateOf("Pending") }
    var dueDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var dueTime by remember { mutableStateOf("05:00 PM") }
    var reminderEnabled by remember { mutableStateOf(true) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    var clientDropdownExpanded by remember { mutableStateOf(false) }
    var caseDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (taskId != null && taskId > 0) {
            val existing = viewModel.getTaskByIdOnce(taskId)
            if (existing != null) {
                title = existing.title
                description = existing.description
                selectedClientId = existing.clientId
                selectedCaseId = existing.caseId
                priority = existing.priority
                status = existing.status
                dueDate = existing.dueDate
                dueTime = existing.dueTime ?: "05:00 PM"
                reminderEnabled = existing.reminderEnabled
            }
        }
    }

    if (showDatePicker) {
        DatePickerModalDialog(
            initialDateMillis = dueDate,
            onDateSelected = { dueDate = it },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        TimePickerModalDialog(
            onTimeSelected = { dueTime = it },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showDeleteConfirmDialog) {
        ConfirmationDialog(
            title = "Delete Task?",
            message = "Are you sure you want to delete this task?",
            confirmText = "Delete",
            onConfirm = {
                showDeleteConfirmDialog = false
                taskId?.let { viewModel.deleteTask(it, onDeleted = onBackClick) }
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId != null && taskId > 0) "Edit Task" else "Add New Task") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (taskId != null && taskId > 0) {
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = RedUrgent)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Title (Required)
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) titleError = null
                },
                label = { Text("Task Title *") },
                leadingIcon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Client Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = clientDropdownExpanded,
                onExpandedChange = { clientDropdownExpanded = it }
            ) {
                val selectedClientName = clients.find { it.id == selectedClientId }?.fullName ?: "Select Client (Optional)"
                OutlinedTextField(
                    value = selectedClientName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Associated Client") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = clientDropdownExpanded,
                    onDismissRequest = { clientDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = {
                            selectedClientId = null
                            clientDropdownExpanded = false
                        }
                    )
                    clients.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.fullName) },
                            onClick = {
                                selectedClientId = c.id
                                clientDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Case Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = caseDropdownExpanded,
                onExpandedChange = { caseDropdownExpanded = it }
            ) {
                val selectedCaseName = cases.find { it.id == selectedCaseId }?.title ?: "Select Case / Matter (Optional)"
                OutlinedTextField(
                    value = selectedCaseName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Associated Case / Matter") },
                    leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = caseDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = caseDropdownExpanded,
                    onDismissRequest = { caseDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = {
                            selectedCaseId = null
                            caseDropdownExpanded = false
                        }
                    )
                    cases.forEach { cs ->
                        DropdownMenuItem(
                            text = { Text(cs.title) },
                            onClick = {
                                selectedCaseId = cs.id
                                clientDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Priority Selector Chips
            Column {
                Text("Priority", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Low", "Medium", "High", "Urgent").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) }
                        )
                    }
                }
            }

            // Status Selector Chips
            Column {
                Text("Status", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Pending", "In Progress", "Completed").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s) }
                        )
                    }
                }
            }

            // Due Date & Time Pickers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f).clickable { showDatePicker = true }) {
                    OutlinedTextField(
                        value = DateUtils.formatDate(dueDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Due Date") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                }

                Box(modifier = Modifier.weight(1f).clickable { showTimePicker = true }) {
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Due Time") },
                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                }
            }

            // Reminder Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Enable Reminder Notification", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                    Text("Trigger Android notification alert before deadline", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = { reminderEnabled = it }
                )
            }

            // Task Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Task Description / Instructions") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Save Task Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = "Task title is required"
                        return@Button
                    }
                    viewModel.saveTask(
                        id = taskId ?: 0L,
                        title = title,
                        description = description,
                        clientId = selectedClientId,
                        caseId = selectedCaseId,
                        priority = priority,
                        status = status,
                        dueDate = dueDate,
                        dueTime = dueTime,
                        reminderEnabled = reminderEnabled,
                        onSuccess = onSaveSuccess
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save Task")
            }
        }
    }
}
