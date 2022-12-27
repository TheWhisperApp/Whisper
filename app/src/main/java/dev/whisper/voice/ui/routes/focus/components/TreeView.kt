package dev.whisper.voice.ui.routes.focus.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.whisper.voice.ui.routes.focus.getFakeChannelTree
import dev.whisper.voice.ui.theme.Grey95
import dev.whisper.voice.ui.theme.Icons
import dev.whisper.voice.viewmodels.TreeNodeViewModel

data class TreeViewItemState<T>(val node: T, val isChildrenShown: Boolean)

@Composable
fun <T> TreeView(
    node: TreeNodeViewModel<T>,
    content: @Composable (value: TreeViewItemState<T>) -> Unit,
    onContentBoxClick: ((itemState: TreeViewItemState<T>) -> Unit)? = null
) {
    Column {
        var isChildrenShown by remember { mutableStateOf(true) }
        OutlinedCard(
            modifier = Modifier
                .padding(all = 3.dp)
                .fillMaxWidth()
                .clickable(
                    enabled = onContentBoxClick != null
                ) {
                    onContentBoxClick?.invoke(
                        TreeViewItemState(
                            node = node.value.value,
                            isChildrenShown
                        )
                    )
                },
            border = BorderStroke(2.dp, Grey95),
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (node.children.isNotEmpty()) {
                    Icon(
                        if (isChildrenShown) Icons.ExpandMore else Icons.ChevronRight,
                        "Expand",
                        modifier = Modifier
                            .clickable {
                                isChildrenShown = !isChildrenShown
                            }
                            .padding(4.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    Spacer(modifier = Modifier.size(10.dp))
                }

                content(TreeViewItemState(node.value.value, isChildrenShown))

            }
        }

        if (isChildrenShown && node.children.size > 0) {
            Row {
                Spacer(modifier = Modifier.size(24.dp, 1.dp))
                Column {
                    node.children.forEach {
                        TreeView<T>(node = it, content = { content(it) })
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun TreeViewPreview() {
    TreeView(
        node = getFakeChannelTree(),
        content = {
            Text(text = it.node)
        }
    )
}