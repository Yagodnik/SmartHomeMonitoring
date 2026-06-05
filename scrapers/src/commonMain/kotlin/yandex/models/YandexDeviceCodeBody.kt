package yandex.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexDeviceCodeBody(
    @SerialName("device_code") val deviceCode: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("interval") val interval: Int,
    @SerialName("user_code") val userCode: String,
    @SerialName("verification_url") val verificationUrl: String,
)
