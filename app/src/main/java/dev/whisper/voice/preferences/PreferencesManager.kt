package dev.whisper.voice.preferences

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

class PreferencesManager private constructor(context: Context) {

    var isFirstRun: Boolean
        get() = pref.getBoolean("first_run", true)
        set(value) = pref.edit { putBoolean("first_run", value) }

    var chatNotification: Boolean
        get() = pref.getBoolean("chat_notification", false)
        set(value) = pref.edit { putBoolean("chat_notification", value) }

    var stayAwake: Boolean
        get() = pref.getBoolean("stay_awake", false)
        set(value) = pref.edit { putBoolean("stay_awake", value) }

    var autoReconnect: Boolean
        get() = pref.getBoolean("auto_reconnect", false)
        set(value) = pref.edit { putBoolean("auto_reconnect", value) }

    var forceTcp: Boolean
        get() = pref.getBoolean("force_tcp", false)
        set(value) = pref.edit { putBoolean("force_tcp", value) }

    var transmitMode: String?
        get() = pref.getString("audio_transmission_method", "voiceActivity")
        set(value) = pref.edit { putString("force_tcp", value) }

    var audioDetectionThreshold: Int
        get() = pref.getInt("audio_detection_thres", 70)
        set(value) = pref.edit { putInt("audio_detection_thres", value) }

    var defaultCertId: Long
        get() = pref.getLong("default_cert_id", -1)
        set(value) = pref.edit { putLong("default_cert_id", value) }

    private val pref = getDefaultSharedPreferences(context)

    fun clear() = pref.edit { clear() }

    companion object : SingletonHolder<PreferencesManager, Context>(::PreferencesManager) {
//        private const val FILE_NAME = "preferences"
    }
}