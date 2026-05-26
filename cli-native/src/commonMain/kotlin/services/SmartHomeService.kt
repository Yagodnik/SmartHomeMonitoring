package services

import domain.Device

interface SmartHomeService {
    fun listDevices(): List<Device>
}