package app

data class Configuration(
    val credentialsDir: String,
    val masterKey: String,
    val yandexClientId: String,
) {
    companion object {
        const val DEFAULT_CREDENTIALS_DIR = "/credentials"
    }
}
