package secrets

import SecretsStorage
import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import org.kotlincrypto.SecureRandom
import org.kotlincrypto.hash.sha2.SHA256

class FileSecretsStorage(
    credentialsDir: String,
    private val masterKey: String
) : SecretsStorage {
    private var configDirectoryPath = Path(credentialsDir)
    private val filePath = Path(credentialsDir, "secrets.json.enc")
    private val json = Json { ignoreUnknownKeys = true }

    private val cryptoProvider = CryptographyProvider.Default
    private val aesGcm = cryptoProvider.get(AES.GCM)
    private val secureRandom = SecureRandom()
    private val sha256 = SHA256()

    override fun waitUntilReady(): Boolean = true

    override fun saveSecret(key: String, value: String) {
        SystemFileSystem.createDirectories(configDirectoryPath, mustCreate = false)

        val currentSecrets = loadSecretsMap().toMutableMap()
        currentSecrets[key] = value

        val plaintext = json.encodeToString(currentSecrets)
        val bytesToWrite = encrypt(plaintext, masterKey)

        SystemFileSystem.sink(filePath).buffered().use { it.write(bytesToWrite) }
    }

    override fun getSecret(key: String): String? {
        return loadSecretsMap()[key]
    }

    private fun loadSecretsMap(): Map<String, String> {
        if (!SystemFileSystem.exists(filePath)) {
            return emptyMap()
        }

        val fileBytes = SystemFileSystem.source(filePath).buffered().use { it.readByteArray() }

        return try {
            val plaintext = if (fileBytes.size > 12) {
                decrypt(fileBytes, masterKey)
            } else {
                fileBytes.decodeToString()
            }
            json.decodeFromString<Map<String, String>>(plaintext)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun encrypt(plaintext: String, keyString: String): ByteArray {
        val keyBytes = sha256.digest(keyString.encodeToByteArray())
        val key = runBlocking { aesGcm.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, keyBytes) }
        val cipher = key.cipher()
        val nonce = ByteArray(12).apply { secureRandom.nextBytesCopyTo(this) }
        val encrypted = runBlocking { cipher.encrypt(plaintext.encodeToByteArray(), nonce) }

        return nonce + encrypted
    }

    private fun decrypt(fileBytes: ByteArray, keyString: String): String {
        val keyBytes = sha256.digest(keyString.encodeToByteArray())
        val key = runBlocking { aesGcm.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, keyBytes) }
        val cipher = key.cipher()
        val nonce = fileBytes.copyOfRange(0, 12)
        val ciphertext = fileBytes.copyOfRange(12, fileBytes.size)
        val decrypted = runBlocking { cipher.decrypt(ciphertext, nonce) }

        return decrypted.decodeToString()
    }
}