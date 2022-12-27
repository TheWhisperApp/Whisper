package dev.whisper.voice.entities.server

import dev.whisper.voice.db.Server
import java.nio.ByteBuffer

class ServerPingResponse {
    var identifier: Long = 0
        private set
    var version = 0
        private set
    var currentUsers = 0
        private set
    var maximumUsers = 0
        private set
    var allowedBandwidth = 0
        private set
    var latency = 0
        private set
    var server: Server? = null
        private set

    /**
     * Whether or not this server info response represents a failure to retrieve a response. Used to efficiently denote failed responses.
     */
    var isDummy = false
        private set

    /**
     * Creates a ServerInfoResponse object with the bytes obtained from the server.
     * @param response The response to the UDP pings sent by the server.
     * @see http://mumble.sourceforge.net/Protocol
     */
    constructor(server: Server, response: ByteArray, latency: Int) {
        val buffer = ByteBuffer.wrap(response)
        version = buffer.int
        identifier = buffer.long
        currentUsers = buffer.int
        maximumUsers = buffer.int
        allowedBandwidth = buffer.int
        this.latency = latency
        this.server = server
    }

    /**
     * Instantiating a ServerInfoResponse with no data will cause it to be considered a 'dummy' response by its handler.
     */
    constructor() {
        isDummy = true
    }

    val versionString: String
        get() {
            val versionBytes = ByteBuffer.allocate(4).putInt(version).array()
            return String.format(
                "%d.%d.%d",
                versionBytes[1].toInt(),
                versionBytes[2].toInt(),
                versionBytes[3].toInt()
            )
        }
}