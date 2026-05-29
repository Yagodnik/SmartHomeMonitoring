package yandex.internal

import yandex.models.YandexAccountInfo
import yandex.models.YandexDeviceCodeBody
import yandex.models.YandexUserInfo

interface InternalYandexApi {
    suspend fun requestCode(): YandexDeviceCodeBody?

    suspend fun exchangeForOauthToken(deviceCodeDto: YandexDeviceCodeBody): String?

    suspend fun queryUserInfo() : Result<YandexUserInfo>

    suspend fun getAccountInfo(): Result<YandexAccountInfo>
}