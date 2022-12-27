package dev.whisper.voice.ui.routes.focus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.whisper.voice.ui.routes.focus.components.ServerBar
import dev.whisper.voice.ui.routes.focus.components.TreeView
import dev.whisper.voice.ui.routes.focus.components.UserIndicator
import dev.whisper.voice.viewmodels.ServerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerScreen(
    server: ServerViewModel,
    onOpenChannelChat: (channelId: Int) -> Unit
) {
    val channels by server.channels.observeAsState()

    Column {
        ServerBar(serverName = server.name, serverAddress = server.host)
        if (channels != null) {
            TreeView(
                node = channels!!,
                content = {
                    Column {
                        Text(
                            text = it.node.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (it.isChildrenShown) {
                            it.node.users.forEach { user ->
                                UserIndicator(userViewModel = user)
                            }
                        }
                    }
                },
                onContentBoxClick = {
                    onOpenChannelChat(it.node.channel.id)
                }
            )
        }
        else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

    }

}

