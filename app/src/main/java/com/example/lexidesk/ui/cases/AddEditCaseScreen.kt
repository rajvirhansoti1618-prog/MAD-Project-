package com.example.lexidesk.ui.cases

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lexidesk.R
import com.example.lexidesk.ui.common.DatePickerModalDialog
import com.example.lexidesk.ui.theme.NavyPrimary
import com.example.lexidesk.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCaseScreen(
    caseId: Long?,
    initialClientId: Long?,
    viewModel: CasesViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: (Long) -> Unit
) {
    val clients by viewModel.clientsFlow.collectAsState()
    val context = LocalContext.current

    var selectedClientId by remember { mutableStateOf<Long?>(initialClientId) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Legal Dispute") }
    var referenceNumber by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var status by remember { mutableStateOf("In Progress") }
    var startDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var dueDate by remember { mutableStateOf<Long?>(System.currentTimeMillis() + 7 * 24 * 3600 * 1000L) }
    var courtOrLocation by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var referenceNumberError by remember { mutableStateOf<String?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }

    var clientDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(caseId) {
        if (caseId != null && caseId > 0) {
            val existing = viewModel.getCaseByIdOnce(caseId)
            if (existing != null) {
                selectedClientId = existing.clientId
                title = existing.title
                category = existing.category
                referenceNumber = existing.referenceNumber
                priority = existing.priority
                status = existing.status
                startDate = existing.startDate
                dueDate = existing.dueDate
                courtOrLocation = existing.courtOrLocation
                description = existing.description
            }
        }
    }

    if (showStartDatePicker) {
        DatePickerModalDialog(
            initialDateMillis = startDate,
            onDateSelected = { startDate = it },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showDueDatePicker) {
        DatePickerModalDialog(
            initialDateMillis = dueDate,
            onDateSelected = { dueDate = it },
            onDismiss = { showDueDatePicker = false }
        )
    }

    val categories = listOf(
        "Legal Dispute",
        "Corporate Advisory",
        "Tax Audit & Compliance",
        "GST Advisory",
        "Contract & Agreement Drafting",
        "Intellectual Property",
        "Arbitration & Mediation",
        "General Consultation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (caseId != null && caseId > 0) "Edit Case / Matter" else "New Case / Assignment") },
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
            // Case Title (Required)
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) titleError = null
                },
                label = { Text("Case / Matter Title *") },
                leadingIcon = { Icon(Icons.Default.Gavel, contentDescription = null) },
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
                val selectedClientName = clients.find { it.id == selectedClientId }?.fullName ?: "Select Associated Client (Optional)"
                OutlinedTextField(
                    value = selectedClientName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Associated Client") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clientDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = clientDropdownExpanded,
                    onDismissRequest = { clientDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None (Standalone Case)") },
                        onClick = {
                            selectedClientId = null
                            clientDropdownExpanded = false
                        }
                    )
                    clients.forEach { c ->
                        DropdownMenuItem(
                            text = { Text("${c.fullName} (${c.clientType})") },
                            onClick = {
                                selectedClientId = c.id
                                clientDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Category Selector
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category / Matter Type") },
                    leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
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

            // Reference Number
            OutlinedTextField(
                value = referenceNumber,
                onValueChange = {
                    referenceNumber = it
                    if (referenceNumberError != null) referenceNumberError = null
                },
                label = { Text("Case Ref # / Court Docket No. / Project Code") },
                isError = referenceNumberError != null,
                supportingText = { referenceNumberError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Court or Location
            OutlinedTextField(
                value = courtOrLocation,
                onValueChange = { courtOrLocation = it },
                label = { Text("Court / Forum / Meeting Location") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Priority Selector Chips
            Column {
                Text("Priority Level", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
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
                    listOf("Not Started", "In Progress", "Waiting", "Completed", "On Hold").forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s) }
                        )
                    }
                }
            }

            // Date Pickers: Start Date and Due Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f).clickable { showStartDatePicker = true }) {
                    OutlinedTextField(
                        value = DateUtils.formatDate(startDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Start Date") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                }

                Box(modifier = Modifier.weight(1f).clickable { showDueDatePicker = true }) {
                    OutlinedTextField(
                        value = DateUtils.formatDate(dueDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Due Date *") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Case Description & Objectives") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = context.getString(R.string.error_case_title_required)
                        return@Button
                    }
                    if (referenceNumber.isNotBlank() && referenceNumber.length < 3) {
                        referenceNumberError = context.getString(R.string.error_case_ref_length)
                        return@Button
                    }
                    viewModel.saveCase(
                        id = caseId ?: 0L,
                        clientId = selectedClientId,
                        title = title,
                        category = category,
                        description = description,
                        referenceNumber = referenceNumber,
                        priority = priority,
                        status = status,
                        startDate = startDate,
                        dueDate = dueDate,
                        courtOrLocation = courtOrLocation,
                        notes = "",
                        onSuccess = onSaveSuccess
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save Case Record")
            }
        }
    }
}
