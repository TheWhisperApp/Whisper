package dev.whisper.voice.db
//
//import android.content.ContentValues
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import android.util.Log
//
//import dev.whisper.core.model.Server
//
//class WhisperSQLite : SQLiteOpenHelper, WhisperDatabase {
//    constructor(context: Context?) : super(context, DATABASE_NAME, null, CURRENT_DB_VERSION) {}
//    constructor(context: Context?, name: String?) : super(
//        context,
//        name,
//        null,
//        CURRENT_DB_VERSION
//    )
//
//    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL(TABLE_SERVER_CREATE_SQL)
//        db.execSQL(TABLE_FAVOURITES_CREATE_SQL)
//        db.execSQL(TABLE_TOKENS_CREATE_SQL)
//        db.execSQL(TABLE_COMMENTS_CREATE_SQL)
//        db.execSQL(TABLE_LOCAL_MUTE_CREATE_SQL)
//        db.execSQL(TABLE_LOCAL_IGNORE_CREATE_SQL)
//        db.execSQL(TABLE_CERTIFICATES_CREATE_SQL)
//    }
//
//    override fun onUpgrade(
//        db: SQLiteDatabase,
//        oldVersion: Int,
//        newVersion: Int
//    ) {
//        Log.w(
//            TAG,
//            "Database upgrade from $oldVersion to $newVersion"
//        )
//        if (oldVersion <= PRE_FAVOURITES_DB_VERSION) {
//            db.execSQL(TABLE_FAVOURITES_CREATE_SQL)
//        }
//        if (oldVersion <= PRE_TOKENS_DB_VERSION) {
//            db.execSQL(TABLE_TOKENS_CREATE_SQL)
//        }
//        if (oldVersion <= PRE_COMMENTS_DB_VERSION) {
//            db.execSQL(TABLE_COMMENTS_CREATE_SQL)
//        }
//        if (oldVersion <= PRE_LOCAL_MUTE_DB_VERSION) {
//            db.execSQL(TABLE_LOCAL_MUTE_CREATE_SQL)
//        }
//        if (oldVersion <= PRE_LOCAL_IGNORE_DB_VERSION) {
//            db.execSQL(TABLE_LOCAL_IGNORE_CREATE_SQL)
//        }
//        if (oldVersion <= PRE_CERTIFICATES_DB_VERSION) {
//            db.execSQL(TABLE_CERTIFICATES_CREATE_SQL)
//        }
//    }
//
//    override fun open() {
//        // Do nothing. Database will be opened automatically when accessing it.
//    }
//
//    override val servers: List<Server>
//        get() {
//            val c = readableDatabase.query(
//                TABLE_SERVER, arrayOf(
//                    SERVER_ID, SERVER_NAME, SERVER_HOST,
//                    SERVER_PORT, SERVER_USERNAME, SERVER_PASSWORD
//                ),
//                null,
//                null,
//                null,
//                null,
//                null
//            )
//            val servers: MutableList<Server> = ArrayList<Server>()
//            c.moveToFirst()
//            while (!c.isAfterLast) {
//                val server = Server(
//                    c.getInt(c.getColumnIndex(SERVER_ID)).toLong(),
//                    c.getString(c.getColumnIndex(SERVER_NAME)),
//                    c.getString(c.getColumnIndex(SERVER_HOST)),
//                    c.getInt(c.getColumnIndex(SERVER_PORT)),
//                    c.getString(c.getColumnIndex(SERVER_USERNAME)),
//                    c.getString(c.getColumnIndex(SERVER_PASSWORD))
//                )
//                servers.add(server)
//                c.moveToNext()
//            }
//            c.close()
//            return servers
//        }
//
//    override fun addServer(server: Server) {
//        val values = ContentValues()
//        values.put(SERVER_NAME, server.name)
//        values.put(SERVER_HOST, server.host)
//        values.put(SERVER_PORT, server.port)
//        values.put(SERVER_USERNAME, server.username)
//        values.put(SERVER_PASSWORD, server.password)
//        server.setId(writableDatabase.insert(TABLE_SERVER, null, values))
//    }
//
//    override fun updateServer(server: Server) {
//        val values = ContentValues()
//        values.put(SERVER_NAME, server.name)
//        values.put(SERVER_HOST, server.host)
//        values.put(SERVER_PORT, server.port)
//        values.put(SERVER_USERNAME, server.username)
//        values.put(SERVER_PASSWORD, server.password)
//        writableDatabase.update(
//            TABLE_SERVER,
//            values,
//            "$SERVER_ID=?", arrayOf(server.id.toString())
//        )
//    }
//
//    override fun removeServer(server: Server) {
//        writableDatabase.delete(
//            TABLE_SERVER,
//            "$SERVER_ID=?",
//            arrayOf(java.lang.String.valueOf(server.id))
//        )
//        // Clean up server-specific entries
//        writableDatabase.delete(
//            TABLE_FAVOURITES,
//            "$FAVOURITES_SERVER=?",
//            arrayOf(java.lang.String.valueOf(server.id))
//        )
//        writableDatabase.delete(
//            TABLE_TOKENS,
//            "$TOKENS_SERVER=?",
//            arrayOf(java.lang.String.valueOf(server.id))
//        )
//        writableDatabase.delete(
//            TABLE_LOCAL_MUTE,
//            "$LOCAL_MUTE_SERVER=?",
//            arrayOf(java.lang.String.valueOf(server.id))
//        )
//        writableDatabase.delete(
//            TABLE_LOCAL_IGNORE,
//            "$LOCAL_IGNORE_SERVER=?",
//            arrayOf(java.lang.String.valueOf(server.id))
//        )
//    }
//
//    override fun getPinnedChannels(serverId: Long): List<Int> {
//        val c = readableDatabase.query(
//            TABLE_FAVOURITES, arrayOf(FAVOURITES_CHANNEL),
//            "$FAVOURITES_SERVER=?", arrayOf(serverId.toString()),
//            null,
//            null,
//            null
//        )
//        val favourites: MutableList<Int> = ArrayList()
//        c.moveToFirst()
//        while (!c.isAfterLast) {
//            favourites.add(c.getInt(0))
//            c.moveToNext()
//        }
//        c.close()
//        return favourites
//    }
//
//    override fun addPinnedChannel(serverId: Long, channelId: Int) {
//        val contentValues = ContentValues()
//        contentValues.put(FAVOURITES_CHANNEL, channelId)
//        contentValues.put(FAVOURITES_SERVER, serverId)
//        writableDatabase.insert(TABLE_FAVOURITES, null, contentValues)
//    }
//
//    override fun isChannelPinned(serverId: Long, channelId: Int): Boolean {
//        val c = readableDatabase.query(
//            TABLE_FAVOURITES, arrayOf(FAVOURITES_CHANNEL),
//            FAVOURITES_SERVER + "=? AND " +
//                    FAVOURITES_CHANNEL + "=?", arrayOf(serverId.toString(), channelId.toString()),
//            null,
//            null,
//            null
//        )
//        c.moveToFirst()
//        return !c.isAfterLast
//    }
//
//    override fun removePinnedChannel(serverId: Long, channelId: Int) {
//        writableDatabase.delete(
//            TABLE_FAVOURITES,
//            "server = ? AND channel = ?",
//            arrayOf(serverId.toString(), channelId.toString())
//        )
//    }
//
//    override fun getAccessTokens(serverId: Long): List<String> {
//        val cursor = readableDatabase.query(
//            TABLE_TOKENS,
//            arrayOf(TOKENS_VALUE),
//            "$TOKENS_SERVER=?",
//            arrayOf(serverId.toString()),
//            null,
//            null,
//            null
//        )
//        cursor.moveToFirst()
//        val tokens: MutableList<String> = ArrayList()
//        while (!cursor.isAfterLast) {
//            tokens.add(cursor.getString(0))
//            cursor.moveToNext()
//        }
//        cursor.close()
//        return tokens
//    }
//
//    override fun addAccessToken(serverId: Long, token: String?) {
//        val contentValues = ContentValues()
//        contentValues.put(TOKENS_SERVER, serverId)
//        contentValues.put(TOKENS_VALUE, token)
//        writableDatabase.insert(TABLE_TOKENS, null, contentValues)
//    }
//
//    override fun removeAccessToken(serverId: Long, token: String?) {
//        writableDatabase.delete(
//            TABLE_TOKENS,
//            "$TOKENS_SERVER=? AND $TOKENS_VALUE=?",
//            arrayOf(serverId.toString(), token)
//        )
//    }
//
//    override fun getLocalMutedUsers(serverId: Long): List<Int> {
//        val cursor = readableDatabase.query(
//            TABLE_LOCAL_MUTE, arrayOf(LOCAL_MUTE_USER),
//            "$LOCAL_MUTE_SERVER=?", arrayOf(serverId.toString()),
//            null, null, null
//        )
//        cursor.moveToNext()
//        val users: MutableList<Int> = ArrayList()
//        while (!cursor.isAfterLast) {
//            users.add(cursor.getInt(0))
//            cursor.moveToNext()
//        }
//        return users
//    }
//
//    override fun addLocalMutedUser(serverId: Long, userId: Int) {
//        val values = ContentValues()
//        values.put(LOCAL_MUTE_SERVER, serverId)
//        values.put(LOCAL_MUTE_USER, userId)
//        writableDatabase.insert(TABLE_LOCAL_MUTE, null, values)
//    }
//
//    override fun removeLocalMutedUser(serverId: Long, userId: Int) {
//        writableDatabase.delete(
//            TABLE_LOCAL_MUTE,
//            "$LOCAL_MUTE_SERVER=? AND $LOCAL_MUTE_USER=?",
//            arrayOf(serverId.toString(), userId.toString())
//        )
//    }
//
//    override fun getLocalIgnoredUsers(serverId: Long): List<Int> {
//        val cursor = readableDatabase.query(
//            TABLE_LOCAL_IGNORE, arrayOf(LOCAL_IGNORE_USER),
//            "$LOCAL_IGNORE_SERVER=?", arrayOf(serverId.toString()),
//            null, null, null
//        )
//        cursor.moveToFirst()
//        val users: MutableList<Int> = ArrayList()
//        while (!cursor.isAfterLast) {
//            users.add(cursor.getInt(0))
//            cursor.moveToNext()
//        }
//        return users
//    }
//
//    override fun addLocalIgnoredUser(serverId: Long, userId: Int) {
//        val values = ContentValues()
//        values.put(LOCAL_IGNORE_SERVER, serverId)
//        values.put(LOCAL_IGNORE_USER, userId)
//        writableDatabase.insert(TABLE_LOCAL_IGNORE, null, values)
//    }
//
//    override fun removeLocalIgnoredUser(serverId: Long, userId: Int) {
//        writableDatabase.delete(
//            TABLE_LOCAL_IGNORE,
//            "$LOCAL_IGNORE_SERVER=? AND $LOCAL_IGNORE_USER=?",
//            arrayOf(serverId.toString(), userId.toString())
//        )
//    }
//
//    override fun addCertificate(name: String?, certificate: ByteArray?): DatabaseCertificate {
//        val values = ContentValues()
//        values.put(COLUMN_CERTIFICATES_NAME, name)
//        values.put(COLUMN_CERTIFICATES_DATA, certificate)
//        val id = writableDatabase.insert(TABLE_CERTIFICATES, null, values)
//        return DatabaseCertificate(id, (name)!!)
//    }
//
//    override val certificates: List<DatabaseCertificate>
//        get() {
//            val cursor = readableDatabase.query(
//                TABLE_CERTIFICATES, arrayOf(COLUMN_CERTIFICATES_ID, COLUMN_CERTIFICATES_NAME),
//                null, null, null, null, null
//            )
//            val certificates: MutableList<DatabaseCertificate> = ArrayList()
//            cursor.moveToFirst()
//            while (!cursor.isAfterLast) {
//                certificates.add(DatabaseCertificate(cursor.getLong(0), cursor.getString(1)))
//                cursor.moveToNext()
//            }
//            cursor.close()
//            return certificates
//        }
//
//    override fun getCertificateData(id: Long): ByteArray? {
//        val cursor = readableDatabase.query(
//            TABLE_CERTIFICATES, arrayOf(COLUMN_CERTIFICATES_DATA),
//            "$COLUMN_CERTIFICATES_ID=?", arrayOf(id.toString()), null, null, null
//        )
//        if (!cursor.moveToFirst()) return null
//        val data = cursor.getBlob(0)
//        cursor.close()
//        return data
//    }
//
//    override fun removeCertificate(id: Long) {
//        writableDatabase.delete(
//            TABLE_CERTIFICATES,
//            "$COLUMN_CERTIFICATES_ID=?", arrayOf(id.toString())
//        )
//    }
//
//    override fun isCommentSeen(hash: String?, commentHash: ByteArray?): Boolean {
//        val cursor = readableDatabase.query(
//            TABLE_COMMENTS,
//            arrayOf(COMMENTS_WHO, COMMENTS_COMMENT, COMMENTS_SEEN),
//            "$COMMENTS_WHO=? AND $COMMENTS_COMMENT=?",
//            arrayOf(
//                hash, String(
//                    (commentHash)!!
//                )
//            ),
//            null,
//            null,
//            null
//        )
//        val hasNext = cursor.moveToNext()
//        cursor.close()
//        return hasNext
//    }
//
//    override fun markCommentSeen(hash: String?, commentHash: ByteArray?) {
//        val values = ContentValues()
//        values.put(COMMENTS_WHO, hash)
//        values.put(COMMENTS_COMMENT, commentHash)
//        values.put(COMMENTS_SEEN, "datetime('now')")
//        writableDatabase.replace(TABLE_COMMENTS, null, values)
//    }
//
//    companion object {
//        private val TAG = WhisperSQLite::class.java.name
//        const val DATABASE_NAME = "mumble.db"
//        const val TABLE_SERVER = "server"
//        const val SERVER_ID = "_id"
//        const val SERVER_NAME = "name"
//        const val SERVER_HOST = "host"
//        const val SERVER_PORT = "port"
//        const val SERVER_USERNAME = "username"
//        const val SERVER_PASSWORD = "password"
//        const val TABLE_SERVER_CREATE_SQL = ("CREATE TABLE IF NOT EXISTS `" + TABLE_SERVER + "` ("
//                + "`" + SERVER_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + "`" + SERVER_NAME + "` TEXT NOT NULL,"
//                + "`" + SERVER_HOST + "` TEXT NOT NULL,"
//                + "`" + SERVER_PORT + "` INTEGER,"
//                + "`" + SERVER_USERNAME + "` TEXT NOT NULL,"
//                + "`" + SERVER_PASSWORD + "` TEXT"
//                + ");")
//        const val TABLE_FAVOURITES = "favourites"
//        const val FAVOURITES_ID = "_id"
//        const val FAVOURITES_CHANNEL = "channel"
//        const val FAVOURITES_SERVER = "server"
//        const val TABLE_FAVOURITES_CREATE_SQL = ("CREATE TABLE IF NOT EXISTS `" + TABLE_FAVOURITES + "` ("
//                + "`" + FAVOURITES_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + "`" + FAVOURITES_CHANNEL + "` TEXT NOT NULL,"
//                + "`" + FAVOURITES_SERVER + "` INTEGER NOT NULL"
//                + ");")
//        const val TABLE_TOKENS = "tokens"
//        const val TOKENS_ID = "_id"
//        const val TOKENS_VALUE = "value"
//        const val TOKENS_SERVER = "server"
//        const val TABLE_TOKENS_CREATE_SQL = ("CREATE TABLE IF NOT EXISTS `" + TABLE_TOKENS + "` ("
//                + "`" + TOKENS_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + "`" + TOKENS_VALUE + "` TEXT NOT NULL,"
//                + "`" + TOKENS_SERVER + "` INTEGER NOT NULL"
//                + ");")
//        const val TABLE_COMMENTS = "comments"
//        const val COMMENTS_WHO = "who"
//        const val COMMENTS_COMMENT = "comment"
//        const val COMMENTS_SEEN = "seen"
//        const val TABLE_COMMENTS_CREATE_SQL = ("CREATE TABLE IF NOT EXISTS `" + TABLE_COMMENTS + "` ("
//                + "`" + COMMENTS_WHO + "` TEXT NOT NULL,"
//                + "`" + COMMENTS_COMMENT + "` TEXT NOT NULL,"
//                + "`" + COMMENTS_SEEN + "` DATE NOT NULL"
//                + ");")
//        const val TABLE_LOCAL_MUTE = "local_mute"
//        const val LOCAL_MUTE_SERVER = "server"
//        const val LOCAL_MUTE_USER = "user"
//        const val TABLE_LOCAL_MUTE_CREATE_SQL = ("CREATE TABLE IF NOT EXISTS " + TABLE_LOCAL_MUTE + " ("
//                + "`" + LOCAL_MUTE_SERVER + "` INTEGER NOT NULL,"
//                + "`" + LOCAL_MUTE_USER + "` INTEGER NOT NULL,"
//                + "CONSTRAINT server_user UNIQUE(" + LOCAL_MUTE_SERVER + "," + LOCAL_MUTE_USER + ")"
//                + ");")
//        const val TABLE_LOCAL_IGNORE = "local_ignore"
//        const val LOCAL_IGNORE_SERVER = "server"
//        const val LOCAL_IGNORE_USER = "user"
//        const val TABLE_LOCAL_IGNORE_CREATE_SQL =
//            ("CREATE TABLE IF NOT EXISTS " + TABLE_LOCAL_IGNORE + " ("
//                    + "`" + LOCAL_IGNORE_SERVER + "` INTEGER NOT NULL,"
//                    + "`" + LOCAL_IGNORE_USER + "` INTEGER NOT NULL,"
//                    + "CONSTRAINT server_user UNIQUE(" + LOCAL_IGNORE_SERVER + "," + LOCAL_IGNORE_USER + ")"
//                    + ");")
//        const val TABLE_CERTIFICATES = "certificates"
//        const val COLUMN_CERTIFICATES_ID = "_id"
//        const val COLUMN_CERTIFICATES_DATA = "data"
//        const val COLUMN_CERTIFICATES_NAME = "name"
//        const val TABLE_CERTIFICATES_CREATE_SQL =
//            ("CREATE TABLE IF NOT EXISTS " + TABLE_CERTIFICATES + " ("
//                    + "`" + COLUMN_CERTIFICATES_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + "`" + COLUMN_CERTIFICATES_DATA + "` BLOB NOT NULL,"
//                    + "`" + COLUMN_CERTIFICATES_NAME + "` TEXT NOT NULL"
//                    + ");")
//        const val PRE_FAVOURITES_DB_VERSION = 2
//        const val PRE_TOKENS_DB_VERSION = 3
//        const val PRE_COMMENTS_DB_VERSION = 4
//        const val PRE_LOCAL_MUTE_DB_VERSION = 5
//        const val PRE_LOCAL_IGNORE_DB_VERSION = 6
//        const val PRE_CERTIFICATES_DB_VERSION = 7
//        const val CURRENT_DB_VERSION = 8
//    }
//}