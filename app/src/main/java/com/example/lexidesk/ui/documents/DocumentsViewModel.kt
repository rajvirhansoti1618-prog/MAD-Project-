package com.example.lexidesk.ui.documents

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexidesk.LexiDeskApp
import com.example.lexidesk.data.entity.CaseEntity
import com.example.lexidesk.data.entity.ClientEntity
import com.example.lexidesk.data.entity.DocumentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class DocumentsListUiState(
    val documents: List<DocumentEntity> = emptyList(),
    val searchQuery: String = "",
    val categoryFilter: String = "All",
    val isLoading: Boolean = false
)

class DocumentsViewModel(private val app: LexiDeskApp) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _categoryFilter = MutableStateFlow("All")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    val clientsFlow: StateFlow<List<ClientEntity>> = app.clientRepository.allClients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val casesFlow: StateFlow<List<CaseEntity>> = app.caseRepository.allCases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val documentsUiState: StateFlow<DocumentsListUiState> = combine(
        app.documentRepository.allDocuments,
        _searchQuery,
        _categoryFilter
    ) { allDocs, query, cat ->
        val filtered = allDocs.filter { doc ->
            val matchesQuery = if (query.isBlank()) true else {
                doc.title.contains(query, ignoreCase = true) ||
                        doc.fileName.contains(query, ignoreCase = true) ||
                        doc.category.contains(query, ignoreCase = true) ||
                        doc.tags.contains(query, ignoreCase = true)
            }
            val matchesCat = if (cat == "All") true else doc.category.equals(cat, ignoreCase = true)
            matchesQuery && matchesCat
        }
        DocumentsListUiState(
            documents = filtered,
            searchQuery = query,
            categoryFilter = cat,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DocumentsListUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onCategoryFilterChanged(category: String) { _categoryFilter.value = category }

    fun importFileAndSave(
        context: Context,
        uri: Uri,
        title: String,
        category: String,
        clientId: Long?,
        caseId: Long?,
        description: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val (savedPath, fileName, fileSize) = withContext(Dispatchers.IO) {
                var name = "Document_${System.currentTimeMillis()}"
                var size = 0L

                // Copy stream to app internal storage
                val destFile = File(context.filesDir, "doc_${System.currentTimeMillis()}.bin")
                try {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    size = destFile.length()
                    name = uri.lastPathSegment?.substringAfterLast('/') ?: destFile.name
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Triple(destFile.absolutePath, name, size)
            }

            val doc = DocumentEntity(
                title = title.ifBlank { fileName },
                fileName = fileName,
                filePath = savedPath,
                fileType = if (fileName.endsWith(".pdf", ignoreCase = true)) "pdf" else "image",
                fileSize = fileSize,
                category = category,
                clientId = clientId,
                caseId = caseId,
                description = description
            )

            val newId = app.documentRepository.insertDocument(doc)
            onSuccess(newId)
        }
    }

    fun deleteDocument(doc: DocumentEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val file = File(doc.filePath)
                    if (file.exists()) file.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            app.documentRepository.deleteDocument(doc)
        }
    }
}
