interface SecretsStorage {
    fun saveSecret(key: String, value: String)

    suspend fun getSecret(key: String): String?
}