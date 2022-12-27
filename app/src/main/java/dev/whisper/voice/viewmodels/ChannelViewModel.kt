package dev.whisper.voice.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dev.whisper.core.model.IChannel
import dev.whisper.core.model.IUser

class ChannelViewModel(
    val channel: IChannel
) : ViewModel() {
    val name: String get() = channel.name
    val users = mutableStateListOf<UserViewModel>()
}

enum class ChannelType {
    PUBLIC, PRIVATE, BROADCAST, PODCAST, CONVERSATION
}