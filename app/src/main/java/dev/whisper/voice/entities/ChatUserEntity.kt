package dev.whisper.voice.entities



/**
 * Represents a User in Discord
 * @param username Uniquely identifies this user
 * @param name Name of the user ([username] is used if no name is given)
 * @param currentStatus Current status of the user
 * @param isOnline Whether the user is online or offline
 * */
data class ChatUserEntity(
    val username: String,
    val name: String = username,
    val currentStatus: String? = null,
    val isOnline: Boolean,
)
