package com.example.lexidesk.ui.documents

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.TealSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentScreen(
    initialClientId: Long?,
    initialCaseId: Long?,
    viewModel: DocumentsViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: (Long) -> Unit
) {
    val context = LocalContext.current
    val clients by viewModel.clientsFlow.collectAsState()
    val cases by viewModel.casesFlow.collectAsState()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Contracts") }
    var selectedClientId by remember { mutableStateOf<Long?>(initialClientId) }
    var selectedCaseId by remember { mutableStateOf<Long?>(initialCaseId) }
    var description by remember { mutableStateOf("") }

    var errorMsg by remember { mutableStateOf<String?>(null) }
    var clientDropdownExpanded by remember { mutableStateOf(false) }
    var caseDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            val name = uri.lastPathSegment?.substringAfterLast('/') ?: "Imported_Document"
            if (title.isBlank()) {
                title = name.substringBeforeLast('.')
            }
            errorMsg = null
        }
    }

    val categories = listOf(
        "Contracts",
        "Client Agreements",
        "Identity Documents",
        "Court Documents",
        "Tax Documents",
        "Invoices and Receipts",
        "Financial Statements",
        "Applications and Letters",
        "Other"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import Document to Vault") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // File Selection Drop-zone Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.5.dp,
                        color = if (selectedUri != null) TealSecondary else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { filePickerLauncher.launch("*/*") },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (selectedUri != null) Icons.Default.Description else Icons.Default.FileUpload,
                        contentDescription = null,
                        tint = if (selectedUri != null) TealSecondary else NavyPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedUri != null) "File Selected: ${selectedUri?.lastPathSegment?.substringAfterLast('/')}" else "Tap to Select PDF or Document",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Supports PDF, images, Word, receipts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (errorMsg != null) {
                Text(text = errorMsg ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Document Display Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category Selector
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Document Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

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
                                caseDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Save Button
            Button(
                onClick = {
                    val uri = selectedUri
                    if (uri == null) {
                        errorMsg = "Please pick a document file first"
                        return@Button
                    }
                    if (title.isBlank()) {
                        errorMsg = "Document title is required"
                        return@Button
                    }
                    viewModel.importFileAndSave(
                        context = context,
                        uri = uri,
                        title = title,
                        category = category,
                        clientId = selectedClientId,
                        caseId = selectedCaseId,
                        description = description,
                        onSuccess = onSaveSuccess
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Securely Import to Vault")
            }
        }
    }
}
