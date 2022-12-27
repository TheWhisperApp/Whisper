package dev.whisper.voice

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.whisper.voice.db.AppDatabase
import dev.whisper.voice.db.ServerDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ServerEntityReadWriteTest {
    private lateinit var serverDao: ServerDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        serverDao = db.serverDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeServerAndReadInList() {
//        val server: Server =
//        val user: User = TestUtil.createUser(3).apply {
//            setName("george")
//        }
//        serverDao.addServer()
//        val byName = serverDao.getAll()
//        assertThat(byName.get(0), equalTo(user))
    }
}