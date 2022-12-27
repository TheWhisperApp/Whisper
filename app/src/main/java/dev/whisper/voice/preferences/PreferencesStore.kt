package dev.whisper.voice.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import dev.whisper.core.Constants
import dev.whisper.voice.R
import dev.whisper.voice.db.DatabaseCertificate
//import dev.whisper.voice.db.WhisperSQLite
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.io.*
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException

@Deprecated("Let me deal with this later")
class PreferencesStore private constructor(ctx: Context) {
    private val preferences: SharedPreferences

    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx)

        // TODO(acomminos): Settings migration infra.
        if (preferences.contains(PREF_CERT_DEPRECATED)) {
            // Perform legacy certificate migration into MumlaSQLiteDatabase.
//            Toast.makeText(ctx, R.string.migration_certificate_begin, Toast.LENGTH_LONG).show()
            val certPath: String? = preferences.getString(PREF_CERT_DEPRECATED, "")
            val certPassword: String? = preferences.getString(PREF_CERT_PASSWORD_DEPRECATED, "")
            Log.d(TAG, "Migrating certificate from $certPath")
            try {
                val certFile: File = File(certPath)
                val certInput: FileInputStream = FileInputStream(certFile)
                val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
                val oldStore: KeyStore = KeyStore.getInstance("PKCS12", BouncyCastleProvider())
                oldStore.load(certInput, certPassword!!.toCharArray())
                oldStore.store(outputStream, CharArray(0))
//                val database: WhisperSQLite = WhisperSQLite(ctx)
//                val certificate: DatabaseCertificate =
//                    database.addCertificate(certFile.name, outputStream.toByteArray())
//                database.close()
//                setDefaultCertificateId(certificate.id)
                Toast.makeText(ctx, R.string.certificate_migration_success, Toast.LENGTH_LONG)
                    .show()
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                // We can safely ignore this; the only case in which we might still want to recover
                // would be if the user's external storage is removed.
            } catch (e: CertificateException) {
                // Likely caused due to stored password being incorrect.
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                preferences.edit()
                    .remove(PREF_CERT_DEPRECATED)
                    .remove(PREF_CERT_PASSWORD_DEPRECATED)
                    .apply()
            }
        }
    }

    // Set default method for users who used to use handset mode before removal.
    val inputMethod: String?
        get() {
            var method: String? = preferences.getString(PREF_INPUT_METHOD, ARRAY_INPUT_METHOD_VOICE)
            if (!ARRAY_INPUT_METHODS!!.contains(method)) {
                // Set default method for users who used to use handset mode before removal.
                method = ARRAY_INPUT_METHOD_VOICE
            }
            return method
        }

    /**
     * Converts the preference input method value to the one used to connect to a server via Humla.
     * @return An input method value used to instantiate a Humla service.
     */
    val humlaInputMethod: Int
        get() {
            val inputMethod: String? = inputMethod
            if ((ARRAY_INPUT_METHOD_VOICE == inputMethod)) {
                return Constants.TRANSMIT_VOICE_ACTIVITY
            } else if ((ARRAY_INPUT_METHOD_PTT == inputMethod)) {
                return Constants.TRANSMIT_PUSH_TO_TALK
            } else if ((ARRAY_INPUT_METHOD_CONTINUOUS == inputMethod)) {
                return Constants.TRANSMIT_CONTINUOUS
            }
            throw RuntimeException("Could not convert input method '$inputMethod' to a Humla input method id!")
        }

    fun setInputMethod(inputMethod: String) {
        if (((ARRAY_INPUT_METHOD_VOICE == inputMethod) || (ARRAY_INPUT_METHOD_PTT == inputMethod) || (ARRAY_INPUT_METHOD_CONTINUOUS == inputMethod))) {
            preferences.edit().putString(PREF_INPUT_METHOD, inputMethod).apply()
        } else {
            throw RuntimeException("Invalid input method $inputMethod")
        }
    }

    val inputSampleRate: Int
        get() = preferences.getString(PREF_INPUT_RATE, DEFAULT_RATE)!!
            .toInt()
    val inputQuality: Int
        get() {
            return preferences.getInt(PREF_INPUT_QUALITY, DEFAULT_INPUT_QUALITY)
        }
    val amplitudeBoostMultiplier: Float
        get() {
            return preferences.getInt(PREF_AMPLITUDE_BOOST, DEFAULT_AMPLITUDE_BOOST).toFloat() / 100
        }
    val detectionThreshold: Float
        get() {
            return preferences.getInt(PREF_THRESHOLD, DEFAULT_THRESHOLD).toFloat() / 100
        }
    val pushToTalkKey: Int
        get() {
            return preferences.getInt(PREF_PUSH_KEY, DEFAULT_PUSH_KEY)
        }
    val hotCorner: String?
        get() {
            return preferences.getString(PREF_HOT_CORNER_KEY, DEFAULT_HOT_CORNER)
        }

    /**
     * Returns whether or not the hot corner is enabled.
     * @return true if a hot corner should be shown.
     */
    val isHotCornerEnabled: Boolean
        get() {
            return !(ARRAY_HOT_CORNER_NONE == preferences.getString(
                PREF_HOT_CORNER_KEY,
                DEFAULT_HOT_CORNER
            ))
        }

    /**
     * Returns the view gravity of the hot corner, or 0 if hot corner is disabled.
     * @return A [android.view.Gravity] value, or 0 if disabled.
     */
    val hotCornerGravity: Int
        get() {
            val hc: String? = hotCorner
            if ((ARRAY_HOT_CORNER_BOTTOM_LEFT == hc)) {
                return Gravity.LEFT or Gravity.BOTTOM
            } else if ((ARRAY_HOT_CORNER_BOTTOM_RIGHT == hc)) {
                return Gravity.RIGHT or Gravity.BOTTOM
            } else if ((ARRAY_HOT_CORNER_TOP_LEFT == hc)) {
                return Gravity.LEFT or Gravity.TOP
            } else if ((ARRAY_HOT_CORNER_TOP_RIGHT == hc)) {
                return Gravity.RIGHT or Gravity.TOP
            }
            return 0
        }

    /**
     * @return the resource ID of the user-defined theme.
     */
