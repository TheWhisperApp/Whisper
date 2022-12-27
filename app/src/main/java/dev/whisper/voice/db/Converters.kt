package dev.whisper.voice.db

import androidx.room.TypeConverter

typealias HumlaServer = dev.whisper.core.model.Server

//class Converters {
//    @TypeConverter
//    fun fromHumlaServer(server: HumlaServer): Server {
//        return Server(
//            id = server.id,
//            name = server.name,
//            host = server.host,
//            port = server.port,
//            username = server.username,
//            password = server.password
//        )
//    }
//
//    @TypeConverter
//    fun fromRoomServer(server: Server): HumlaServer {
//        return HumlaServer(
//            id = server.id,
//            name = server.name,
//            host = server.host,
//            port = server.port,
//            username = server.username,
//            password = server.password
//        )
//    }
//}
