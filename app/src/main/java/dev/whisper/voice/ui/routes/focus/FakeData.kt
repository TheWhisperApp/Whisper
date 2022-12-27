package dev.whisper.voice.ui.routes.focus

import dev.whisper.voice.entities.message.WhisperMessageEntity
import dev.whisper.voice.viewmodels.MessageUiEntity
import dev.whisper.voice.viewmodels.ServerViewModel
import dev.whisper.voice.viewmodels.TreeNodeViewModel
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

fun getFakeChannelTree(): TreeNodeViewModel<String> {
    return TreeNodeViewModel(
        value = "Root",
        children = listOf(
            TreeNodeViewModel(
                value = "Channel 1",
                children = listOf(
                    TreeNodeViewModel(
                        value = "Nested Channel 1"
                    ),
                    TreeNodeViewModel(
                        value = "Nested Channel 2"
                    )
                )
            ),
            TreeNodeViewModel(
                value = "Channel 2",
                children = listOf(
                    TreeNodeViewModel(
                        value = "Nested Channel 1"
                    ),
                    TreeNodeViewModel(
                        value = "Nested Channel 2"
                    )
                )
            )
        )
    )
}