package services

import models.Device

interface SmartHomeService {
    fun listDevices(): List<Device>
}