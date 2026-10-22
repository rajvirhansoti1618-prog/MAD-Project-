package com.example.lexidesk.ui.search

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
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
import com.example.lexidesk.ui.theme.BlueInfo
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
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LexiSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    placeholder = "Type name, case, task, document title..."
                )
            }

            if (query.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Type a keyword above to search everything.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (filteredClients.isEmpty() && filteredCases.isEmpty() && filteredTasks.isEmpty() && filteredDocs.isEmpty()) {
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
                                modifier = Modifier.fillMaxWidth().clickable { onClientClick(client.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.People, contentDescription = null, tint = NavyPrimary)
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
                                modifier = Modifier.fillMaxWidth().clickable { onCaseClick(c.id) },
                                shape = RoundedCornerShape(10.dp),
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
                                modifier = Modifier.fillMaxWidth().clickable { onTaskClick(task.id) },
                                shape = RoundedCornerShape(10.dp),
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
                                modifier = Modifier.fillMaxWidth().clickable { onDocumentClick(doc.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = NavyPrimary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(doc.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
