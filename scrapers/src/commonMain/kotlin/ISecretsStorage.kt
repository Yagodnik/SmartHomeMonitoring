interface ISecretsStorage {
    fun saveSecret(key: String, value: String)

    fun getSecret(key: String): String?
}