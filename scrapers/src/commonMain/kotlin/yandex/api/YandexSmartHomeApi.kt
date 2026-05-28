package yandex.api

import SmartHomeApi
import models.Account
import models.Device
import yandex.internal.YandexApi

class YandexSmartHomeApi(
    private val api: YandexApi,
) : SmartHomeApi {
    override suspend fun listDevices(): List<Device> {
        val result = api.queryUserInfo()

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
        val accountInfo = api.getAccountInfo()

        return accountInfo.fold(
            onSuccess = { Account(it.displayName, it.defaultEmail)},
            onFailure = { Account.empty() }
        )
    }
}