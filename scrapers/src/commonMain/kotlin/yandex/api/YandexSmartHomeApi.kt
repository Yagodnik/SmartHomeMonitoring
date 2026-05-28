package yandex.api

import SmartHomeApi
import models.Device
import yandex.internal.YandexApi

class YandexSmartHomeApi(
    private val api: YandexApi,
) : SmartHomeApi {
    override suspend fun listDevices(): List<Device> {
        val result = api.queryUserInfo()

        result.fold(
            onSuccess = {
                return it.devices
                    .map { Device(it.id, it.name) }
            },
            onFailure = { return emptyList() }
        )
    }

    override suspend fun findDeviceById(id: String): Device? {
        return listDevices().firstOrNull { it.deviceId == id }
    }
}