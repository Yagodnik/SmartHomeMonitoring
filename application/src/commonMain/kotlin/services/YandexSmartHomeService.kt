package services

import SmartHomeApi
import models.Device

class YandexSmartHomeService(
    private val api: SmartHomeApi
) : SmartHomeService {
    override suspend fun listDevices(): List<Device> {
        return api.listDevices()
    }
}