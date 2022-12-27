package dev.whisper.voice.ui.routes.focus.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.whisper.core.model.TalkState
import dev.whisper.core.model.User
import dev.whisper.voice.ui.theme.Blue40
import dev.whisper.voice.ui.theme.Icons
import dev.whisper.voice.ui.theme.Red40
import dev.whisper.voice.viewmodels.UserViewModel

@Composable
fun UserIndicator(userViewModel: UserViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Person,
                "User",
                tint = if (userViewModel.talkState == TalkState.PASSIVE) MaterialTheme.colorScheme.onBackground else Blue40
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(text = userViewModel.username)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (userViewModel.isMuted || userViewModel.isSelfMuted) {
                Icon(Icons.MicOff, contentDescription = "Muted", tint = Red40)
            }
            Spacer(modifier = Modifier.size(3.dp))
            if (userViewModel.isDeafened || userViewModel.isSelfDeafened) {
                Icon(Icons.VolumeOff, contentDescription = "Deafened", tint = Red40)
            }
        }
    }
}

@Preview
@Composable
fun UserIndicatorPreview() {
    val user = remember { User(1, "User 1") }
    user.talkState = TalkState.TALKING

    Column {
        UserIndicator(userViewModel = UserViewModel(user))
        UserIndicator(userViewModel = UserViewModel(User(1, "User 2")))

        val u = User(1, "User 3")
        u.isMuted = true
        u.isDeafened = true
        UserIndicator(userViewModel = UserViewModel(u))
    }
}