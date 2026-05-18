package yandex.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
enum class YandexCapabilityType {
    @SerialName("devices.capabilities.on_off") ON_OFF,
    @SerialName("devices.capabilities.color_setting") COLOR_SETTING,
    @SerialName("devices.capabilities.video_stream") VIDEO_STREAM,
    @SerialName("devices.capabilities.mode") MODE,
    @SerialName("devices.capabilities.range") RANGE,
    @SerialName("devices.capabilities.toggle") TOGGLE,
}

@Serializable
data class YandexCapability(
    val type: YandexCapabilityType,
    val state: JsonObject
)
