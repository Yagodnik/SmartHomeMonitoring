package services

import domain.Device

class YandexSmartHomeService(
) : SmartHomeService {
    override fun listDevices(): List<Device> {
        return listOf(
            Device("1", "Lamp"),
            Device("2", "Lamp 2"),
            Device("1", "Lamp 3"),
        )
    }
}