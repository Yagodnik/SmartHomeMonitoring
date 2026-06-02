package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.table
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import models.Device
import models.ResultOrError
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
            val result = try {
                withTimeout(DEFAULT_TIMEOUT) {
                    smartHomeService.listDevices()
                }
            } catch (_: TimeoutCancellationException) {
                terminal.println(TextColors.red("Timeout: failed to receive devices list"))
                return@runBlocking
            }

            when (result) {
                is ResultOrError.Success<List<Device>> -> {
                    val table = table {
                        header { row("Device ID", "Device Name") }
                        body {
                            result.data.forEach { (id, name) ->
                                row(id, name)
                            }
                        }
                    }

                    terminal.println(table)
                }
                is ResultOrError.Error<String> -> {
                    terminal.println(TextColors.red("Failed: ${result.error}"))
                }
            }
        }
    }
}