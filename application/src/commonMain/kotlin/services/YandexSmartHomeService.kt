package services

import SmartHomeApi
import models.Device
import models.ResultOrError

class YandexSmartHomeService(
    private val api: SmartHomeApi
) : SmartHomeService {
    override suspend fun listDevices(): ResultOrError<List<Device>, String>
        = api.listDevices()
}