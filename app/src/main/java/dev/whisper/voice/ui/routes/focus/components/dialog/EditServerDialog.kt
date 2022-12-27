package dev.whisper.voice.ui.routes.focus.components.dialog

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import dev.whisper.voice.ui.theme.Icons
import androidx.compose.material.icons.twotone.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.whisper.voice.R
import dev.whisper.voice.WhisperConstants
import dev.whisper.voice.db.Server
import dev.whisper.voice.viewmodels.ServerListViewModel
import dev.whisper.voice.viewmodels.ServerViewModel
import java.util.UUID


@Composable
fun EditServerDialog(
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    serverListVM: ServerListViewModel,
    selectedIndexToRemove: Int,
    selectedIndex:Int,
    onServerAdd: (Server) -> Unit
) {
    val context = LocalContext.current
    if (showDialog) {
        val serverVM = serverListVM.serverList[selectedIndex]
        val server = serverVM.server

        var serverName by rememberSaveable { mutableStateOf(server.name) }  // state to hold the server name
        var serverAddress by rememberSaveable { mutableStateOf(server.host) }// state to hold the server address
        var serverPort by rememberSaveable { mutableStateOf(server.port.toString()) } // state to hold the server port
        var userName by rememberSaveable { mutableStateOf(server.username) } // state to hold the user name
        var password by rememberSaveable { mutableStateOf(server.password) } // state to hold the password
        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
            },
            title = null,
            text = {
                Column(
                    verticalArrangement = Arrangement.Top,
                ) {
                    Text(
                        style = MaterialTheme.typography.subtitle1.copy(Color.Black),
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = context.getString(R.string.modify_server_dialog_title)
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        value = serverName,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Home,
                                contentDescription = context.getString(R.string.modify_server_dialog_title)
                            )
                        },
                        //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                        onValueChange = {
                            serverName = it
                        }, // update the serverName state with the new value
                        label = { Text(text = context.getString(R.string.add_server_dialog_server_name)) },
                    )

                    OutlinedTextField(
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        value = serverAddress, // use the serverAddress state to hold the value
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Dns,
                                contentDescription = context.getString(R.string.add_server_dialog_server_host)
                            )
                        },
                        onValueChange = {
                            serverAddress = it
                        }, // update the serverAddress state with the new value
                        label = { Text(text = context.getString(R.string.add_server_dialog_server_host)) },
                    )

                    OutlinedTextField(
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        value = serverPort, // use the serverPort state to hold the value
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Pin,
                                contentDescription = context.getString(R.string.add_server_dialog_server_port)
                            )
                        },
                        onValueChange = { value ->
                            if (value.length <= 5) {
                                serverPort = value.filter { it.isDigit() }
                            }
                        },
                        label = { Text(text = context.getString(R.string.add_server_dialog_server_port)) },
                        placeholder = { Text(text = "64738") },
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        value = userName, // use the userName state to hold the value
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.People,
                                contentDescription = context.getString(R.string.add_server_dialog_server_username)
                            )
                        },
                        onValueChange = {
                            userName = it
                        }, // update the userName state with the new value
                        label = { Text(text = context.getString(R.string.add_server_dialog_server_username)) },
//                        placeholder = { Text(text = "Whisper_User") },
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password, // use the password state to hold the value
                        onValueChange = {
                            password = it
                        }, // update the password state with the new value
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Lock,
                                contentDescription = context.getString(R.string.add_server_dialog_server_password)
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        label = { Text(text = context.getString(R.string.add_server_dialog_server_password)) },
                        placeholder = { Text(text = "") },
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Visibility
                            else Icons.VisibilityOff

                            // Please provide localized description for accessibility services
                            val description =
                                if (passwordVisible) context.getString(R.string.add_server_dialog_server_hide_password) else context.getString(
                                    R.string.add_server_dialog_server_show_password
                                )

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, description)
                            }
                        }
                    )
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {


                    TextButton(
                        onClick = {
                            if (selectedIndex==selectedIndexToRemove){
                                // TODO: handle selected server deletion
                            }
                            serverListVM.removeServer(serverVM)
                            setShowDialog(false)
                        },
                        colors = ButtonDefaults.buttonColors(contentColor  = Color.Red, backgroundColor = Color.Transparent)
                    ) {
                        Text(text = context.getString(R.string.modify_server_dialog_server_delete))
                    }

                    TextButton(
                        onClick = {
                            setShowDialog(false)
                        },
                    ) {
                        Text(text = context.getString(R.string.add_server_dialog_cancel))
                    }


                    TextButton(
                        onClick = {
                            if (!Patterns.WEB_URL.matcher(serverAddress).matches()) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.add_server_dialog_server_host_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (!serverPort.isEmpty() && (serverPort.toInt() < 1 || serverPort.toInt() > 65535)) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.add_server_dialog_server_port_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                onServerAdd(
                                    Server(
                                        UUID.randomUUID().mostSignificantBits,
                                        serverName,
                                        serverAddress,
                                        if (serverPort.isEmpty()) WhisperConstants.DEFAULT_PORT else serverPort.toInt(),
                                        if (userName.isEmpty()) (WhisperConstants.DEFAULT_USERNAME + (0..100000).random()) else userName,
                                        password
                                    )
                                )
                                setShowDialog(false)
                            }
                        },
                    ) {
                        Text(text = context.getString(R.string.add_server_dialog_confirm))
                    }

                }
            },
        )
    }
}
