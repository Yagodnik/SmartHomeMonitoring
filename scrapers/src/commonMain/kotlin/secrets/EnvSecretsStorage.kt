package secrets

import SecretsStorage
import dev.scottpierce.envvar.EnvVar

class EnvSecretsStorage : SecretsStorage {
    private val data: MutableMap<String, String?> = mutableMapOf()

    override fun saveSecret(key: String, value: String) {
        data[key] = value
    }

    override suspend fun getSecret(key: String): String? {
        if (data[key] == null) {
            return EnvVar[key]?.let {
                saveSecret(key, it)
                return it
            }
        }

        return data[key]
    }
}