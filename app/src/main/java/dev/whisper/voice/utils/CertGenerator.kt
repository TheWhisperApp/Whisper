package dev.whisper.voice.utils

import android.content.Context
import dev.whisper.core.net.HumlaCertificateGenerator
import dev.whisper.voice.R
import dev.whisper.voice.db.*
import kotlinx.coroutines.runInterruptible
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss"

suspend fun generateCertificate(context: Context, database: AppDatabase): DatabaseCertificate {
    return runInterruptible {
        val byteStream = ByteArrayOutputStream()
        HumlaCertificateGenerator.generateCertificate(byteStream)
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val fileName =
            context.getString(R.string.certificate_export_prefix, dateFormat.format(Date()))
        val certId = database.certificateDao().addCertificate(Certificate(
            name = fileName,
            data = byteStream.toByteArray()
        ))
        DatabaseCertificate(id = certId, name = fileName)
    }
}