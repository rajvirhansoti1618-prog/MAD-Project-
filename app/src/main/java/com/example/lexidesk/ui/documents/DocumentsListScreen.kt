package com.example.lexidesk.ui.documents

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.core.content.FileProvider
import com.example.lexidesk.data.entity.DocumentEntity
import com.example.lexidesk.pdf.PdfGenerator
import com.example.lexidesk.ui.common.ConfirmationDialog
import com.example.lexidesk.ui.common.EmptyStateView
import com.example.lexidesk.ui.common.LexiSearchBar
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.RedUrgent
import com.example.lexidesk.ui.theme.TealSecondary
import com.example.lexidesk.utils.DateUtils
import java.io.File

@Composable
fun DocumentsListScreen(
    viewModel: DocumentsViewModel,
    onAddDocumentClick: () -> Unit,
    onScannerClick: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.documentsUiState.collectAsState()
    var docToDelete by remember { mutableStateOf<DocumentEntity?>(null) }

    if (docToDelete != null) {
        ConfirmationDialog(
            title = "Delete Document?",
            message = "Are you sure you want to delete '${docToDelete?.title}' from the vault?",
            confirmText = "Delete Permanently",
            onConfirm = {
                docToDelete?.let { viewModel.deleteDocument(it) }
                docToDelete = null
            },
            onDismiss = { docToDelete = null }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDocumentClick,
                containerColor = NavyPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import Document")
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Document Vault",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedButton(
                        onClick = onScannerClick,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Scan OCR", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LexiSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = "Search documents by title, file name, category..."
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Category filter chips
                val categories = listOf("All", "Contracts", "Tax Documents", "Court Documents", "Client Agreements", "Identity Documents", "Invoices and Receipts")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = state.categoryFilter == cat,
                            onClick = { viewModel.onCategoryFilterChanged(cat) },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            if (state.documents.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Description,
                    title = if (state.searchQuery.isNotBlank()) "No Matching Documents" else "Vault is Empty",
                    description = if (state.searchQuery.isNotBlank()) "Try resetting your search query or category." else "Import client PDFs, scanned contracts, or agreements into app storage.",
                    actionButtonText = if (state.searchQuery.isBlank()) "Import Document" else null,
                    onActionClick = onAddDocumentClick
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.documents) { doc ->
                        DocumentListItemCard(
                            doc = doc,
                            onOpen = {
                                val file = File(doc.filePath)
                                if (file.exists()) {
                                    PdfGenerator.viewPdf(context, file)
                                }
                            },
                            onShare = {
                                val file = File(doc.filePath)
                                if (file.exists()) {
                                    PdfGenerator.sharePdf(context, file, doc.title)
                                }
                            },
                            onDelete = { docToDelete = doc }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentListItemCard(
    doc: DocumentEntity,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (doc.fileType == "pdf") RedUrgent.copy(alpha = 0.12f) else TealSecondary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (doc.fileType == "pdf") Icons.Default.PictureAsPdf else Icons.Default.Description,
                    contentDescription = null,
                    tint = if (doc.fileType == "pdf") RedUrgent else TealSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doc.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${doc.category} • ${doc.fileName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Added on ${DateUtils.formatDate(doc.createdAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = RedUrgent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
