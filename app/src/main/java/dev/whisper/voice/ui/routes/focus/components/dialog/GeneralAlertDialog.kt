package dev.whisper.voice.ui.routes.focus.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun GeneralAlertDialog(
    title: String,
    text: String,
    icon: (@Composable () -> Unit)? = null,
    onUserDismiss: () -> Unit,
    onUserConfirm: (() -> Unit)? = null,
    showDialog: Boolean = false
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onUserDismiss },
            title = { Text(text = title) },
            text = { Text(text = text) },
            icon = { icon },
            dismissButton = {
                if (onUserConfirm != null) {
                    TextButton(onClick = onUserDismiss) {
                        Text(text = "Cancel")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onUserConfirm?.invoke()
                    onUserDismiss()
                }) {
                    Text(text = "Ok")
                }
            }
        )
    }
}