package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.table
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import services.SmartHomeService
import kotlin.time.Duration.Companion.seconds

class ListDevicesCommand(
    private val smartHomeService: SmartHomeService,
) : CliktCommand(name = "list-devices") {
    companion object {
        val DEFAULT_TIMEOUT = 5.seconds
    }

    override fun run() {
        runBlocking {
            val devices = try {
                withTimeout(DEFAULT_TIMEOUT) {
                    smartHomeService.listDevices()
                }
            } catch (_: TimeoutCancellationException) {
                terminal.println(TextColors.red("Timeout: failed to receive devices list"))
                return@runBlocking
            }

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
}