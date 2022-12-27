package dev.whisper.voice.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ServerDao {
    @Query("SELECT * FROM server")
    fun getAll(): List<Server>?

    @Insert
    fun addServer(server: Server): Unit

//    @Insert
//    fun addHumlaServer(server: HumlaServer): Unit

    @Update
    fun updateServer(server: Server): Unit

    @Query("UPDATE server SET password = :password WHERE id = :id")
    fun updateServerPasswordById(id: Long, password: String)

    @Delete
    fun removeServer(server: Server): Unit
}

@Dao
interface CertificateDao {
    @Query("SELECT * FROM certificate")
    fun getAll(): List<Certificate>?

    @Query("SELECT * FROM certificate WHERE id = :id")
    fun getCertificateById(id: Long): Certificate?

    @Query("SELECT data FROM certificate WHERE id = :id")
    fun getCertificateDataById(id: Long): ByteArray?

    @Insert
    fun addCertificate(cert: Certificate): Long

    @Query("DELETE FROM certificate WHERE id = :id")
    fun removeCertificateById(id: Long): Unit
}

@Dao
interface TokenDao {
//    @Query("SELECT * from token")
//    fun getAccessTokens(id: Long): List<String?>?

    @Query("SELECT value FROM token WHERE server = :id")
    fun getAccessTokensByServerId(id: Long): List<String>?

//    @Insert
//    fun addAccessToken(token: Token): Unit
//
//    @Delete
//    fun removeAccessToken(id: Int, token: Token?): Unit
}

@Dao
interface LocalMutedUserDao {

    @Query("SELECT user FROM local_mute WHERE server = :id")
    fun getLocalMutedUserByServerId(id: Long): List<Int?>?

    @Insert
    fun addLocalMutedUser(localMutedUser: LocalMutedUser): Unit

    @Delete
    fun removeLocalMutedUser(localMutedUser: LocalMutedUser): Unit
}

@Dao
interface LocalIgnoredUserDao {

    @Query("SELECT user FROM local_ignore WHERE server = :id")
    fun getLocalIgnoredUserByServerId(id: Long): List<Int?>?

    @Insert
    fun addLocalIgnoredUser(localIgnoredUser: LocalIgnoredUser): Unit

    @Delete
    fun removeLocalIgnoredUser(localIgnoredUser: LocalIgnoredUser): Unit
}