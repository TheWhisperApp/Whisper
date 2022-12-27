package dev.whisper.voice

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import dev.whisper.voice.db.AppDatabase
import dev.whisper.voice.preferences.PreferencesManager
import dev.whisper.voice.service.WhisperServiceManager
import dev.whisper.voice.utils.generateCertificate
import dev.whisper.voice.ui.routes.focus.HomeScreen
import dev.whisper.voice.ui.routes.focus.components.dialog.GeneralAlertDialog
import dev.whisper.voice.ui.theme.WhisperTheme
import dev.whisper.voice.viewmodels.BottomControlViewModel
import dev.whisper.voice.viewmodels.MessagingPageViewModel
import dev.whisper.voice.viewmodels.ServerListViewModel
import dev.whisper.voice.viewmodels.VolumeControlViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch


@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {

    private lateinit var database : AppDatabase
    private lateinit var whisperServiceManager: WhisperServiceManager

    private var multiplePermissionRequestDeferred: CompletableDeferred<Map<String, Boolean>>? = null
    private val multiplePermissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        multiplePermissionRequestDeferred?.complete(result)
    }

    private var singlePermissionRequestDeferred: CompletableDeferred<Boolean>? = null
    private val singlePermissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        singlePermissionRequestDeferred?.complete(result)
    }

    private val volumeControlViewModel = VolumeControlViewModel()
    private val messagingViewModel = MessagingPageViewModel(
        initialMessages = emptyList(),
        channelName = "Root"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.LightTheme)
        super.onCreate(savedInstanceState)
        // when set to false, ui draw overrides the system ui border
        WindowCompat.setDecorFitsSystemWindows(window, true)

        database = Room.databaseBuilder(
            this@MainActivity,
            AppDatabase::class.java, "whisper.db"
        ).allowMainThreadQueries().build()

        whisperServiceManager = WhisperServiceManager(
            context = this,
            database = database,
            onPermissionRequest = {
                requestPermission(it)
            },
            messageViewModel = messagingViewModel,
            volumeControlViewModel = volumeControlViewModel
        )

        val firstRun: Boolean = PreferencesManager.getInstance(this).isFirstRun

        lifecycleScope.launch {
            requestPermissions(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            WhisperTheme {
                var showGenCertDialog by remember { mutableStateOf(firstRun) }
                val coroutineScope = rememberCoroutineScope()
                GeneralAlertDialog(
                    title = this@MainActivity.getString(R.string.oobe_cert_gen_title),
                    text = this@MainActivity.getString(R.string.oobe_cert_gen_desc),
                    showDialog = showGenCertDialog,
                    onUserDismiss = {
                        coroutineScope.launch {
                            val handle = generateCertificate(this@MainActivity, database)
                            showGenCertDialog = false
                            PreferencesManager.getInstance(this@MainActivity).isFirstRun = false
                            PreferencesManager.getInstance(this@MainActivity).defaultCertId = handle.id
                        }
                    },
                )

                HomeScreen(
                    serverListViewModel = ServerListViewModel(emptyList(), database),
                    bottomControlViewModel = BottomControlViewModel(),
                    whisperServiceManager = whisperServiceManager,
                    messagingPageViewModel = messagingViewModel,
                    volumeControlViewModel = volumeControlViewModel,
                    database = database
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        whisperServiceManager.unbindService()
    }

    override fun onResume() {
        super.onResume()
        whisperServiceManager.bindService()
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }

    suspend fun requestPermissions(vararg permissions: String): Map<String, Boolean> {
        multiplePermissionRequestDeferred?.await()
        multiplePermissionRequestDeferred = CompletableDeferred()
        multiplePermissionRequestLauncher.launch(permissions.asList().toTypedArray())
        return multiplePermissionRequestDeferred!!.await()
    }

    suspend fun requestPermission(permission: String): Boolean {
        singlePermissionRequestDeferred?.await()
        singlePermissionRequestDeferred = CompletableDeferred()
        singlePermissionRequestLauncher.launch(permission)
        return singlePermissionRequestDeferred!!.await()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WhisperTheme {
        // HomeScreen(serverList = getFakeServerList(), null)
    }
}
