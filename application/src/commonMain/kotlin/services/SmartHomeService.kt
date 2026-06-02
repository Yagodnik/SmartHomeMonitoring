package services

import models.Device
import models.ResultOrError

interface SmartHomeService {
    suspend fun listDevices(): ResultOrError<List<Device>, String>
}