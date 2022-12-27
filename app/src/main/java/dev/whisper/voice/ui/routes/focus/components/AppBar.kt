package dev.whisper.voice.ui.routes.focus.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.whisper.voice.ui.theme.WhisperTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        actions = actions,
        title = title,
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun WhisperAppBarPreview() {
    WhisperTheme {
        AppBar(title = { Text("Channel Name") })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NoWhisperAppBarPreview() {
    AppBar(title = { Text("Channel Name") })
}