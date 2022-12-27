package dev.whisper.voice.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "server")
data class Server(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "host") val host: String,
    @ColumnInfo(name = "port") val port: Int,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String
) {
    fun toHumlaServer(): HumlaServer {
        return HumlaServer(
            this.id,
            this.name,
            this.host,
            this.port,
            this.username,
            this.password
        )
    }
}

@Entity(tableName = "certificate")
data class Certificate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "data") val data: ByteArray
)

@Entity(tableName = "token")
data class Token(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "server") val server: Long
)

@Entity(
    tableName = "local_mute",
    primaryKeys = ["server", "user"],
    indices = [Index(value = ["server", "user"], unique = true)]
)
data class LocalMutedUser(
    @ColumnInfo(name = "server") val server: Long,
    @ColumnInfo(name = "user") val user: Long
)

@Entity(
    tableName = "local_ignore",
    primaryKeys = ["server", "user"],
    indices = [Index(value = ["server", "user"], unique = true)]
)
data class LocalIgnoredUser(
    @ColumnInfo(name = "server") val server: Long,
    @ColumnInfo(name = "user") val user: Long
)