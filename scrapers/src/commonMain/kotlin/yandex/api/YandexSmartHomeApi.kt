package yandex.api

import SmartHomeApi
import models.Account
import models.Device
import models.ResultOrError
import yandex.internal.InternalYandexApi
import yandex.models.YandexAccountInfo
import yandex.models.YandexError
import yandex.models.YandexUserInfo

class YandexSmartHomeApi(
    private val internalApi: InternalYandexApi,
) : SmartHomeApi {
    override suspend fun listDevices(): ResultOrError<List<Device>, String> {
        return when (val result = internalApi.queryUserInfo()) {
            is ResultOrError.Success<YandexUserInfo> -> {
                val data = result.data.devices
                    .map { Device(it.id, it.name) }
                ResultOrError.Success(data)
            }
            is ResultOrError.Error<YandexError> -> {
                ResultOrError.Error(result.error.errorDescription)
            }
        }
    }

    override suspend fun findDeviceById(id: String): Device? {
        return when (val result = listDevices()) {
            is ResultOrError.Success<List<Device>> -> result.data.firstOrNull()
            is ResultOrError.Error<String> -> null
        }
    }

    override suspend fun getAccount(): ResultOrError<Account, String> {
        return when (val accountInfo = internalApi.getAccountInfo()) {
            is ResultOrError.Success<YandexAccountInfo> -> {
                val data = accountInfo.data.let { Account(it.displayName, it.defaultEmail) }
                ResultOrError.Success(data)
            }
            is ResultOrError.Error<YandexError> -> {
                ResultOrError.Error(accountInfo.error.errorDescription)
            }
        }
    }
}