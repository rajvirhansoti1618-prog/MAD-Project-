package com.example.lexidesk.ui.clients

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.lexidesk.data.entity.NoteEntity
import com.example.lexidesk.ui.common.ConfirmationDialog
import com.example.lexidesk.ui.common.PriorityBadge
import com.example.lexidesk.ui.common.StatusBadge
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.RedUrgent
import com.example.lexidesk.ui.theme.TealSecondary
import com.example.lexidesk.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    clientId: Long,
    viewModel: ClientsViewModel,
    onBackClick: () -> Unit,
    onEditClientClick: (Long) -> Unit,
    onCaseClick: (Long) -> Unit,
    onAddCaseClick: (Long) -> Unit,
    onAddTaskClick: (Long) -> Unit,
    onAddDocumentClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val detailState by viewModel.getClientDetailState(clientId).collectAsState()
    val client = detailState.client

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<NoteEntity?>(null) }
    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }

    if (showDeleteConfirmDialog) {
        ConfirmationDialog(
            title = "Delete Client Record?",
            message = "Are you sure you want to delete ${client?.fullName ?: "this client"}? Associated cases and tasks will remain archived.",
            confirmText = "Delete Permanently",
            onConfirm = {
                showDeleteConfirmDialog = false
                viewModel.deleteClient(clientId, onDeleted = onBackClick)
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }

    if (noteToDelete != null) {
        val target = noteToDelete!!
        ConfirmationDialog(
            title = "Delete Note?",
            message = "Are you sure you want to delete note \"${target.title}\"?",
            confirmText = "Delete",
            onConfirm = {
                viewModel.deleteNote(target)
                noteToDelete = null
            },
            onDismiss = { noteToDelete = null }
        )
    }

    if (noteToEdit != null) {
        val target = noteToEdit!!
        var editTitle by remember(target) { mutableStateOf(target.title) }
        var editContent by remember(target) { mutableStateOf(target.content) }

        AlertDialog(
            onDismissRequest = { noteToEdit = null },
            title = { Text("Edit Client Note", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Note Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editContent,
                        onValueChange = { editContent = it },
                        label = { Text("Content / Follow-up Details") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTitle.isNotBlank()) {
                            viewModel.updateNote(target.copy(title = editTitle.trim(), content = editContent.trim()))
                            noteToEdit = null
                        }
                    }
                ) {
                    Text("Update Note")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { noteToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddNoteDialog) {
        var noteTitle by remember { mutableStateOf("") }
        var noteContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("Add Client Note", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Note Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Content / Follow-up Details") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank()) {
                            viewModel.addNote(clientId, noteTitle, noteContent)
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
                title = { Text(client?.fullName ?: "Client Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        client?.let { c ->
                            val shareText = buildString {
                                appendLine("--- LexiDesk Client Contact ---")
                                appendLine("Name: ${c.fullName}")
                                if (c.companyName.isNotBlank()) appendLine("Company: ${c.companyName}")
                                appendLine("Type: ${c.clientType}")
                                if (c.phone.isNotBlank()) appendLine("Phone: ${c.phone}")
                                if (c.email.isNotBlank()) appendLine("Email: ${c.email}")
                                if (c.address.isNotBlank()) appendLine("Address: ${c.address}")
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Client Contact: ${c.fullName}")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Client Info"))
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Client")
                    }
                    IconButton(onClick = { onEditClientClick(clientId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Client")
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Client", tint = RedUrgent)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (client == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading client profile...", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                // Client Info Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(if (client.clientType == "Business") TealSecondary.copy(alpha = 0.15f) else NavyPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (client.clientType == "Business") Icons.Default.Business else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (client.clientType == "Business") TealSecondary else NavyPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = client.fullName,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (client.companyName.isNotBlank()) "${client.clientType} • ${client.companyName}" else client.clientType,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (client.address.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = client.address,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (client.tags.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tags: ${client.tags}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TealSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Contact Action Buttons: Dial & Email
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (client.phone.isNotBlank()) {
                                    Button(
                                        onClick = {
                                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${client.phone}"))
                                            context.startActivity(dialIntent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Call", fontSize = 13.sp)
                                    }
                                }

                                if (client.email.isNotBlank()) {
                                    Button(
                                        onClick = {
                                            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${client.email}"))
                                            context.startActivity(emailIntent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = TealSecondary)
                                    ) {
                                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Email", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Tabs for Associated items
                item {
                    val tabs = listOf("Cases (${detailState.cases.size})", "Tasks (${detailState.tasks.size})", "Docs (${detailState.documents.size})", "Notes (${detailState.notes.size})")
                    TabRow(selectedTabIndex = selectedTabIndex) {
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
                    0 -> { // Cases Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Client Cases & Matters", style = MaterialTheme.typography.titleMedium)
                                OutlinedButton(onClick = { onAddCaseClick(clientId) }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ New Case", fontSize = 12.sp)
                                }
                            }
                        }
                        if (detailState.cases.isEmpty()) {
                            item {
                                Text("No cases associated with this client yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            items(detailState.cases) { caseItem ->
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
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(caseItem.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                            PriorityBadge(priority = caseItem.priority)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(caseItem.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Due: ${DateUtils.formatDate(caseItem.dueDate)}", style = MaterialTheme.typography.bodySmall)
                                            StatusBadge(status = caseItem.status)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> { // Tasks Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Client Tasks", style = MaterialTheme.typography.titleMedium)
                                OutlinedButton(onClick = { onAddTaskClick(clientId) }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ New Task", fontSize = 12.sp)
                                }
                            }
                        }
                        if (detailState.tasks.isEmpty()) {
                            item {
                                Text("No tasks associated with this client.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            items(detailState.tasks) { task ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
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

                    2 -> { // Documents Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Client Documents", style = MaterialTheme.typography.titleMedium)
                                OutlinedButton(onClick = { onAddDocumentClick(clientId) }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Document", fontSize = 12.sp)
                                }
                            }
                        }
                        if (detailState.documents.isEmpty()) {
                            item {
                                Text("No documents attached to this client.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            items(detailState.documents) { doc ->
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
                                        Icon(Icons.Default.Description, contentDescription = null, tint = NavyPrimary)
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

                    3 -> { // Notes Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Consultation & Case Notes", style = MaterialTheme.typography.titleMedium)
                                OutlinedButton(onClick = { showAddNoteDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Note", fontSize = 12.sp)
                                }
                            }
                        }
                        if (detailState.notes.isEmpty()) {
                            item {
                                Text("No notes recorded yet. Add notes for follow-up reminders.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            items(detailState.notes) { note ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
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
                                                note.title,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Row {
                                                IconButton(
                                                    onClick = { noteToEdit = note },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Note", modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(
                                                    onClick = { noteToDelete = note },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = RedUrgent, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Logged on ${DateUtils.formatDate(note.createdAt)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
