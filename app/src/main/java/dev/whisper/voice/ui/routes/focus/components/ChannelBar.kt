package dev.whisper.voice.ui.routes.focus.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelBar(
    channelName: String,
    channelMembers: Int,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    AppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Channel name
                Text(
                    text = "#" + channelName,
                    style = MaterialTheme.typography.titleMedium
                )
                // Number of members
                Text(
                    text = channelMembers.toString() + " members online",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
    )
}