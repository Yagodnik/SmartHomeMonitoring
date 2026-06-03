package secrets

import SecretsStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class DefaultSecretsStorage : SecretsStorage {
    private val ksafe = createKSafe()

    override fun waitUntilReady(): Boolean = true

    override fun saveSecret(key: String, value: String) {
        ksafe.putDirect(key, value)
    }

    override fun getSecret(key: String): String? {
        return ksafe.getDirect<String?>(key, null)
    }
}