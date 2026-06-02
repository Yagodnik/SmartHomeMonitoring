import models.Account
import models.Device
import models.ResultOrError

interface SmartHomeApi {
    suspend fun listDevices() : ResultOrError<List<Device>, String>

    suspend fun findDeviceById(id: String) : Device?

    suspend fun getAccount(): ResultOrError<Account, String>
}