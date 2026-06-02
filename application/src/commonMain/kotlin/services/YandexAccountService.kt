package services

import SecretsStorage
import models.Account
import models.AuthData
import models.OAuth2Token
import models.ResultOrError
import yandex.internal.InternalYandexApi
import yandex.models.YandexAccountInfo
import yandex.models.YandexDeviceCodeBody
import yandex.models.YandexError

class YandexAccountService(
    private val internalApi: InternalYandexApi,
    private val secretsStorage: SecretsStorage,
) : AccountService {
    class YandexAuthSession(
        private val internalApi: InternalYandexApi
    ) : AuthSession {
        private var dto: ResultOrError<YandexDeviceCodeBody, YandexError>? = null

        override suspend fun requestUserCode(): AuthData? {
            dto = internalApi.requestCode()

            return dto?.let {
                when (it) {
                    is ResultOrError.Success<YandexDeviceCodeBody> -> {
                        AuthData(it.data.userCode, it.data.verificationUrl)
                    }
                    is ResultOrError.Error<YandexError> -> null
                }
            }
        }

        override suspend fun exchangeForToken(): OAuth2Token? {
            return dto?.let {
                if (it is ResultOrError.Success<YandexDeviceCodeBody>) {
                    when (val result = internalApi.exchangeForOAuthToken(it.data)) {
                        is ResultOrError.Success<OAuth2Token> -> result.data
                        is ResultOrError.Error<YandexError> -> null
                    }
                }

                null
            }
        }
    }

    override suspend fun getAccount(): Account {
        return when (val result = internalApi.getAccountInfo()) {
            is ResultOrError.Success<YandexAccountInfo> -> {
                Account(result.data.displayName, result.data.defaultEmail)
            }
            is ResultOrError.Error<YandexError> -> {
                Account("", "")
            }
        }
    }

    override fun createAuthSession(): AuthSession = YandexAuthSession(internalApi)

    override fun saveOAuthToken(tokens: OAuth2Token) {
        secretsStorage.saveSecret("YANDEX_ACCESS_TOKEN", tokens.accessToken)
        secretsStorage.saveSecret("YANDEX_REFRESH_TOKEN", tokens.refreshToken)
    }
}