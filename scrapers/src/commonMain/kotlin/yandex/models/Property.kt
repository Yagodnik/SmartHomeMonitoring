package yandex.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
enum class YandexPropertyType {
    @SerialName("devices.properties.float") FLOAT,
    @SerialName("devices.properties.event") EVENT,
}

@Serializable
data class YandexProperty(
    val type: YandexPropertyType,
    val state: JsonObject
)
