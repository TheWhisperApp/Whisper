package dev.whisper.voice.ui.routes.focus

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.twotone.*
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.whisper.core.protobuf.Mumble
import dev.whisper.core.util.HumlaException
import dev.whisper.voice.Constants.ACTION_SERVICE_START
import dev.whisper.voice.R
import dev.whisper.voice.preferences.WhisperSettingsActivity
import dev.whisper.voice.crypto.TrustStore
import dev.whisper.voice.db.AppDatabase
import dev.whisper.voice.service.WhisperAudioPermissionDeniedException
import dev.whisper.voice.service.WhisperServiceConnectException
import dev.whisper.voice.service.WhisperServiceManager
import dev.whisper.voice.ui.routes.focus.components.dialog.GeneralAlertDialog
import dev.whisper.voice.ui.routes.focus.components.dialog.PasswordInputDialog
import dev.whisper.voice.ui.routes.focus.components.dialog.UntrustedCertDialog
import dev.whisper.voice.ui.theme.Icons
import dev.whisper.voice.ui.theme.WhisperTheme
import dev.whisper.voice.viewmodels.*
import kotlinx.coroutines.launch
import java.security.cert.X509Certificate


@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    serverListViewModel: ServerListViewModel,
    bottomControlViewModel: BottomControlViewModel,
    navController: NavHostController = rememberNavController(),
    whisperServiceManager: WhisperServiceManager,
    messagingPageViewModel: MessagingPageViewModel,
    volumeControlViewModel: VolumeControlViewModel,
    database: AppDatabase
) {
    var selectedIndex by rememberSaveable { mutableStateOf(-1) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val context = LocalContext.current
    var connected by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(serverListViewModel) {
        serverListViewModel.loadServerList()
    }

    BottomSheetScaffold(
        sheetPeekHeight = 64.dp,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // TODO: probably a bug, volume control
                val muted by volumeControlViewModel.muted.observeAsState(initial = false)
                val deafened by volumeControlViewModel.deafened.observeAsState(initial = false)

                val density = LocalDensity.current

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(
                        visible = connected,
                        enter = slideInHorizontally {
                            // Slide in from 40 dp from the top.
                            with(density) { -40.dp.roundToPx() }
                        } + expandHorizontally(
                            // Expand from the top.
                            expandFrom = Alignment.Start
                        ) + fadeIn(
                            // Fade in with the initial alpha of 0.3f.
                            initialAlpha = 0.3f
                        ),
                        exit = slideOutHorizontally() + shrinkHorizontally() + fadeOut()
                    ) {
                        FilledTonalIconButton(onClick = { /* connected = false */ }) {
                            Icon(
                                imageVector = Icons.Link,
                                contentDescription = "Connected"
                            )
                        }
                    }

                    if (connected) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Button(
                        enabled = connected,
                        onClick = {
                            volumeControlViewModel.muted.value = !muted
                        }
                    ) {
                        val unmutedPrompt = context.getString(R.string.bottom_toggle_mute)
                        val mutedPrompt = context.getString(R.string.bottom_toggle_muted)
                        Icon(
                            imageVector = if (!muted) Icons.Mic else Icons.MicOff,
                            contentDescription = context.getString(R.string.bottom_toggle_mute_desc)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = if (!muted) unmutedPrompt else mutedPrompt)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        enabled = connected,
                        onClick = {
                            volumeControlViewModel.deafened.value = !deafened
                        },
                        colors = ButtonDefaults.buttonColors(
// TODO: change color when state changes
//                            containerColor  = Color.Red,
                        )
                    ) {
                        val undeafenedPrompt = context.getString(R.string.bottom_toggle_deafen)
                        val deafenedPrompt = context.getString(R.string.bottom_toggle_deafened)
                        Icon(
                            imageVector = if (!deafened) Icons.VolumeUp else Icons.VolumeOff,
                            contentDescription = context.getString(R.string.bottom_toggle_deafen_desc)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = if (!deafened) undeafenedPrompt else deafenedPrompt)
                    }
                }
            }
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.inverseOnSurface,
        scaffoldState = scaffoldState,
    ) {
        val coroutineScope = rememberCoroutineScope()
        var busy by remember { mutableStateOf(false) }
        var showUntrustedCertDialog by rememberSaveable { mutableStateOf(false) }
        var certChain by rememberSaveable { mutableStateOf(arrayOf<X509Certificate>()) }

        var alertDialogText by remember { mutableStateOf("") }
        var showGeneralAlertDialog by remember { mutableStateOf(false) }

        var showPasswordDialog by remember { mutableStateOf(false) }

        suspend fun connect(server: ServerViewModel) {
            try {
                //TODO: remove ServerEntity
                whisperServiceManager.connectToServer(server)

                navController.popBackStack()
                navController.navigate("server/${selectedIndex}")
            } catch (e: WhisperServiceConnectException) {
                if (e.invalidChain != null) {
                    certChain = e.invalidChain
                    showUntrustedCertDialog = true
                } else if (e.humlaException != null) {
                    if (
                        e.humlaException.reason == HumlaException.HumlaDisconnectReason.REJECT &&
                        e.humlaException.reject.type == Mumble.Reject.RejectType.WrongServerPW
                    ) {
                        showPasswordDialog = true

                        return
                    }
                    //TODO: user password rejection handling

                    alertDialogText = e.humlaException.message ?: "Unknown error"
                    showGeneralAlertDialog = true
                }
            } catch (e: WhisperAudioPermissionDeniedException) {
                alertDialogText = "Microphone permission denied."
                showGeneralAlertDialog = true
            } finally {
                server.connecting.value = false
                busy = false
                server.connected.value = true
                connected = true
            }
        }

        UntrustedCertDialog(
            showDialog = showUntrustedCertDialog,
            certChain = certChain,
            onUserCancel = {
                showUntrustedCertDialog = false
            },
            onUserAccept = {
                showUntrustedCertDialog = false

                // TODO: decouple from UI
                val store = TrustStore.getTrustStore(context)
                val server = serverListViewModel.serverList[selectedIndex]
                store.setCertificateEntry(server.host, it)
                TrustStore.saveTrustStore(context, store)

                coroutineScope.launch {
                    connect(server)
                }
            }
        )

        GeneralAlertDialog(
            title = "Failed to connect",
            text = alertDialogText,
            showDialog = showGeneralAlertDialog,
            onUserDismiss = { showGeneralAlertDialog = false }
        )

        PasswordInputDialog(
            showDialog = showPasswordDialog,
            onUserCancel = { showPasswordDialog = false },
            onUserConfirm = {
                val serverViewModel = serverListViewModel.serverList[selectedIndex]
                serverViewModel.update(password = it)
                showPasswordDialog = false
                coroutineScope.launch {
                    connect(serverViewModel)
                }
            })

        Row(modifier = Modifier.padding(it).background(MaterialTheme.colorScheme.background)) {
            ServerDrawer(
                modifier = Modifier
                    .width(72.dp),
                database = database,
                serverListViewModel = serverListViewModel,
                selectedIndex = selectedIndex,
                onSelectedIndexChange = {
                    if (!busy && it != selectedIndex) {
                        busy = true
                        coroutineScope.launch {
                            selectedIndex = it
                            val server = serverListViewModel.serverList[selectedIndex]
                            server.connecting.value = true
                            connect(server)
                        }
                    }
                },
                onSettingsClick = {
                    startActivity(
                        context,
                        Intent(context, WhisperSettingsActivity::class.java),
                        null
                    )
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(color = Color.Gray)
            )
            Spacer(modifier = Modifier.width(4.dp))
            NavHost(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                navController = navController, startDestination = "empty"
            ) {
                composable("empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = context.getString(R.string.home_screen_chat_placeholder))
                    }
                }
                composable("server/{selectedIndex}") { backStackEntry ->
                    val server = serverListViewModel.serverList[selectedIndex]

                    LaunchedEffect(server) {
                        if (whisperServiceManager.rootChannel != null) {
                            server.updateChannel(whisperServiceManager.rootChannel!!)
                        }
                    }

                    ServerScreen(
                        serverListViewModel.serverList[selectedIndex],
                        onOpenChannelChat = {
                            navController.navigate("chat/$it")
                        }
                    )
                }

                composable("chat/{channelId}") { backStackEntry ->
                    MessagingPage(
                        viewModel = messagingPageViewModel,
                        onSendMessage = {
                            whisperServiceManager.sendMessage(it.content)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BottomControlButtonSample() {
    WhisperTheme {
        Button(
            onClick = { /*TODO*/ },
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.TwoTone.Mic,
                contentDescription = "Mute Microphone"
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Mute")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    WhisperTheme {
//        HomeScreen(serverListViewModel = ServerListViewModel(getFakeServerList(), InMemoryWhisperDatabase()), null)
    }
}
