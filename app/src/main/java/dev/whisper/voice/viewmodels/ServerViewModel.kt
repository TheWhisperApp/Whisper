package dev.whisper.voice.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.whisper.core.model.IChannel
import dev.whisper.voice.db.AppDatabase
import dev.whisper.voice.db.Server
import dev.whisper.voice.utils.pingServer

class ServerViewModel(
    val server: Server,
    private val database: AppDatabase,
) : ViewModel() {
    var connecting = mutableStateOf(false)
    var connected = mutableStateOf(false)
    var latency = mutableStateOf(-1)
    var currentUsers = mutableStateOf(-1)
    val id get() = server.id

//    private val _name = MutableLiveData(server.name)
//    val name: LiveData<String> = _name
//
//    private val _host = MutableLiveData(server.host)
//    val host: LiveData<String> = _host

    private val _password = MutableLiveData(server.password)
    val password: LiveData<String> = _password

    fun update(password: String = "") {
        _password.value = password
        database.serverDao().updateServerPasswordById(this.id, password)
    }

    suspend fun ping() {
        try {
            val res = pingServer(server)
            currentUsers.value = res.currentUsers
            latency.value = res.latency
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val name: String get() = server.name
    val host: String get() = server.host
    val port: Int get() = server.port
    val username: String get() = server.username

    var channels = MutableLiveData<TreeNodeViewModel<ChannelViewModel>?>(null)

    fun updateChannel(rootChannel: IChannel) {
        channels.value = TreeNodeViewModel(
            value = ChannelViewModel(rootChannel)
        )
        buildChannelTree(channels.value!!)
    }

    private fun buildChannelTree(
        parent: TreeNodeViewModel<ChannelViewModel>
    ) {
        parent.value.value.users.addAll(parent.value.value.channel.users.map {
            UserViewModel(it)
        })
        for (channel in parent.value.value.channel.subchannels) {
            val c = TreeNodeViewModel(
                value = ChannelViewModel(channel)
            )
            parent.children.add(c)
            buildChannelTree(c)
        }
    }

    val totalMembersCount: Int = 0
    val onlineMembersCount: Int = 0
    val description: String? = null


    fun asServer(): Server {
        return Server(
            id = this.id,
            name = this.name,
            host = this.host,
            port = this.port,
            password = this.password.value!!,
            username = this.username,

        )
    }
}