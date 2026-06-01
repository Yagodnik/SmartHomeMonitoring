package services

import models.Account
import models.AuthData
import yandex.internal.InternalYandexApi
import yandex.models.YandexDeviceCodeBody

class YandexAccountService(
    private val internalApi: InternalYandexApi
) : AccountService {
    class YandexAuthSession(
        private val internalApi: InternalYandexApi
    ) : AuthSession {
        private var dto: YandexDeviceCodeBody? = null

        override suspend fun requestUserCode(): AuthData? {
            dto = internalApi.requestCode()

            return dto?.let {
                AuthData(it.userCode, it.verificationUrl)
            }
        }

        override suspend fun exchangeForToken(): String? {
            return dto?.let {
                internalApi.exchangeForOauthToken(it)
            }
        }
    }

    override suspend fun getAccount(): Account {
        val infoResult = internalApi.getAccountInfo()

        return infoResult.fold(
            onSuccess = { Account(it.displayName, it.defaultEmail) },
            onFailure = { Account("", "") }
        )
    }

    override fun createAuthSession(): AuthSession = YandexAuthSession(internalApi)
}