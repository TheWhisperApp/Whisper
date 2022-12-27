package dev.whisper.voice.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VolumeControlViewModel : ViewModel() {
    val muted = MutableLiveData(false)
    val deafened = MutableLiveData(false)
}