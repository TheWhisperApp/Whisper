package dev.whisper.voice.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class TreeNodeViewModel<T>(
    children: List<TreeNodeViewModel<T>> = emptyList(),
    value: T
) : ViewModel() {
    val children = mutableStateListOf<TreeNodeViewModel<T>>()
    var value = mutableStateOf(value)

    init {
        this.children.addAll(children)
    }
}