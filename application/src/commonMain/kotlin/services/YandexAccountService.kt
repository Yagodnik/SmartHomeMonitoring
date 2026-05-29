package services

import kotlinx.coroutines.delay
import models.Account
import yandex.internal.InternalYandexApi
import kotlin.time.Duration.Companion.seconds

class YandexAccountService(
    private val internalApi: InternalYandexApi
) : AccountService {
    override suspend fun getAccount(): Account {
        // Request code here

        // Exchange code here

        TODO("Not yet implemented")
    }

    override suspend fun requestUserCode(): String? {
        val dto = internalApi.requestCode()

        return dto?.userCode
    }

    override suspend fun exchangeForToken(): String? {
        delay(10.seconds)

        return null
    }
}