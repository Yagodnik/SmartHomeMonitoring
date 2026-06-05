package yandex.models

import kotlinx.serialization.Serializable

@Serializable
data class YandexDevice(
    val id: String,
    val name: String,
    val type: String,
    val capabilities: List<YandexCapability>,
    val properties: List<YandexProperty>,
)
