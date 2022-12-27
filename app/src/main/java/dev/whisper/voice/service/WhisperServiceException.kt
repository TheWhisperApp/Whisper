package dev.whisper.voice.service

import dev.whisper.core.util.HumlaException
import java.security.cert.X509Certificate

open class WhisperServiceException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
}

class WhisperServiceConnectException(
    val invalidChain: Array<X509Certificate>? = null,
    val humlaException: HumlaException? = null
) : WhisperServiceException()

class WhisperAudioPermissionDeniedException : WhisperServiceException()