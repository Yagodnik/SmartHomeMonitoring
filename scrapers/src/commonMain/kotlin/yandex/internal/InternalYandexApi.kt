package yandex.internal

import models.OAuth2Token
import models.ResultOrError
import yandex.models.YandexAccountInfo
import yandex.models.YandexDeviceCodeBody
import yandex.models.YandexError
import yandex.models.YandexUserInfo

interface InternalYandexApi {
    suspend fun requestCode(): ResultOrError<YandexDeviceCodeBody, YandexError>

    suspend fun exchangeForOAuthToken(deviceCodeDto: YandexDeviceCodeBody): ResultOrError<OAuth2Token, YandexError>

    suspend fun queryUserInfo() : ResultOrError<YandexUserInfo, YandexError>

    suspend fun getAccountInfo(): ResultOrError<YandexAccountInfo, YandexError>
}