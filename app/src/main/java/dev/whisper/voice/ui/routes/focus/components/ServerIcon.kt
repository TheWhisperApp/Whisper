package dev.whisper.voice.ui.routes.focus.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.whisper.voice.ui.theme.*

@Composable
fun ServerIcon(
    modifier: Modifier = Modifier,
    serverLabel: String,
    onlineMembers: Int,
    ping: Int? = null,
    busy: Boolean = false,
    connected: Boolean = false,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier.then(Modifier.padding(vertical = 5.dp)),
        contentAlignment = Alignment.Center
    ) {
        InfoBadge(count = onlineMembers, badgeAlignment = Alignment.TopEnd) {
            var pingIndicatorColor = Color.Red
            if ((ping == null) || (ping < 0)) {
                pingIndicatorColor = Color.Red
            } else if (ping < 40) {
                pingIndicatorColor = Green200
            } else if (ping in 41..99) {
                pingIndicatorColor = Orange700
            }
            InfoBadge(
                text = (ping?.toString() ?: "NA") + "ms",
                color = pingIndicatorColor,
                forceCircleShape = false
            ) {
                var displayText = serverLabel;
                if (displayText.length > 2) {
                    val words = displayText.split(" ")
                    displayText = if (words.size > 1) {
                        words[0].substring(0, 1) + words[1].substring(0, 1);
                    } else {
                        displayText.substring(0, 2);
                    }
                }

//                val finiteTransition = rememberInfiniteTransition()
//                val cornerPercent by finiteTransition.animateValue(
//                    initialValue = 50,
//                    targetValue = 10,
//                    typeConverter = Int.VectorConverter,
//                    animationSpec = InfiniteRepeatableSpec(
//                        animation = keyframes {
//                            durationMillis = 1000
//                            50.at(0).with(FastOutSlowInEasing)
////                            50.at(100).with(FastOutSlowInEasing)
////                            25.at(500).with(FastOutSlowInEasing)
////                            15.at(800).with(FastOutSlowInEasing)
//                            10.at(1000).with(FastOutSlowInEasing)
//                        },
////                        repeatMode = RepeatMode.Reverse
//                    )
//                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(size)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        if (busy) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .absolutePadding(bottom = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayText,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ServerIconPreview() {
    WhisperTheme {
        Column {
            ServerIcon(serverLabel = "Fuiyoo", onlineMembers = 100)
            ServerIcon(serverLabel = "Fuiyoo", onlineMembers = 100, busy = true, connected = true)
        }
    }
}