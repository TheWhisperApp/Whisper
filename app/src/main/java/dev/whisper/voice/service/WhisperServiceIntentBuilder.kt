package dev.whisper.voice.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaRecorder
import dev.whisper.core.HumlaService
import dev.whisper.voice.R
import dev.whisper.voice.preferences.PreferencesStore
import dev.whisper.voice.db.WhisperDatabase
import dev.whisper.voice.preferences.PreferencesManager
import dev.whisper.voice.crypto.TrustStore
import dev.whisper.voice.db.AppDatabase
import dev.whisper.voice.db.Server

fun buildWhisperServiceIntent(
    context: Context,
    database: AppDatabase,
    server: Server
): Intent {
    val settings: PreferencesStore = PreferencesStore.getInstance(context)

    /* Convert input method defined in settings to an integer format used by Humla. */
    val inputMethod: Int = settings.humlaInputMethod
    val audioSource =
        if (settings.isHandsetMode) MediaRecorder.AudioSource.DEFAULT else MediaRecorder.AudioSource.VOICE_COMMUNICATION
    val audioStream =
        if (settings.isHandsetMode) AudioManager.STREAM_VOICE_CALL else AudioManager.STREAM_MUSIC
    var applicationVersion = ""
    try {
        applicationVersion =
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    val connectIntent = Intent(context, WhisperService::class.java)
    connectIntent.putExtra(HumlaService.EXTRAS_SERVER, server.toHumlaServer())
    connectIntent.putExtra(
        HumlaService.EXTRAS_CLIENT_NAME,
        context.getString(R.string.app_name) + " " + applicationVersion
    )
    connectIntent.putExtra(HumlaService.EXTRAS_TRANSMIT_MODE, inputMethod)
    connectIntent.putExtra(
        HumlaService.EXTRAS_DETECTION_THRESHOLD,
        settings.detectionThreshold
    )
    connectIntent.putExtra(
        HumlaService.EXTRAS_AMPLITUDE_BOOST,
        settings.amplitudeBoostMultiplier
    )
    connectIntent.putExtra(
        HumlaService.EXTRAS_AUTO_RECONNECT,
        settings.isAutoReconnectEnabled
    )
    connectIntent.putExtra(
        HumlaService.EXTRAS_AUTO_RECONNECT_DELAY,
            WhisperService.RECONNECT_DELAY
    )
    connectIntent.putExtra(HumlaService.EXTRAS_USE_OPUS, !settings.isOpusDisabled)
    connectIntent.putExtra(
        HumlaService.EXTRAS_INPUT_RATE,
        settings.inputSampleRate
    )
    connectIntent.putExtra(
        HumlaService.EXTRAS_INPUT_QUALITY,
        settings.inputQuality
    )
    connectIntent.putExtra(HumlaService.EXTRAS_FORCE_TCP, settings.isTcpForced)
    connectIntent.putExtra(HumlaService.EXTRAS_USE_TOR, settings.isTorEnabled)
    connectIntent.putStringArrayListExtra(
        HumlaService.EXTRAS_ACCESS_TOKENS,
        database.tokenDao().getAccessTokensByServerId(server.id) as ArrayList<String>
    )
    connectIntent.putExtra(HumlaService.EXTRAS_AUDIO_SOURCE, audioSource)
    connectIntent.putExtra(HumlaService.EXTRAS_AUDIO_STREAM, audioStream)
    connectIntent.putExtra(
        HumlaService.EXTRAS_FRAMES_PER_PACKET,
        settings.framesPerPacket
    )
    connectIntent.putExtra(
        HumlaService.EXTRAS_TRUST_STORE,
        TrustStore.getTrustStorePath(context)
    )
    connectIntent.putExtra(
        HumlaService.EXTRAS_TRUST_STORE_PASSWORD,
        TrustStore.trustStorePassword
    )
    connectIntent.putExtra(
        HumlaService.EXTRAS_TRUST_STORE_FORMAT,
        TrustStore.trustStoreFormat
    )
    connectIntent.putExtra(HumlaService.EXTRAS_HALF_DUPLEX, settings.isHalfDuplex)
    connectIntent.putExtra(
        HumlaService.EXTRAS_ENABLE_PREPROCESSOR,
        settings.isPreprocessorEnabled
    )

    //TODO: impl server.isSaved
//    if (server.isSaved) {
        val muteHistory = database.localMutedUserDao().getLocalMutedUserByServerId(server.id) as ArrayList<Long?>? //getLocalMutedUsers(server.id) as ArrayList<Int?>?
        val ignoreHistory = database.localIgnoredUserDao().getLocalIgnoredUserByServerId(server.id) as ArrayList<Long?>? //getLocalIgnoredUsers(server.id) as ArrayList<Int?>?
        connectIntent.putExtra(HumlaService.EXTRAS_LOCAL_MUTE_HISTORY, muteHistory)
        connectIntent.putExtra(HumlaService.EXTRAS_LOCAL_IGNORE_HISTORY, ignoreHistory)
//    }

    val certificateId: Long = PreferencesManager.getInstance(context).defaultCertId
    val certificate = database.certificateDao().getCertificateDataById(certificateId)
    if (certificate != null) {
        connectIntent.putExtra(
            HumlaService.EXTRAS_CERTIFICATE,
            certificate
        )
    }

    connectIntent.action = HumlaService.ACTION_CONNECT
    return connectIntent
}