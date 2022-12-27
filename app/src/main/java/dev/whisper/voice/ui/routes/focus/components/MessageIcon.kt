package dev.whisper.voice.ui.routes.focus.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.whisper.voice.ui.theme.Red90
import dev.whisper.voice.ui.theme.WhisperTheme

@Composable
fun MessageIcon(iconTitle: String) {
    Box(
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        var displayText = iconTitle;
        if (displayText.length > 2) {
            val words = displayText.split(" ")
            displayText = if (words.size > 1) {
                words[0].substring(0, 1) + words[1].substring(0, 1);
            } else {
                displayText.substring(0, 2);
            }
        }

        Surface(
            modifier = Modifier.padding(6.dp),
            shape = CircleShape,
            color = Red90
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .absolutePadding(bottom = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MessageIconPreview() {
    WhisperTheme {
        Column {
            MessageIcon(iconTitle = "Title")
        }
    }
}