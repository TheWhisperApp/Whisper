package dev.whisper.voice.entities.message

data class WhisperMessageEntity(
    val uuid: String = "",
    val channelId: String = "",
    val userId: String = "",
    val message: String = "",
    val replyTo: String = "",
    val replyToMessage: String = "",
    val createdBy: String = "",
    val createdDate: Long = 0,
    val modifiedDate: Long = 0,
    val metaTitle: String = "",
    val metaDesc: String = "",
    val metaImageUrl: String = "",
    val metaUrl: String = ""
)