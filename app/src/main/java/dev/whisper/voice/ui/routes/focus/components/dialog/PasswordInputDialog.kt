package dev.whisper.voice.ui.routes.focus.components.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.material.icons.twotone.VisibilityOff
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.whisper.voice.R
import dev.whisper.voice.ui.theme.Icons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInputDialog(
    showDialog: Boolean = false,
    onUserCancel: () -> Unit,
    onUserConfirm: (password: String) -> Unit,
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onUserCancel,
            title = { Text(text = "Wrong password") },
            icon = { Icon(Icons.Warning, contentDescription = "Warning") },
            text = {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password, // use the password state to hold the value
                    onValueChange = {
                        password = it
                    }, // update the password state with the new value
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        androidx.compose.material.Icon(
                            imageVector = Icons.Lock,
                            contentDescription = context.getString(R.string.add_server_dialog_server_password)
                        )
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    label = { androidx.compose.material.Text(text = context.getString(R.string.add_server_dialog_server_password)) },
                    placeholder = { androidx.compose.material.Text(text = "") },
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Visibility
                        else Icons.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description =
                            if (passwordVisible) context.getString(R.string.add_server_dialog_server_hide_password) else context.getString(
                                R.string.add_server_dialog_server_show_password
                            )

                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            androidx.compose.material.Icon(imageVector = image, description)
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = onUserCancel) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = { onUserConfirm(password) }) {
                    Text(text = "Reconnect")
                }
            },
        )
    }
}