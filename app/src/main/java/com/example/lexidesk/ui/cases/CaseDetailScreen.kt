package com.example.lexidesk.ui.cases

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lexidesk.ui.common.ConfirmationDialog
import com.example.lexidesk.ui.common.PriorityBadge
import com.example.lexidesk.ui.common.StatusBadge
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.RedUrgent
import com.example.lexidesk.ui.theme.TealSecondary
import com.example.lexidesk.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseDetailScreen(
    caseId: Long,
    viewModel: CasesViewModel,
    onBackClick: () -> Unit,
    onEditCaseClick: (Long) -> Unit,
    onClientClick: (Long) -> Unit,
    onAddTaskClick: (Long) -> Unit,
    onAddDocumentClick: (Long) -> Unit
) {
    val detailState by viewModel.getCaseDetailState(caseId).collectAsState()
    val context = LocalContext.current
    val caseItem = detailState.caseItem
    val client = detailState.client

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirmDialog) {
        ConfirmationDialog(
            title = "Delete Case Record?",
            message = "Are you sure you want to permanently delete '${caseItem?.title ?: "this case"}'?",
            confirmText = "Delete Permanently",
            onConfirm = {
                showDeleteConfirmDialog = false
                viewModel.deleteCase(caseId, onDeleted = onBackClick)
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }

    if (showAddNoteDialog) {
        var noteTitle by remember { mutableStateOf("") }
        var noteContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Add Case Proceeding Note", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Hearing / Meeting Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Record of Proceedings / Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank()) {
                            viewModel.addNote(caseId, noteTitle, noteContent)
                            showAddNoteDialog = false
                        }
                    }
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddNoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(caseItem?.title ?: "Case Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        caseItem?.let { c ->
                            val shareText = buildString {
                                appendLine("--- LexiDesk Case Summary ---")
                                appendLine("Title: ${c.title}")
                                appendLine("Category: ${c.category}")
                                appendLine("Priority: ${c.priority} | Status: ${c.status}")
                                if (c.referenceNumber.isNotBlank()) appendLine("Ref #: ${c.referenceNumber}")
                                if (c.courtOrLocation.isNotBlank()) appendLine("Court / Forum: ${c.courtOrLocation}")
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Case Summary: ${c.title}")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Case Info"))
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Case")
                    }
                    IconButton(onClick = { onEditCaseClick(caseId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Case")
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Case", tint = RedUrgent)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (caseItem == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Loading case details...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Overview Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = caseItem.title,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                PriorityBadge(priority = caseItem.priority)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(status = caseItem.status)
                                Text(
                                    text = "Ref: ${caseItem.referenceNumber.ifBlank { "N/A" }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (caseItem.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = caseItem.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Start Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(DateUtils.formatDate(caseItem.startDate), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                                }
                                Column {
                                    Text("Due / Hearing Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = DateUtils.formatDate(caseItem.dueDate),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = if (DateUtils.isOverdue(caseItem.dueDate)) RedUrgent else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (caseItem.courtOrLocation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TealSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(caseItem.courtOrLocation, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                // Linked Client Card
                item {
                    if (client != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onClientClick(client.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Client / Organization", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(client.fullName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                }
                                Text("View", style = MaterialTheme.typography.labelMedium, color = TealSecondary)
                            }
                        }
                    }
                }

                // Sub-tabs: Tasks, Documents, Notes
                item {
                    val tabs = listOf("Tasks (${detailState.tasks.size})", "Documents (${detailState.documents.size})", "Notes (${detailState.notes.size})")
                    PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                when (selectedTabIndex) {
                    0 -> { // Tasks
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Associated Tasks", style = MaterialTheme.typography.titleMedium)
                                OutlinedButton(onClick = { onAddTaskClick(caseId) }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Task", fontSize = 12.sp)
                                }
                            }
                        }
                        if (detailState.tasks.isEmpty()) {
                            item { Text("No tasks associated with this case yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            items(detailState.tasks) { task ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(task.title, style = MaterialTheme.typography.titleMedium)
                                            Text("Due: ${DateUtils.formatDate(task.dueDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        StatusBadge(status = task.status)
                                    }
                                }
                            }
                        }
                    }

                    1 -> { // Documents
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Case Documents & Briefs", style = MaterialTheme.typography.titleMedium)
                                OutlinedButton(onClick = { onAddDocumentClick(caseId) }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Document", fontSize = 12.sp)
                                }
                            }
                        }
                        if (detailState.documents.isEmpty()) {
                            item { Text("No documents attached to this case.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            items(detailState.documents) { doc ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(doc.title, style = MaterialTheme.typography.titleMedium)
                                            Text("${doc.category} • ${doc.fileName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> { // Notes
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Hearing & Proceeding Notes", style = MaterialTheme.typography.titleMedium)
                                OutlinedButton(onClick = { showAddNoteDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Note", fontSize = 12.sp)
                                }
                            }
                        }
                        if (detailState.notes.isEmpty()) {
                            item { Text("No notes logged for this case.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            items(detailState.notes) { note ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(note.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Logged: ${DateUtils.formatDate(note.createdAt)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
