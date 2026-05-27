package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.table.table
import services.SmartHomeService

class ListDevicesCommand(
    private val smartHomeService: SmartHomeService,
) : CliktCommand(name = "list-devices") {
    override fun run() {
        val devices = smartHomeService.listDevices()

        val table = table {
            header { row("Device ID", "Device Name") }
            body {
                devices.forEach { (id, name) ->
                    row(id, name)
                }
            }
        }

        terminal.println(table)
    }
}