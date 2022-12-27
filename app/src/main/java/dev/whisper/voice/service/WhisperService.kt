package dev.whisper.voice.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Binder
import android.os.IBinder
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import dev.whisper.core.HumlaService
import dev.whisper.core.util.HumlaException
import dev.whisper.voice.Constants.ACTION_DISCONNECT
import dev.whisper.voice.Constants.ACTION_MUTE_MIC
import dev.whisper.voice.Constants.ACTION_MUTE_SPK
import dev.whisper.voice.Constants.ACTION_UNMUTE_MIC
import dev.whisper.voice.Constants.ACTION_UNMUTE_SPK
import dev.whisper.voice.Constants.NOTIFICATION_CHANNEL_ID
import dev.whisper.voice.Constants.NOTIFICATION_CHANNEL_NAME
import dev.whisper.voice.Constants.NOTIFICATION_ID
import dev.whisper.voice.MainActivity
import dev.whisper.voice.R


class WhisperService : HumlaService(), OnSharedPreferenceChangeListener {
    companion object {
        const val RECONNECT_DELAY: Int = 10000
    }

    lateinit var notificationManager: NotificationManager
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = WhisperLocalBinder()

    private var muted: Boolean = false
    private var deafened: Boolean = false

    var volumeControlCallback: ((muted: Boolean, deafened: Boolean) -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply()
        {
            description = "Used for Chatting"
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.unregisterOnSharedPreferenceChangeListener(this)
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences, p1: String) {
        println("Changed: " + p1)
    }

    override fun setSelfMuteDeafState(mute: Boolean, deaf: Boolean) {
        this.muted = mute
        this.deafened = deaf
        updateVolumeState()
        super.setSelfMuteDeafState(mute, deaf)
    }

    private fun buildWhisperServicePendingIntentWithAction(action: String): PendingIntent {
        return PendingIntent.getService(
            this,
            0,
            Intent(this, WhisperService::class.java).apply {
                this.action = action
            },
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onConnectionEstablished() {
        super.onConnectionEstablished()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        notificationBuilder
            .setContentTitle("Voice Connected")
            .setContentText("${targetServer.name} / Root") //TODO: channel name
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setOngoing(true)
            .setSmallIcon(R.drawable.whisper_notification_white)
        buildNotificationActions()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun buildNotificationActions() {
        notificationBuilder
            .clearActions()
            .addAction(
                0,
                if (muted) "UnMute" else "Mute",
                buildWhisperServicePendingIntentWithAction(if (muted) ACTION_UNMUTE_MIC else ACTION_MUTE_MIC)
            )
            .addAction(
                0,
                if (deafened) "UnDeafen" else "Deafen",
                buildWhisperServicePendingIntentWithAction(if (deafened) ACTION_UNMUTE_SPK else ACTION_MUTE_SPK)
            )
            .addAction(
                0,
                "Disconnect",
                buildWhisperServicePendingIntentWithAction(ACTION_DISCONNECT)
            )
    }

    private fun updateVolumeState() {
        buildNotificationActions()
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                ACTION_DISCONNECT -> {

                }
                ACTION_MUTE_SPK -> {
                    setSelfMuteDeafState(muted, true)
                    updateVolumeState()
                    volumeControlCallback?.invoke(muted, deafened)
                }
                ACTION_UNMUTE_SPK -> {
                    setSelfMuteDeafState(muted, false)
                    updateVolumeState()
                    volumeControlCallback?.invoke(muted, deafened)
                }
                ACTION_MUTE_MIC -> {
                    setSelfMuteDeafState(true, deafened)
                    updateVolumeState()
                    volumeControlCallback?.invoke(muted, deafened)
                }
                ACTION_UNMUTE_MIC -> {
                    setSelfMuteDeafState(false, deafened)
                    updateVolumeState()
                    volumeControlCallback?.invoke(muted, deafened)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onConnectionDisconnected(e: HumlaException?) {
        super.onConnectionDisconnected(e)
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }

    inner class WhisperLocalBinder : Binder() {
        fun getService(): WhisperService = this@WhisperService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}