package yandex.internal

import yandex.models.YandexUserInfo

interface YandexApi {
    suspend fun requestOauthToken(): String?

    suspend fun queryUserInfo() : Result<YandexUserInfo>
}