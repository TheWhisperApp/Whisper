package dev.whisper.voice.service

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Observer
import dev.whisper.core.model.IChannel
import dev.whisper.core.model.IMessage
import dev.whisper.core.model.IUser
import dev.whisper.core.util.HumlaException
import dev.whisper.core.util.IHumlaObserver
import dev.whisper.core.util.VoiceTargetMode
import dev.whisper.voice.db.AppDatabase
import dev.whisper.voice.db.WhisperDatabase
import dev.whisper.voice.viewmodels.MessageUiEntity
import dev.whisper.voice.viewmodels.MessagingPageViewModel
import dev.whisper.voice.viewmodels.ServerViewModel
import dev.whisper.voice.viewmodels.VolumeControlViewModel
import kotlinx.coroutines.CompletableDeferred
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

class WhisperServiceManager(
    private val context: Context,
    private val onPermissionRequest: suspend (permissions: String) -> Boolean,
    private val database: AppDatabase,
    private val messageViewModel: MessagingPageViewModel,
    private val volumeControlViewModel: VolumeControlViewModel
) {
    private var service: WhisperService? = null
    private var connectDeferred: CompletableDeferred<Unit>? = null
    private var disconnectDeferred: CompletableDeferred<Unit>? = null
    private val observer: WhisperServiceObserver = WhisperServiceObserver()
    private var server: ServerViewModel? = null

    val bound get() = service != null
    private var _conected: Boolean = false
    val connected get() = service != null && _conected
    val connecting get() = connectDeferred != null

    val rootChannel: IChannel?
        get() {
            if (service != null && service!!.isSynchronized) {
                return service?.rootChannel
            }
            return null
        }

    init {
        volumeControlViewModel.muted.observeForever {
            if (connected) {
                service!!.setSelfMuteDeafState(it, volumeControlViewModel.deafened.value!!)
            }
        }
        volumeControlViewModel.deafened.observeForever {
            if (connected) {
                service!!.setSelfMuteDeafState(volumeControlViewModel.muted.value!!, it)
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as WhisperService.WhisperLocalBinder
            this@WhisperServiceManager.service = binder.getService()
            this@WhisperServiceManager.service!!.registerObserver(observer)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }

    }

    fun bindService() {
        val intent = Intent(context, WhisperService::class.java)
        context.bindService(intent, connection, 0)
    }

    fun unbindService() {
        context.unbindService(connection)
    }

    fun sendMessage(message: String) {
        if (connected) {
            service!!.sendChannelTextMessage(service!!.rootChannel.id, message, false)
        }
    }

    suspend fun connectToServer(server: ServerViewModel) {
        if (connecting) {
            throw IllegalStateException("Does not support simultaneous connection to multiple servers")
        }

        if (connected) {
            disconnectDeferred = CompletableDeferred()
            service!!.disconnect()
            disconnectDeferred!!.await()
        }

        if (onPermissionRequest(Manifest.permission.RECORD_AUDIO)) {
            val serviceIntent = buildWhisperServiceIntent(context, database, server.asServer())
            connectDeferred = CompletableDeferred()
            this.server = server

            context.startService(serviceIntent)
            context.bindService(serviceIntent, connection, 0)

            try {
                connectDeferred!!.await()
            } finally {
                connectDeferred = null
            }

            service!!.volumeControlCallback = { muted, deafened ->
                volumeControlViewModel.muted.value = muted
                volumeControlViewModel.deafened.value = deafened
            }
        } else {
            throw WhisperAudioPermissionDeniedException()
        }
    }

    inner class WhisperServiceObserver : IHumlaObserver {
        override fun onConnected() {
            assert(connecting)
            connectDeferred!!.complete(Unit)
            connectDeferred = null
            _conected = true
            server!!.updateChannel(service!!.rootChannel)
            updateMessaging()
        }

        override fun onConnecting() {
        }

        override fun onDisconnected(e: HumlaException?) {
            // onDisconnected will be called multiple times
            if (connecting && e != null) {
                connectDeferred!!.completeExceptionally(
                    WhisperServiceConnectException(
                        humlaException = e
                    )
                )
                connectDeferred = null
            }
            if (disconnectDeferred != null) {
                disconnectDeferred!!.complete(Unit)
                disconnectDeferred = null
            }

            server = null
            _conected = false
        }

        override fun onTLSHandshakeFailed(chain: Array<X509Certificate>?) {
            assert(connecting)
            connectDeferred!!.completeExceptionally(WhisperServiceConnectException(invalidChain = chain))
        }

        private fun updateMessaging() {
            if (connected) {
                // TODO: other channel
                messageViewModel.onlineMemberCount.value = service!!.rootChannel.subchannelUserCount
            }
        }

        // TODO: more efficient update
        private fun updateChannels() {
            if (connected) {
                assert(server != null)
                server!!.updateChannel(service!!.rootChannel)
            }
        }

        override fun onChannelAdded(channel: IChannel) {
            updateChannels()
        }

        override fun onChannelStateUpdated(channel: IChannel?) {
            updateChannels()
        }

        override fun onChannelRemoved(channel: IChannel?) {
            updateChannels()
        }

        override fun onChannelPermissionsUpdated(channel: IChannel?) {
//            TODO("Not yet implemented")
        }

        override fun onUserConnected(user: IUser?) {
            updateChannels()
            updateMessaging()
        }

        override fun onUserStateUpdated(user: IUser?) {
            updateChannels()
            updateMessaging()
        }

        override fun onUserTalkStateUpdated(user: IUser?) {
            updateChannels()
            updateMessaging()
        }

        override fun onUserJoinedChannel(
            user: IUser?,
            newChannel: IChannel?,
            oldChannel: IChannel?
        ) {
//            TODO("Not yet implemented")
        }

        override fun onUserRemoved(user: IUser?, reason: String?) {
//            TODO("Not yet implemented")
        }

        override fun onPermissionDenied(reason: String?) {
//            TODO("Not yet implemented")
        }

        override fun onMessageLogged(message: IMessage) {
//            TODO("Not yet implemented")\
            // TODO: handle message ini other channels
            messageViewModel.addMessage(
                MessageUiEntity(
                    author = message.actorName,
                    content = message.message,
                    timestamp = OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(message.receivedTime),
                        ZoneId.systemDefault()
                    ),
                )
            )
        }

        override fun onVoiceTargetChanged(mode: VoiceTargetMode?) {
//            TODO("Not yet implemented")
        }

        override fun onLogInfo(message: String) {
            Log.i("WhisperService", message)
        }

        override fun onLogWarning(message: String) {
            Log.w("WhisperService", message)
        }

        override fun onLogError(message: String) {
            Log.e("WhisperService", message)
        }

    }
}