package com.example.lexidesk.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.lexidesk.ui.theme.RedUrgent

@Composable
fun DiscardChangesDialog(
    isOpen: Boolean,
    onConfirmDiscard: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "Discard Unsaved Changes?",
    message: String = "You have unsaved changes. Leaving this screen will lose any modifications."
) {
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            Button(
                onClick = onConfirmDiscard,
                colors = ButtonDefaults.buttonColors(containerColor = RedUrgent)
            ) {
                Text("Discard")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Keep Editing")
            }
        }
    )
}
