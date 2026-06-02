package yandex.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexError(
    @SerialName("error_description") val errorDescription: String,
    @SerialName("error") val error: String,
)
