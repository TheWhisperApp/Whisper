package dev.whisper.voice.db

import dev.whisper.core.model.Server
//
//class InMemoryDatabase(
//    override val servers: List<Server> = emptyList(),
//    override val certificates: List<Any?>? = null
//) : WhisperDatabase {
//    override fun open() {
//        TODO("Not yet implemented")
//    }
//
//    override fun close() {
//        TODO("Not yet implemented")
//    }
//
//    override fun addServer(server: Server) {
//        TODO("Not yet implemented")
//    }
//
//    override fun updateServer(server: Server) {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeServer(server: Server) {
//        TODO("Not yet implemented")
//    }
//
//    override fun isCommentSeen(hash: String?, commentHash: ByteArray?): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun markCommentSeen(hash: String?, commentHash: ByteArray?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getPinnedChannels(serverId: Long): List<Int?>? {
//        TODO("Not yet implemented")
//    }
//
//    override fun addPinnedChannel(serverId: Long, channelId: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun removePinnedChannel(serverId: Long, channelId: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun isChannelPinned(serverId: Long, channelId: Int): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun getAccessTokens(serverId: Long): List<String?>? {
//        TODO("Not yet implemented")
//    }
//
//    override fun addAccessToken(serverId: Long, token: String?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeAccessToken(serverId: Long, token: String?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getLocalMutedUsers(serverId: Long): List<Int?>? {
//        TODO("Not yet implemented")
//    }
//
//    override fun addLocalMutedUser(serverId: Long, userId: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeLocalMutedUser(serverId: Long, userId: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getLocalIgnoredUsers(serverId: Long): List<Int?>? {
//        TODO("Not yet implemented")
//    }
//
//    override fun addLocalIgnoredUser(serverId: Long, userId: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeLocalIgnoredUser(serverId: Long, userId: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun addCertificate(name: String?, certificate: ByteArray?): DatabaseCertificate? {
//        TODO("Not yet implemented")
//    }
//
//    override fun getCertificateData(id: Long): ByteArray? {
//        TODO("Not yet implemented")
//    }
//
//    override fun removeCertificate(id: Long) {
//        TODO("Not yet implemented")
//    }
//}