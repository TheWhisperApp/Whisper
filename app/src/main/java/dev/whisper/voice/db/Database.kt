package dev.whisper.voice.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Server::class, Certificate::class, Token::class, LocalMutedUser::class, LocalIgnoredUser::class],
    version = 1
)
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun certificateDao(): CertificateDao
    abstract fun tokenDao(): TokenDao
    abstract fun localMutedUserDao(): LocalMutedUserDao
    abstract fun localIgnoredUserDao(): LocalIgnoredUserDao
}