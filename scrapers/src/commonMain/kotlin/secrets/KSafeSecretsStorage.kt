package secrets

import SecretsStorage

class KSafeSecretsStorage : SecretsStorage {
    private val ksafe = createKSafe()

    override fun waitUntilReady(): Boolean = true

    override fun saveSecret(key: String, value: String) {
        ksafe.putDirect(key, value)
    }

    override fun getSecret(key: String): String? {
        return ksafe.getDirect<String?>(key, null)
    }
}