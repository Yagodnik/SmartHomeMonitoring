package secrets

import SecretsStorage
import kotlinx.coroutines.flow.takeWhile

class DefaultSecretsStorage : SecretsStorage {
    private val ksafe = createKSafe()

    override fun saveSecret(key: String, value: String) {
        ksafe.putDirect(key, value)
    }

    override suspend fun getSecret(key: String): String? {
        val myFlow = ksafe.getFlow<String?>(key, null)

        var keepLooping = true

        myFlow
            .takeWhile { keepLooping }
            .collect { value ->
                if (value != null) {
                    keepLooping = false
                    println("Found non-null value: $value. Stopping loop.")
                } else {
                    println("Value is null, loop continues...")
                }
            }

        return ksafe.get<String?>(key, null)
    }
}