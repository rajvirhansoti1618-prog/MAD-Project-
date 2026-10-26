package com.example.lexidesk.ui.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lexidesk.R
import com.example.lexidesk.ui.common.DiscardChangesDialog
import com.example.lexidesk.ui.common.TopBarWithBack
import com.example.lexidesk.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClientScreen(
    clientId: Long?,
    viewModel: ClientsViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: (Long) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var clientType by remember { mutableStateOf("Individual") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var companyNameError by remember { mutableStateOf<String?>(null) }

    var showDiscardDialog by remember { mutableStateOf(false) }

    val nameRequiredMsg = stringResource(R.string.error_name_required)
    val invalidEmailMsg = stringResource(R.string.error_invalid_email)
    val invalidPhoneMsg = stringResource(R.string.error_invalid_phone)
    val companyRequiredMsg = stringResource(R.string.error_company_name_required)

    LaunchedEffect(clientId) {
        if (clientId != null && clientId > 0) {
            val existing = viewModel.getClientByIdOnce(clientId)
            if (existing != null) {
                fullName = existing.fullName
                clientType = existing.clientType
                phone = existing.phone
                email = existing.email
                companyName = existing.companyName
                address = existing.address
                tags = existing.tags
                notes = existing.notes
            }
        }
    }

    val isDirty = fullName.isNotBlank() || phone.isNotBlank() || email.isNotBlank() || companyName.isNotBlank() || notes.isNotBlank()
    val handleBack = {
        if (isDirty) {
            showDiscardDialog = true
        } else {
            onBackClick()
        }
    }

    DiscardChangesDialog(
        isOpen = showDiscardDialog,
        onConfirmDiscard = {
            showDiscardDialog = false
            onBackClick()
        },
        onDismiss = { showDiscardDialog = false }
    )

    Scaffold(
        topBar = {
            TopBarWithBack(
                title = if (clientId != null && clientId > 0) stringResource(R.string.edit_client) else stringResource(R.string.add_new_client),
                onBackClick = handleBack
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
            // Client Type Selector
            Column {
                Text("Client Type", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Individual", "Business").forEach { type ->
                        FilterChip(
                            selected = clientType == type,
                            onClick = { clientType = type },
                            label = { Text(type) }
                        )
                    }
                }
            }

            // Full Name (Required)
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    if (it.isNotBlank()) nameError = null
                },
                label = { Text(stringResource(R.string.full_name_label)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Full name icon") },
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
            )

            // Company Name
            OutlinedTextField(
                value = companyName,
                onValueChange = {
                    companyName = it
                    if (companyNameError != null) companyNameError = null
                },
                isError = companyNameError != null,
                supportingText = companyNameError?.let { { Text(it) } },
                label = { Text(if (clientType == "Business") stringResource(R.string.company_name_required) else stringResource(R.string.company_name_optional)) },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
            )

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    if (phoneError != null) phoneError = null
                },
                label = { Text(stringResource(R.string.phone_label)) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone number icon") },
                isError = phoneError != null,
                supportingText = {
                    when {
                        phoneError != null -> Text(phoneError!!, color = MaterialTheme.colorScheme.error)
                        else -> Text("Enter at least 7 digits", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                },
                label = { Text(stringResource(R.string.email_label)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email address icon") },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            // Physical Address
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.address_label)) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Tags
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text(stringResource(R.string.tags_label)) },
                leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.notes_label)) },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Note, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Save Button
            Button(
                onClick = {
                    val trimmedName = fullName.trim()
                    val trimmedEmail = email.trim()
                    val trimmedPhone = phone.trim()

                    if (trimmedName.isBlank()) {
                        nameError = nameRequiredMsg
                        return@Button
                    }
                    if (clientType == "Business" && companyName.trim().isBlank()) {
                        companyNameError = companyRequiredMsg
                        return@Button
                    }
                    if (trimmedEmail.isNotBlank() && !com.example.lexidesk.utils.ValidationUtils.isValidEmail(trimmedEmail)) {
                        emailError = invalidEmailMsg
                        return@Button
                    }
                    if (trimmedPhone.isNotBlank() && !com.example.lexidesk.utils.ValidationUtils.isValidPhone(trimmedPhone)) {
                        phoneError = invalidPhoneMsg
                        return@Button
                    }

                    // Save client
                    viewModel.saveClient(
                        id = clientId ?: 0L,
                        fullName = trimmedName,
                        clientType = clientType,
                        phone = trimmedPhone,
                        email = trimmedEmail,
                        address = address.trim(),
                        companyName = companyName.trim(),
                        notes = notes,
                        tags = tags,
                        onSuccess = onSaveSuccess
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .semantics { contentDescription = "Save client" },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text(stringResource(R.string.save_client_record))
            }
        }
    }
}
