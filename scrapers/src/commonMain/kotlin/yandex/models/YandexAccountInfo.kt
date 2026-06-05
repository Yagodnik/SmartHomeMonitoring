package yandex.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YandexAccountInfo(
    @SerialName("login") val login: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("default_avatar_id") val defaultAvatarId: String?,
    @SerialName("default_email") val defaultEmail: String,
)
