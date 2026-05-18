package yandex.api

import yandex.models.YandexUserInfo

interface YandexApi {
    suspend fun queryUserInfo() : Result<YandexUserInfo>
}