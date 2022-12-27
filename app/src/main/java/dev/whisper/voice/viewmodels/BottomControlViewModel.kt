package dev.whisper.voice.viewmodels

import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.runInterruptible

class BottomControlViewModel(
    initialState: Array<Int> = emptyArray<Int>()        // essentially two volume numbers
) : ViewModel() {
    init {

    }

    suspend fun updateVolume() {
        runInterruptible {

        }
    }
}