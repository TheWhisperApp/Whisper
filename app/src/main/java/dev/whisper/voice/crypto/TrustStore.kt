package dev.whisper.voice.crypto

import android.content.Context
import java.io.*
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException

object TrustStore {
    private const val STORE_FILE = "whisper-trust-store.bks"
    const val trustStorePassword = ""
    const val trustStoreFormat = "BKS"

    /**
     * Loads the app's trust store of certificates.
     * @return A loaded KeyStore with the user's trusted certificates.
     */
    @Throws(
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class,
        KeyStoreException::class
    )
    fun getTrustStore(context: Context): KeyStore {
        val store = KeyStore.getInstance(trustStoreFormat)
        try {
            val fis: FileInputStream = context.openFileInput(STORE_FILE)
            store.load(fis, trustStorePassword.toCharArray())
            fis.close()
        } catch (e: FileNotFoundException) {
            store.load(null, null)
        }
        return store
    }

    @Throws(
        IOException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class
    )
    fun saveTrustStore(context: Context, store: KeyStore) {
        val fos: FileOutputStream = context.openFileOutput(STORE_FILE, Context.MODE_PRIVATE)
        store.store(fos, trustStorePassword.toCharArray())
        fos.close()
    }

    fun clearTrustStore(context: Context) {
        context.deleteFile(STORE_FILE)
    }

    /**
     * Gets the app's trust store path.
     * @return null if the store has not yet been initialized, or the absolute path if it has.
     */
    fun getTrustStorePath(context: Context): String? {
        val trustPath = File(context.filesDir, STORE_FILE)
        return if (trustPath.exists()) trustPath.absolutePath else null
    }
}
