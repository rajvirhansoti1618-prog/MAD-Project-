package com.example.lexidesk.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.ui.common.LexiSearchBar
import com.example.lexidesk.ui.theme.AmberAccent
import com.example.lexidesk.ui.theme.BlueInfo
import com.example.lexidesk.ui.theme.GreenSuccess
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.TealSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    app: LexiDeskApp,
    onBackClick: () -> Unit,
    onClientClick: (Long) -> Unit,
    onCaseClick: (Long) -> Unit,
    onTaskClick: (Long) -> Unit,
    onDocumentClick: (Long) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val allClients by app.clientRepository.allClients.collectAsState(initial = emptyList())
    val allCases by app.caseRepository.allCases.collectAsState(initial = emptyList())
    val allTasks by app.taskRepository.allTasks.collectAsState(initial = emptyList())
    val allDocs by app.documentRepository.allDocuments.collectAsState(initial = emptyList())

    // Notes and drafts: collect full lists and filter in-memory (avoids Flow restart on every keystroke)
    val allNotes by app.noteRepository.searchNotes(query.trim()).collectAsState(initial = emptyList())
    val allDrafts by app.draftTemplateRepository.searchDrafts(query.trim()).collectAsState(initial = emptyList())

    val filteredClients = if (query.isBlank()) emptyList() else allClients.filter {
        it.fullName.contains(query, ignoreCase = true) || it.companyName.contains(query, ignoreCase = true)
    }

    val filteredCases = if (query.isBlank()) emptyList() else allCases.filter {
        it.title.contains(query, ignoreCase = true) || it.referenceNumber.contains(query, ignoreCase = true)
    }

    val filteredTasks = if (query.isBlank()) emptyList() else allTasks.filter {
        it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
    }

    val filteredDocs = if (query.isBlank()) emptyList() else allDocs.filter {
        it.title.contains(query, ignoreCase = true) || it.fileName.contains(query, ignoreCase = true)
    }

    // Notes and drafts are already filtered by the DAO query; show empty list when query is blank
    val filteredNotes = if (query.isBlank()) emptyList() else allNotes
    val filteredDrafts = if (query.isBlank()) emptyList() else allDrafts

    val hasResults = filteredClients.isNotEmpty() || filteredCases.isNotEmpty() ||
            filteredTasks.isNotEmpty() || filteredDocs.isNotEmpty() ||
            filteredNotes.isNotEmpty() || filteredDrafts.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Search") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .imePadding()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LexiSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    placeholder = "Type name, case, task, note, document title..."
                )
            }

            if (query.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type a keyword above to search everything.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (!hasResults) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results found for \"${query.trim()}\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Clients Section
                    if (filteredClients.isNotEmpty()) {
                        item { Text("Clients (${filteredClients.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
                        items(filteredClients) { client ->
                            Card(
                                modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp).clickable { onClientClick(client.id) },
                                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(client.fullName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                                }
                            }
                        }
                    }

                    // Cases Section
                    if (filteredCases.isNotEmpty()) {
                        item { Text("Cases & Matters (${filteredCases.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
                        items(filteredCases) { c ->
                            Card(
                                modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp).clickable { onCaseClick(c.id) },
                                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = TealSecondary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(c.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                                }
                            }
                        }
                    }

                    // Tasks Section
                    if (filteredTasks.isNotEmpty()) {
                        item { Text("Tasks (${filteredTasks.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
                        items(filteredTasks) { task ->
                            Card(
                                modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp).clickable { onTaskClick(task.id) },
                                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Assignment, contentDescription = null, tint = BlueInfo)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(task.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                                }
                            }
                        }
                    }

                    // Documents Section
                    if (filteredDocs.isNotEmpty()) {
                        item { Text("Documents (${filteredDocs.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
                        items(filteredDocs) { doc ->
                            Card(
                                modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp).clickable { onDocumentClick(doc.id) },
                                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(doc.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                                }
                            }
                        }
                    }

                    // Notes Section
                    if (filteredNotes.isNotEmpty()) {
                        item { Text("Notes (${filteredNotes.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
                        items(filteredNotes) { note ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Note, contentDescription = null, tint = GreenSuccess)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(note.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                                        if (note.content.isNotBlank()) {
                                            Text(
                                                note.content.take(80) + if (note.content.length > 80) "…" else "",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Draft Templates Section
                    if (filteredDrafts.isNotEmpty()) {
                        item { Text("Draft Templates (${filteredDrafts.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
                        items(filteredDrafts) { draft ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Article, contentDescription = null, tint = AmberAccent)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(draft.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                                        Text(draft.templateType, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
