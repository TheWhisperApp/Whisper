package dev.whisper.voice.ui.routes.focus.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.whisper.voice.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerBar(
    serverName: String,
    serverAddress: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val context = LocalContext.current
    AppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Server name
                Text(
                    text = "#" + serverName,
                    style = MaterialTheme.typography.titleMedium
                )
                // Server host
                Text(
                    text = context.getString(R.string.app_bar_host_name) + " " + serverAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
    )
}