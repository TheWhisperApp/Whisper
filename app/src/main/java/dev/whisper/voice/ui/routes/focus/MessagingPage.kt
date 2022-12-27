package dev.whisper.voice.ui.routes.focus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import dev.whisper.voice.ui.routes.focus.components.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import dev.whisper.voice.viewmodels.MessageUiEntity
import dev.whisper.voice.viewmodels.MessagingPageViewModel
import java.time.OffsetDateTime

private val JumpToBottomThreshold = 56.dp
const val ConversationTestTag = "ConversationTestTag"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingPage(
    viewModel: MessagingPageViewModel,
    modifier: Modifier = Modifier,
    onSendMessage: (message: MessageUiEntity) -> Unit
) {
    val authorMe = "me"

    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()

    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                MessageFeed(
                    messages = viewModel.messages,
                    modifier = Modifier.weight(1f),
                    scrollState = scrollState
                )
                UserInput(
                    onMessageSent = { content ->
                        val entity = MessageUiEntity(authorMe, content, OffsetDateTime.now())
                        viewModel.addMessage(entity)
                        onSendMessage(entity)
                    },
                    resetScroll = {
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding(),
                )
            }

            val memberCount by viewModel.onlineMemberCount.observeAsState()

            // Channel name bar floats above the messages
            ChannelBar(
                channelName = viewModel.channelName,
                channelMembers = memberCount ?: 0,
                scrollBehavior = scrollBehavior,
            )
        }
    }
}

@Composable
fun MessageFeed(
    messages: List<MessageUiEntity>,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    Box(modifier = modifier) {

        val authorMe = "me"
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            contentPadding =
            WindowInsets.statusBars.add(WindowInsets(top = 90.dp)).asPaddingValues(),
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize()
        ) {
            for (index in messages.indices) {
                val prevAuthor = messages.getOrNull(index - 1)?.author
                val nextAuthor = messages.getOrNull(index + 1)?.author
                val content = messages[index]
                val isFirstMessageByAuthor = prevAuthor != content.author
                val isLastMessageByAuthor = nextAuthor != content.author


                item {
                    MessageContainer(
                        msg = content,
                        isUserMe = content.author == authorMe,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor
                    )
                }
            }
        }

        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                (scrollState.firstVisibleItemIndex != 0) ||
                        (scrollState.firstVisibleItemScrollOffset > jumpThreshold)
            }
        }
        JumpToBottom(
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
