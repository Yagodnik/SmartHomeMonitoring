package yandex.internal

import models.OAuth2Token
import models.ResultOrError
import yandex.models.*

interface InternalYandexApi {
    suspend fun generateAuthUrl(): YandexAuthData

    suspend fun exchangeForOAuthToken(
        code: String,
        dto: YandexAuthData
    ): ResultOrError<OAuth2Token, YandexError>

    suspend fun queryUserInfo() : ResultOrError<YandexUserInfo, YandexError>

    suspend fun getAccountInfo(): ResultOrError<YandexAccountInfo, YandexError>

    fun close()
}