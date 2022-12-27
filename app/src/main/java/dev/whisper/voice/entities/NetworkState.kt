package dev.whisper.voice.entities


sealed class NetworkState<out T> {
    class Success<T>(val data: T): NetworkState<T>()
    class Failure(val throwable: Throwable): NetworkState<Nothing>()
}
