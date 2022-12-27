package dev.whisper.voice.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.OffsetDateTime

class MessagingPageViewModel(
    val channelName: String,
    initialMessages: List<MessageUiEntity>
) : ViewModel() {
    val _messages: MutableList<MessageUiEntity> = initialMessages.toMutableStateList()
    val messages: List<MessageUiEntity> = _messages

    val onlineMemberCount = MutableLiveData(0)

    fun addMessage(msg: MessageUiEntity) {
        _messages.add(0, msg) // Add to the beginning of the list
    }

    fun clearMessage() {
        _messages.clear()
    }
}

@Immutable
data class MessageUiEntity(
    val author: String,
    val content: String,
    val timestamp: OffsetDateTime,
    val image: Int? = null,
)
