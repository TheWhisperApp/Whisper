package dev.whisper.voice.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import dev.whisper.voice.db.AppDatabase
import dev.whisper.voice.db.WhisperDatabase
import kotlinx.coroutines.runInterruptible

class ServerListViewModel(
    initialServerList: List<ServerViewModel> = emptyList(),
    private val database: AppDatabase
) : ViewModel() {
    private val _serverList: SnapshotStateList<ServerViewModel> = mutableStateListOf()
    val serverList: List<ServerViewModel> get() = _serverList

    init {
        _serverList.addAll(initialServerList)
    }

    suspend fun loadServerList() {
        runInterruptible {
            _serverList.addAll(database.serverDao().getAll()?.map { server ->
                ServerViewModel(server, database)
            }?: emptyList())
        }
        _serverList.forEach {
            it.ping()
        }
    }

    fun addServer(server: ServerViewModel) {
        database.serverDao().addServer(server.asServer())
        _serverList.add(server)
    }
    fun removeServer(server: ServerViewModel) {
        database.serverDao().removeServer(server.asServer())
        _serverList.remove(server)
    }

}