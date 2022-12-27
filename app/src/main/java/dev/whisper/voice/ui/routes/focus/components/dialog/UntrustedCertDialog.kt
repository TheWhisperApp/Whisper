package dev.whisper.voice.ui.routes.focus.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import dev.whisper.voice.ui.theme.Icons
import org.spongycastle.util.encoders.Hex
import java.security.MessageDigest
import java.security.cert.X509Certificate

@Composable
fun UntrustedCertDialog(
    showDialog: Boolean,
    certChain: Array<X509Certificate>,
    onUserCancel: () -> Unit,
    onUserAccept: (cert: X509Certificate) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Text(
                    text = "Untrusted Certificate"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUserAccept(certChain[0])
                }) {
                    Text(text = "Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = onUserCancel) {
                    Text(text = "Cancel")
                }
            },
            icon = {
                Icon(
                    Icons.Warning,
                    //TODO: i18n
                    contentDescription = "Warning"
                )
            },
            text = {
                assert(certChain.isNotEmpty())
                Column {
                    Text(text = "Subject:")
                    Text(
                        text = certChain[0].subjectDN.toString(),
                        style = TextStyle(fontFamily = FontFamily.Monospace)
                    )
                    Text(text = "Valid from:")
                    Text(
                        text = certChain[0].notBefore.toString(),
                        style = TextStyle(fontFamily = FontFamily.Monospace)
                    )
                    Text(text = "Valid to:")
                    Text(
                        text = certChain[0].notAfter.toString(),
                        style = TextStyle(fontFamily = FontFamily.Monospace)
                    )
                    Text(text = "Digest (SHA-1)")
                    val digestSha1 = MessageDigest.getInstance("SHA-1")
                    Text(
                        text = String(Hex.encode(digestSha1.digest(certChain[0].encoded))),
                        style = TextStyle(fontFamily = FontFamily.Monospace)
                    )
                    Text(text = "Digest (SHA-256)")
                    val digestSha256 = MessageDigest.getInstance("SHA-256")
                    Text(
                        text = String(Hex.encode(digestSha256.digest(certChain[0].encoded))),
                        style = TextStyle(fontFamily = FontFamily.Monospace)
                    )
                }
            }
        )
    }
}

//@Preview(showSystemUi = true)
//@Composable
//fun UntrustedCertDialogPreview() {
//    val byteStream = ByteArrayOutputStream()
//    val cert by remember { HumlaCertificateGenerator.generateCertificate(byteStream) }
//
//    WhisperTheme {
//        UntrustedCertDialog(openDialog = true, certChain = arrayOf(cert))
//    }
//}