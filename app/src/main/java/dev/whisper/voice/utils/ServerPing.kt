package dev.whisper.voice.utils


import dev.whisper.voice.db.Server
import dev.whisper.voice.entities.server.ServerPingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer


suspend fun pingServer(server: Server): ServerPingResponse {
    // Create ping message
    val buffer = ByteBuffer.allocate(12)
    buffer.putInt(0)            // type
    buffer.putLong(server.id)   // id
    val requestPkt = DatagramPacket(
        buffer.array(), 12,
        withContext(Dispatchers.IO) {
            InetAddress.getByName(server.host)
        }, server.port
    )

    // send
    val socket = withContext(Dispatchers.IO) {
        DatagramSocket()
    }
    socket.soTimeout = 1000
    socket.receiveBufferSize = 1024
    val startTime = System.nanoTime()
    withContext(Dispatchers.IO) {
        socket.send(requestPkt)
    }
    val responseBuffer = ByteArray(24)
    val responsePkt = DatagramPacket(responseBuffer, responseBuffer.size)
    withContext(Dispatchers.IO) {
        socket.receive(responsePkt)
    }
    val latency = ((System.nanoTime() - startTime) / 1000000).toInt()   // ms
    //    Log.d(
//        "Server version: "
//                + responseMeta.versionString
//                + " Users: "
//                + responseMeta.currentUsers.toString()
//                + "/"
//                + responseMeta.maximumUsers
//    )
    return ServerPingResponse(server, responseBuffer, latency)
}
