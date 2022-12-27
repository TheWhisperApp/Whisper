package dev.whisper.voice.ui.routes.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.twotone.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.whisper.voice.db.AppDatabase
import dev.whisper.voice.ui.routes.focus.components.ServerIcon
import dev.whisper.voice.ui.routes.focus.components.dialog.AddServerDialog
import dev.whisper.voice.viewmodels.ServerListViewModel
import dev.whisper.voice.viewmodels.ServerViewModel
import dev.whisper.voice.ui.theme.Icons
import dev.whisper.voice.ui.routes.focus.components.dialog.EditServerDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ServerDrawer(
    modifier: Modifier = Modifier,
    serverListViewModel: ServerListViewModel,
    selectedIndex: Int = -1,
    onSelectedIndexChange: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    database: AppDatabase
) {
    val listState = rememberLazyListState()
    val (showAddServerDialog, setShowAddServerDialog) = rememberSaveable { mutableStateOf(false) }
    val (showEditServerDialog, setShowEditAddServerDialog) = rememberSaveable { mutableStateOf(false) }
    val serverList = serverListViewModel.serverList
    var longpressedItemIndex by rememberSaveable { mutableStateOf(-1) }

    LazyColumn(
        state = listState,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(serverList.size) { index ->
            val server = serverList[index]
            val selected = index == selectedIndex
            Row(
                modifier = Modifier.combinedClickable(
                    onClick = { onSelectedIndexChange(index) },
                    onLongClick = {
                        longpressedItemIndex = index
                        setShowEditAddServerDialog(true)
                    }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = selected) {
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topEndPercent = 50, bottomEndPercent = 50
                                )
                            )
                            .size(width = 6.dp, height = 32.dp)
                            .background(Color.Blue)
                    )
                }
                ServerIcon(
                    modifier = Modifier.fillMaxWidth(),
                    ping = server.latency.value,
                    serverLabel = server.name,
                    onlineMembers = server.currentUsers.value,
                    busy = server.connecting.value,
                    connected = server.connected.value
                )
            }
        }

        items(1) {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = { setShowAddServerDialog(true) }) {
                Icon(
                    imageVector = Icons.Add,
                    contentDescription = "Add Server",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            AddServerDialog(showAddServerDialog, setShowAddServerDialog, onServerAdd = {
                serverListViewModel.addServer(ServerViewModel(it, database))
            })
            EditServerDialog(
                showEditServerDialog,
                setShowEditAddServerDialog,
                serverListVM = serverListViewModel,
                selectedIndexToRemove = longpressedItemIndex,
                selectedIndex = selectedIndex
            ) {
//                serverListViewModel.addServer(ServerViewModel(it, database))
            }
        }
    }

}

//@Preview(showSystemUi = true)
//@Composable
//fun ServerDrawerPreview() {
//    WhisperTheme {
//        var selectedIndex by remember { mutableStateOf(-1) }
//
//        Box {
//            ServerDrawer(
//                serverListViewModel = ServerListViewModel(database = InMemoryWhisperDatabase()),
//                selectedIndex = selectedIndex,
//                onSelectedIndexChange = { selectedIndex = it },
//                onSettingsClick = {}
//            )
//        }
//    }
//}