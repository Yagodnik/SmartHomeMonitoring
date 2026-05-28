import models.Device

interface SmartHomeApi {
    suspend fun listDevices() : List<Device>

    suspend fun findDeviceById(id: String) : Device?
}