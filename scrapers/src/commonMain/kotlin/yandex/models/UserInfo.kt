package yandex.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class YandexStatus {
    @SerialName("ok") OK,
    @SerialName("error") ERROR,
}

@Serializable
data class YandexUserInfo(
    val status: YandexStatus,
    @SerialName("request_id") val requestId: String,
    val devices: List<YandexDevice>
)
