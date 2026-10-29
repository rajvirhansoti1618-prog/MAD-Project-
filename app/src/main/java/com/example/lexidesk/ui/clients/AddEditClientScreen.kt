package com.example.lexidesk.ui.clients

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.lexidesk.ui.theme.TealLight
import com.example.lexidesk.ui.theme.TealSecondary
import com.example.lexidesk.utils.ValidationUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClientScreen(
    clientId: Long?,
    viewModel: ClientsViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: (Long) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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

    var isSaving by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val nameRequiredMsg = stringResource(R.string.error_name_required)
    val invalidEmailMsg = stringResource(R.string.error_invalid_email)
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

    // Validation computations
    val isNameValid = fullName.trim().isNotBlank()
    val isCompanyValid = clientType != "Business" || companyName.trim().isNotBlank()
    val isPhoneValid = ValidationUtils.isValid10DigitPhone(phone)
    val isEmailValid = email.trim().isBlank() || ValidationUtils.isValidEmailFormat(email)
    val isFormValid = isNameValid && isCompanyValid && isPhoneValid && isEmailValid

    val isDirty = fullName.isNotBlank() || phone.isNotBlank() || email.isNotBlank() || companyName.isNotBlank() || notes.isNotBlank()
    val handleBack = {
        if (isDirty && !isSaving) {
            showDiscardDialog = true
        } else {
            onBackClick()
        }
    }

    // Connect Android system back handler
    BackHandler(enabled = !isSaving, onBack = handleBack)

    DiscardChangesDialog(
        isOpen = showDiscardDialog,
        onConfirmDiscard = {
            showDiscardDialog = false
            onBackClick()
        },
        onDismiss = { showDiscardDialog = false }
    )

    // Pulsing glow animation for enabled Submit button
    val infiniteTransition = rememberInfiniteTransition(label = "submitButtonGlow")
    val glowFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowFraction"
    )
    val glowAlpha = 0.35f + (0.50f * glowFraction)
    val glowElevation = (4f + 8f * glowFraction).dp

    val isGlowing = isFormValid && !isSaving
    val buttonEnabled = isFormValid && !isSaving

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                .imePadding()
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
                            onClick = {
                                clientType = type
                                if (type != "Business") {
                                    companyNameError = null
                                }
                            },
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
                    nameError = if (it.trim().isBlank()) nameRequiredMsg else null
                },
                label = { Text(stringResource(R.string.full_name_label) + " *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Full name icon") },
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
            )

            // Company Name (Required for Business clients)
            OutlinedTextField(
                value = companyName,
                onValueChange = {
                    companyName = it
                    companyNameError = if (clientType == "Business" && it.trim().isBlank()) companyRequiredMsg else null
                },
                isError = companyNameError != null,
                supportingText = { companyNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                label = { Text(if (clientType == "Business") stringResource(R.string.company_name_required) else stringResource(R.string.company_name_optional)) },
                leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
            )

            // Phone (Exactly 10 numeric digits, only 0-9)
            OutlinedTextField(
                value = phone,
                onValueChange = { input ->
                    // Accept only digits 0-9 and limit to exactly 10 digits
                    val filtered = input.filter { it.isDigit() }.take(10)
                    phone = filtered
                    phoneError = when {
                        filtered.isNotEmpty() && filtered.length < 10 -> {
                            "Phone number must be exactly 10 digits (${filtered.length}/10)"
                        }
                        else -> null
                    }
                },
                label = { Text(stringResource(R.string.phone_label) + " *") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone number icon") },
                isError = phoneError != null,
                supportingText = {
                    when {
                        phoneError != null -> Text(phoneError!!, color = MaterialTheme.colorScheme.error)
                        phone.isEmpty() -> Text("Required — exactly 10 digits", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        phone.length == 10 -> Text("10/10 digits entered", color = MaterialTheme.colorScheme.primary)
                        else -> Text("${phone.length}/10 digits", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            // Email (Optional, but if entered must match valid format)
            OutlinedTextField(
                value = email,
                onValueChange = { input ->
                    email = input
                    emailError = when {
                        input.trim().isBlank() -> null
                        !ValidationUtils.isValidEmailFormat(input) -> invalidEmailMsg
                        else -> null
                    }
                },
                label = { Text(stringResource(R.string.email_label)) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email address icon") },
                isError = emailError != null,
                supportingText = {
                    when {
                        emailError != null -> Text(emailError!!, color = MaterialTheme.colorScheme.error)
                        else -> Text("Optional (e.g. name@domain.com)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
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

            // Glowing Animated Submit Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .then(
                        if (isGlowing) {
                            Modifier
                                .shadow(
                                    elevation = glowElevation,
                                    shape = RoundedCornerShape(14.dp),
                                    ambientColor = TealSecondary.copy(alpha = glowAlpha),
                                    spotColor = TealLight.copy(alpha = glowAlpha)
                                )
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            TealSecondary.copy(alpha = glowAlpha),
                                            TealLight.copy(alpha = glowAlpha),
                                            TealSecondary.copy(alpha = glowAlpha)
                                        )
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                )
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
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
                        if (!ValidationUtils.isValid10DigitPhone(trimmedPhone)) {
                            phoneError = "Phone number must be exactly 10 digits"
                            return@Button
                        }
                        if (trimmedEmail.isNotBlank() && !ValidationUtils.isValidEmailFormat(trimmedEmail)) {
                            emailError = invalidEmailMsg
                            return@Button
                        }

                        if (isSaving) return@Button
                        isSaving = true

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
                            onSuccess = { newId ->
                                isSaving = false
                                val successMsg = if (clientId != null && clientId > 0) {
                                    "Client updated successfully"
                                } else {
                                    "Client saved successfully"
                                }
                                Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                                onSaveSuccess(newId)
                            },
                            onError = { error ->
                                isSaving = false
                                val errorMsg = error.localizedMessage?.takeIf { it.isNotBlank() }
                                    ?: "Failed to save client. Please check your connection and try again."
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(errorMsg)
                                }
                            }
                        )
                    },
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics { contentDescription = "Save client" },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyPrimary,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saving Client...")
                    } else {
                        Text(
                            text = stringResource(R.string.save_client_record),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
