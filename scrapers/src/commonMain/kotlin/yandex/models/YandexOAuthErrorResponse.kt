package yandex.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexOAuthErrorResponse(
    @SerialName("error") val error: String,
    @SerialName("error_description") val errorDescription: String,
)