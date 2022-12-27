package dev.whisper.voice.db

import dev.whisper.core.model.Server

interface WhisperDatabase {
    fun open()
    fun close()
    val servers: List<Server>


    // NOTE: commented functions are implemented with Room
    //       see Dao.kt, Entity.kt for more info.

//    fun addServer(server: Server)
//    fun updateServer(server: Server)
//    fun removeServer(server: Server)
    fun isCommentSeen(hash: String?, commentHash: ByteArray?): Boolean
    fun markCommentSeen(hash: String?, commentHash: ByteArray?)
    fun getPinnedChannels(serverId: Long): List<Int?>?
    fun addPinnedChannel(serverId: Long, channelId: Int)
    fun removePinnedChannel(serverId: Long, channelId: Int)
    fun isChannelPinned(serverId: Long, channelId: Int): Boolean
    fun getAccessTokens(serverId: Long): List<String?>?
    fun addAccessToken(serverId: Long, token: String?)
    fun removeAccessToken(serverId: Long, token: String?)
//    fun getLocalMutedUsers(serverId: Long): List<Int?>?
//    fun addLocalMutedUser(serverId: Long, userId: Int)
//    fun removeLocalMutedUser(serverId: Long, userId: Int)
//    fun getLocalIgnoredUsers(serverId: Long): List<Int?>?
//    fun addLocalIgnoredUser(serverId: Long, userId: Int)
//    fun removeLocalIgnoredUser(serverId: Long, userId: Int)

    /**
     * Adds the given certificate binary blob to the database.
     * @param name The user-readable certificate name.
     * @param certificate A PKCS12 binary blob.
     * @return A handle for the newly created certificate.
     */
    fun addCertificate(name: String?, certificate: ByteArray?): DatabaseCertificate?
//    val certificates: List<Any?>?

    /**
     * Obtains the certificate data associated with the given certificate ID.
     * @param id The certificate ID to fetch the data of.
     * @return A binary representation of a PKCS12 certificate.
     */
//    fun getCertificateData(id: Long): ByteArray?

    /**
     * Removes the certificate with the given ID.
     * @param id The certificate's identifier.
     */
//    fun removeCertificate(id: Long)
}

class DatabaseCertificate(val id: Long, val name: String) {

    override fun toString(): String {
        return name
    }
}
