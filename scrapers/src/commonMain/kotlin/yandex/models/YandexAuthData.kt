package yandex.models

data class YandexAuthData(
    val url: String,
    val verifier: String,
    val challenge: String,
)
