package dev.whisper.voice.ui.routes.focus.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.whisper.voice.ui.theme.WhisperTheme

@Composable
fun InfoBadge(
    modifier: Modifier = Modifier,
    count: Int? = null,
    text: String? = null,
    color: Color = Color.Blue,
    textSize: TextUnit = 8.sp,
    badgeAlignment: Alignment = Alignment.BottomEnd,
    forceCircleShape: Boolean = true,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(contentAlignment = badgeAlignment) {
        content()
        AnimatedVisibility(
            modifier = modifier,
            visible = count != null && count > 0 || text != null && text.isNotEmpty()
        ) {
            val textSizeInDp = with(LocalDensity.current) { textSize.toDp() }
            Surface(
                shape = CircleShape,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .sizeIn(
                            minWidth = textSizeInDp * 2,
                            minHeight = textSizeInDp * 2,
                            maxWidth = if (forceCircleShape) textSizeInDp * 2 else Dp.Unspecified,
                            maxHeight = if (forceCircleShape) textSizeInDp * 2 else Dp.Unspecified,
                        )
                        .clip(CircleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    var displayCount = count
                    if (displayCount != null && forceCircleShape && displayCount > 99) {
                        displayCount = 99
                    }
                    Text(
                        text = text ?: displayCount?.toString() ?: "0",
                        fontSize = textSize,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        modifier = Modifier.padding(2.dp),
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun InfoBadgePreview() {
    WhisperTheme {
        Column {
            InfoBadge(text = "NA")
            InfoBadge(
                count = 99,
                forceCircleShape = true
            )
            InfoBadge(
                count = 2020,
                forceCircleShape = true
            )
            InfoBadge(
                count = 2020,
                forceCircleShape = false
            )
            InfoBadge(
                count = 100,
                forceCircleShape = false
            ) {
                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Color.Green) {}
            }
            InfoBadge(
                count = 100,
                forceCircleShape = false
            ) {
                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Color.Green) {}
            }
        }
    }
}