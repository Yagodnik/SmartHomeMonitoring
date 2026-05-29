package yandex.api

import SmartHomeApi
import models.Account
import models.Device
import yandex.internal.InternalYandexApi

class YandexSmartHomeApi(
    private val internalApi: InternalYandexApi,
) : SmartHomeApi {
    override suspend fun listDevices(): List<Device> {
        val result = internalApi.queryUserInfo()

        return result.fold(
            onSuccess = {
                it.devices
                    .map { device ->
                        Device(device.id, device.name)
                    }
            },
            onFailure = { emptyList() }
        )
    }

    override suspend fun findDeviceById(id: String): Device? {
        return listDevices().firstOrNull { it.deviceId == id }
    }

    override suspend fun getAccount(): Account {
        val accountInfo = internalApi.getAccountInfo()

        return accountInfo.fold(
            onSuccess = { Account(it.displayName, it.defaultEmail)},
            onFailure = { Account.empty() }
        )
    }
}