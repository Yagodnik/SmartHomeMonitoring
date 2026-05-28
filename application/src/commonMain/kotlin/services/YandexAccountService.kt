package services

import SmartHomeApi
import models.Account

class YandexAccountService(
    private val smartHomeApi: SmartHomeApi,
) : AccountService {
    override suspend fun getAccount(): Account {
        return smartHomeApi.getAccount()
    }
}