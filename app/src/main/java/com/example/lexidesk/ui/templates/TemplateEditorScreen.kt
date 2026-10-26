package com.example.lexidesk.ui.templates

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.ui.theme.TealSecondary
import com.example.lexidesk.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateEditorScreen(
    draftId: Long?,
    templateType: String?,
    viewModel: TemplatesViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clients by viewModel.clientsFlow.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedClientId by remember { mutableStateOf<Long?>(null) }
    var clientDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(draftId, templateType) {
        if (draftId != null && draftId > 0) {
            val draft = viewModel.getDraftByIdOnce(draftId)
            if (draft != null) {
                title = draft.title
                content = draft.content
                selectedClientId = draft.clientId
            }
        } else if (templateType != null) {
            val template = viewModel.sampleTemplates.find { it.type == templateType }
            if (template != null) {
                title = template.title
                content = template.defaultContent
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Draft Document") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveDraft(
                                id = draftId ?: 0L,
                                type = templateType ?: "Custom",
                                title = title.ifBlank { "Untitled Draft" },
                                content = content,
                                clientId = selectedClientId,
                                onSaved = {
                                    Toast.makeText(context, "Draft Saved", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Draft", tint = NavyPrimary)
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
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Document Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Document Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Client Selector Dropdown for Smart Placeholders
            ExposedDropdownMenuBox(
                expanded = clientDropdownExpanded,
                onExpandedChange = { clientDropdownExpanded = it }
            ) {
                val selectedClient = clients.find { it.id == selectedClientId }
                val clientName = selectedClient?.fullName ?: "Autofill from Client (Optional)"
                OutlinedTextField(
                    value = clientName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Client to Replace Placeholders") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = clientDropdownExpanded,
                    onDismissRequest = { clientDropdownExpanded = false }
                ) {
                    clients.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.fullName) },
                            onClick = {
                                selectedClientId = c.id
                                clientDropdownExpanded = false

                                // Autofill placeholders in content
                                val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
                                content = content
                                    .replace("[CLIENT_NAME]", c.fullName)
                                    .replace("[COMPANY_NAME]", c.companyName.ifBlank { c.fullName })
                                    .replace("[ADDRESS]", c.address.ifBlank { "Local Jurisdiction" })
                                    .replace("[DATE]", dateStr)
                                    .replace("[FIRM_NAME]", "LexiDesk Legal & Tax Advisory")

                                Toast.makeText(context, "Placeholders filled with ${c.fullName}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // Quick Fill Button
            OutlinedButton(
                onClick = {
                    val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
                    content = content
                        .replace("[DATE]", dateStr)
                        .replace("[FIRM_NAME]", "LexiDesk Legal & Tax Advisory")
                        .replace("[AMOUNT]", "INR 25,000/-")
                        .replace("[REFERENCE_NO]", "LD-${System.currentTimeMillis() % 10000}")
                    Toast.makeText(context, "Filled date and firm defaults", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Autofill Current Date & Firm Name", fontSize = 12.sp)
            }

            // Document Content Editor
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Document Content (Edit freely)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 14
            )

            // PDF Generation & Export Action Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "Enter a title first", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val selectedClient = clients.find { it.id == selectedClientId }
                    val file = viewModel.generateAndSharePdf(
                        context = context,
                        title = title,
                        content = content,
                        clientName = selectedClient?.fullName ?: "",
                        firmName = "LexiDesk Professional Services"
                    )
                    if (file == null) {
                        Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealSecondary)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate & Share PDF")
            }
        }
    }
}
