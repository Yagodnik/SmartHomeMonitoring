package services

import SecretsStorage
import models.Account
import models.AuthData
import models.OAuth2Token
import models.ResultOrError
import yandex.internal.InternalYandexApi
import yandex.models.YandexAccountInfo
import yandex.models.YandexAuthData
import yandex.models.YandexDeviceCodeBody
import yandex.models.YandexError

class YandexAccountService(
    private val internalApi: InternalYandexApi,
    private val secretsStorage: SecretsStorage,
) : AccountService {
    class YandexAuthSession(
        private val internalApi: InternalYandexApi
    ) : AuthSession {
        private var dto: YandexAuthData? = null

        override suspend fun requestAuthUrl(): String? {
            dto = internalApi.generateAuthUrl()

            return dto?.url
        }

        override suspend fun exchangeForToken(userCode: String): OAuth2Token? {
            return dto?.let {
                when (val result = internalApi.exchangeForOAuthToken(userCode, it)) {
                    is ResultOrError.Success<OAuth2Token> -> result.data
                    is ResultOrError.Error<YandexError> -> {
                        println("Error: ${result.error.error} ${result.error.errorDescription}")
                        null
                    }
                }
            }
        }
    }

    override suspend fun getAccount(): Account {
        return when (val result = internalApi.getAccountInfo()) {
            is ResultOrError.Success<YandexAccountInfo> -> {
                Account(result.data.displayName, result.data.defaultEmail)
            }
            is ResultOrError.Error<YandexError> -> {
                Account(result.error.error, result.error.errorDescription)
            }
        }
    }

    override fun createAuthSession(): AuthSession = YandexAuthSession(internalApi)

    override fun saveOAuthToken(tokens: OAuth2Token) {
        secretsStorage.saveSecret("YANDEX_ACCESS_TOKEN", tokens.accessToken)
        secretsStorage.saveSecret("YANDEX_REFRESH_TOKEN", tokens.refreshToken)
    }

    override fun deleteAuthData() {
        secretsStorage.saveSecret("YANDEX_ACCESS_TOKEN", "")
        secretsStorage.saveSecret("YANDEX_REFRESH_TOKEN", "")
    }
}