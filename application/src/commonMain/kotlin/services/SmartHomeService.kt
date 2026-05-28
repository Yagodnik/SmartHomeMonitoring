package services

import models.Device

interface SmartHomeService {
    suspend fun listDevices(): List<Device>
}