//    val theme: Int
//        get() {
//            val theme: String? = preferences.getString(PREF_THEME, ARRAY_THEME_LIGHT)
//            if ((ARRAY_THEME_LIGHT == theme)) return R.style.Theme_Mumla else if ((ARRAY_THEME_DARK == theme)) return R.style.Theme_Mumla_Dark else if ((ARRAY_THEME_SOLARIZED_LIGHT == theme)) return R.style.Theme_Mumla_Solarized_Light else if ((ARRAY_THEME_SOLARIZED_DARK == theme)) return R.style.Theme_Mumla_Solarized_Dark
//            return -1
//        }

    /* @return the height of PTT button */
    val pTTButtonHeight: Int
        get() {
            return preferences.getInt(PREF_PTT_BUTTON_HEIGHT, DEFAULT_PTT_BUTTON_HEIGHT)
        }

    /**
     * Returns a database identifier for the default certificate, or a negative number if there is
     * no default certificate set.
     * @return The default certificate's ID, or a negative integer if not set.
     */
    val defaultCertificate: Long
        get() {
            return preferences.getLong(PREF_CERT_ID, -1)
        }
    val defaultUsername: String?
        get() {
            return preferences.getString(PREF_DEFAULT_USERNAME, DEFAULT_DEFAULT_USERNAME)
        }
    val isPushToTalkToggle: Boolean
        get() {
            return preferences.getBoolean(PREF_PTT_TOGGLE, DEFAULT_PTT_TOGGLE)
        }
    val isPushToTalkButtonShown: Boolean
        get() {
            return !preferences.getBoolean(PREF_PUSH_BUTTON_HIDE_KEY, DEFAULT_PUSH_BUTTON_HIDE)
        }
    val isChatNotifyEnabled: Boolean
        get() {
            return preferences.getBoolean(PREF_CHAT_NOTIFY, DEFAULT_CHAT_NOTIFY)
        }
    val isTextToSpeechEnabled: Boolean
        get() {
            return preferences.getBoolean(PREF_USE_TTS, DEFAULT_USE_TTS)
        }
    val isShortTextToSpeechMessagesEnabled: Boolean
        get() {
            return preferences.getBoolean(PREF_SHORT_TTS_MESSAGES, DEFAULT_SHORT_TTS_MESSAGES)
        }
    val isAutoReconnectEnabled: Boolean
        get() {
            return preferences.getBoolean(PREF_AUTO_RECONNECT, DEFAULT_AUTO_RECONNECT)
        }
    val isTcpForced: Boolean
        get() {
            return preferences.getBoolean(PREF_FORCE_TCP, DEFAULT_FORCE_TCP)
        }
    val isOpusDisabled: Boolean
        get() {
            return preferences.getBoolean(PREF_DISABLE_OPUS, DEFAULT_DISABLE_OPUS)
        }
    val isTorEnabled: Boolean
        get() {
            return preferences.getBoolean(PREF_USE_TOR, DEFAULT_USE_TOR)
        }

    fun disableTor() {
        preferences.edit().putBoolean(PREF_USE_TOR, false).apply()
    }

    val isMuted: Boolean
        get() {
            return preferences.getBoolean(PREF_MUTED, DEFAULT_MUTED)
        }
    val isDeafened: Boolean
        get() {
            return preferences.getBoolean(PREF_DEAFENED, DEFAULT_DEAFENED)
        }
    var isFirstRun: Boolean
        get() {
            return preferences.getBoolean(PREF_FIRST_RUN, DEFAULT_FIRST_RUN)
        }
        set(run) {
            preferences.edit().putBoolean(PREF_FIRST_RUN, run).apply()
        }

    fun shouldLoadExternalImages(): Boolean {
        return preferences.getBoolean(PREF_LOAD_IMAGES, DEFAULT_LOAD_IMAGES)
    }

    fun setMutedAndDeafened(muted: Boolean, deafened: Boolean) {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean(PREF_MUTED, muted || deafened)
        editor.putBoolean(PREF_DEAFENED, deafened)
        editor.apply()
    }

    val framesPerPacket: Int
        get() {
            return preferences.getString(PREF_FRAMES_PER_PACKET, DEFAULT_FRAMES_PER_PACKET)!!
                .toInt()
        }
    val isHalfDuplex: Boolean
        get() {
            return preferences.getBoolean(PREF_HALF_DUPLEX, DEFAULT_HALF_DUPLEX)
        }
    val isHandsetMode: Boolean
        get() {
            return preferences.getBoolean(PREF_HANDSET_MODE, DEFAULT_HANDSET_MODE)
        }
    val isPttSoundEnabled: Boolean
        get() {
            return preferences.getBoolean(PREF_PTT_SOUND, DEFAULT_PTT_SOUND)
        }
    val isPreprocessorEnabled: Boolean
        get() {
            return preferences.getBoolean(PREF_PREPROCESSOR_ENABLED, DEFAULT_PREPROCESSOR_ENABLED)
        }

    fun shouldStayAwake(): Boolean {
        return preferences.getBoolean(PREF_STAY_AWAKE, DEFAULT_STAY_AWAKE)
    }

    fun setDefaultCertificateId(defaultCertificateId: Long) {
        preferences.edit().putLong(PREF_CERT_ID, defaultCertificateId).apply()
    }

    fun disableCertificate() {
        preferences.edit().putLong(PREF_CERT_ID, -1).apply()
    }

    val isUsingCertificate: Boolean
        get() {
            return defaultCertificate >= 0
        }

    /**
     * @return true if the user count should be shown next to channels.
     */
    fun shouldShowUserCount(): Boolean {
        return preferences.getBoolean(PREF_SHOW_USER_COUNT, DEFAULT_SHOW_USER_COUNT)
    }

    fun shouldStartUpInPinnedMode(): Boolean {
        return preferences.getBoolean(PREF_START_UP_IN_PINNED_MODE, DEFAULT_START_UP_IN_PINNED_MODE)
    }

    companion object {
        private val TAG: String = PreferencesStore::class.java.name
        val PREF_INPUT_METHOD: String = "audioInputMethod"
        var ARRAY_INPUT_METHODS: MutableSet<String?>? = null

        /** Voice activity transmits depending on the amplitude of user input.  */
        val ARRAY_INPUT_METHOD_VOICE: String = "voiceActivity"

        /** Push to talk transmits on command.  */
        val ARRAY_INPUT_METHOD_PTT: String = "ptt"

        /** Continuous transmits always.  */
        val ARRAY_INPUT_METHOD_CONTINUOUS: String = "continuous"

        // NOTE: When changing DEFAULTs, the default value in the corresponding
        // widget in settings_PAGE.xml must also be changed. It doesn't pick this
        // up itself...
        val PREF_THRESHOLD: String = "vadThreshold"
        val DEFAULT_THRESHOLD: Int = 50
        val PREF_PUSH_KEY: String = "talkKey"
        val DEFAULT_PUSH_KEY: Int = -1
        val PREF_HOT_CORNER_KEY: String = "hotCorner"
        val ARRAY_HOT_CORNER_NONE: String = "none"
        val ARRAY_HOT_CORNER_TOP_LEFT: String = "topLeft"
        val ARRAY_HOT_CORNER_BOTTOM_LEFT: String = "bottomLeft"
        val ARRAY_HOT_CORNER_TOP_RIGHT: String = "topRight"
        val ARRAY_HOT_CORNER_BOTTOM_RIGHT: String = "bottomRight"
        val DEFAULT_HOT_CORNER: String = ARRAY_HOT_CORNER_NONE
        val PREF_PUSH_BUTTON_HIDE_KEY: String = "hidePtt"
        val DEFAULT_PUSH_BUTTON_HIDE: Boolean = false
        val PREF_PTT_TOGGLE: String = "togglePtt"
        val DEFAULT_PTT_TOGGLE: Boolean = false
        val PREF_INPUT_RATE: String = "input_quality"
        val DEFAULT_RATE: String = "48000"
        val PREF_INPUT_QUALITY: String = "input_bitrate"
        val DEFAULT_INPUT_QUALITY: Int = 40000
        val PREF_AMPLITUDE_BOOST: String = "inputVolume"
        val DEFAULT_AMPLITUDE_BOOST: Int = 100
        val PREF_CHAT_NOTIFY: String = "chatNotify"
        val DEFAULT_CHAT_NOTIFY: Boolean = true
        val PREF_USE_TTS: String = "useTts"
        val DEFAULT_USE_TTS: Boolean = true
        val PREF_SHORT_TTS_MESSAGES: String = "shortTtsMessages"
        val DEFAULT_SHORT_TTS_MESSAGES: Boolean = false
        val PREF_AUTO_RECONNECT: String = "autoReconnect"
        val DEFAULT_AUTO_RECONNECT: Boolean = true
        val PREF_THEME: String = "theme"
        val ARRAY_THEME_LIGHT: String = "lightDark"
        val ARRAY_THEME_DARK: String = "dark"
        val ARRAY_THEME_SOLARIZED_LIGHT: String = "solarizedLight"
        val ARRAY_THEME_SOLARIZED_DARK: String = "solarizedDark"
        val PREF_PTT_BUTTON_HEIGHT: String = "pttButtonHeight"
        val DEFAULT_PTT_BUTTON_HEIGHT: Int = 150

        @Deprecated("use {@link #PREF_CERT_ID } ")
        val PREF_CERT_DEPRECATED: String = "certificatePath"

        @Deprecated("use {@link #PREF_CERT_ID } ")
        val PREF_CERT_PASSWORD_DEPRECATED: String = "certificatePassword"

        /**
         * The DB identifier for the default certificate.
         * @see se.lublin.mumla.db.DatabaseCertificate
         */
        val PREF_CERT_ID: String = "certificateId"
        val PREF_DEFAULT_USERNAME: String = "defaultUsername"
        val DEFAULT_DEFAULT_USERNAME: String = "Mumla_User" // funny var name
        val PREF_FORCE_TCP: String = "forceTcp"
        val DEFAULT_FORCE_TCP: Boolean = false
        val PREF_USE_TOR: String = "useTor"
        val DEFAULT_USE_TOR: Boolean = false
        val PREF_DISABLE_OPUS: String = "disableOpus"
        val DEFAULT_DISABLE_OPUS: Boolean = false
        val PREF_MUTED: String = "muted"
        val DEFAULT_MUTED: Boolean = false
        val PREF_DEAFENED: String = "deafened"
        val DEFAULT_DEAFENED: Boolean = false
        val PREF_FIRST_RUN: String = "firstRun"
        val DEFAULT_FIRST_RUN: Boolean = true
        val PREF_LOAD_IMAGES: String = "load_images"
        val DEFAULT_LOAD_IMAGES: Boolean = true
        val PREF_FRAMES_PER_PACKET: String = "audio_per_packet"
        val DEFAULT_FRAMES_PER_PACKET: String = "2"
        val PREF_HALF_DUPLEX: String = "half_duplex"
        val DEFAULT_HALF_DUPLEX: Boolean = false
        val PREF_HANDSET_MODE: String = "handset_mode"
        val DEFAULT_HANDSET_MODE: Boolean = false
        val PREF_PTT_SOUND: String = "ptt_sound"
        val DEFAULT_PTT_SOUND: Boolean = false
        val PREF_PREPROCESSOR_ENABLED: String = "preprocessor_enabled"
        val DEFAULT_PREPROCESSOR_ENABLED: Boolean = true
        val PREF_STAY_AWAKE: String = "stay_awake"
        val DEFAULT_STAY_AWAKE: Boolean = false
        val PREF_SHOW_USER_COUNT: String = "show_user_count"
        val DEFAULT_SHOW_USER_COUNT: Boolean = false
        val PREF_START_UP_IN_PINNED_MODE: String = "startUpInPinnedMode"
        val DEFAULT_START_UP_IN_PINNED_MODE: Boolean = false

        init {
            ARRAY_INPUT_METHODS = HashSet()
            (ARRAY_INPUT_METHODS as HashSet<String?>).add(ARRAY_INPUT_METHOD_VOICE)
            (ARRAY_INPUT_METHODS as HashSet<String?>).add(ARRAY_INPUT_METHOD_PTT)
            (ARRAY_INPUT_METHODS as HashSet<String?>).add(ARRAY_INPUT_METHOD_CONTINUOUS)
        }

        fun getInstance(context: Context): PreferencesStore {
            return PreferencesStore(context)
        }
    }
}
