interface SecretsStorage {
    fun waitUntilReady(): Boolean

    fun saveSecret(key: String, value: String)

    fun getSecret(key: String): String?
}