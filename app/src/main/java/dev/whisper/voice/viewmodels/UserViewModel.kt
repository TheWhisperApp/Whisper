package dev.whisper.voice.viewmodels

import androidx.lifecycle.ViewModel
import dev.whisper.core.model.IUser
import dev.whisper.core.model.TalkState

class UserViewModel(
    private val user: IUser) : ViewModel() {
    val id: Int get() = user.userId

    val username: String get() = user.name
    val talkState: TalkState get() = user.talkState

    val isMuted get() = user.isMuted
    val isSelfMuted get() = user.isSelfMuted

    val isDeafened get() = user.isDeafened
    val isSelfDeafened get() = user.isSelfDeafened
}