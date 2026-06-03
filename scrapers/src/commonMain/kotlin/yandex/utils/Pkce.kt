package yandex.utils

import org.kotlincrypto.SecureRandom
import org.kotlincrypto.hash.sha2.SHA256
import kotlin.io.encoding.Base64

fun generatePkce(): Pair<String, String> {
    val bytes = ByteArray(32)
    SecureRandom().nextBytesCopyTo(bytes)

    val encoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
    val verifier = encoder.encode(bytes)

    val verifierBytes = verifier.encodeToByteArray()
    val hash = SHA256().digest(verifierBytes)

    val challenge = encoder.encode(hash)

    return Pair(verifier, challenge)
}