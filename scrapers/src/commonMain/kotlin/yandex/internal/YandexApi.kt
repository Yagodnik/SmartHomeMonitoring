package yandex.internal

import yandex.models.YandexAccountInfo
import yandex.models.YandexUserInfo

interface YandexApi {
    suspend fun requestOauthToken(): String?

    suspend fun queryUserInfo() : Result<YandexUserInfo>

    suspend fun getAccountInfo(): Result<YandexAccountInfo>
}