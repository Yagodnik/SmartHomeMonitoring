package yandex.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexTokenBody(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
)
