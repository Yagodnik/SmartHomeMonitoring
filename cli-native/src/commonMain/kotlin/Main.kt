import cli.parseCliArgs
import config.YamlConfigReader
import dev.scottpierce.envvar.EnvVar
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import printer.Color
import services.DefaultMonitoringService

fun main(args: Array<String>) {
    val cliConfig = try {
        parseCliArgs(args)
    } catch (e: Exception) {
        println("CLI Error: ${e.message}")
        return
    }

    val configReader = YamlConfigReader(cliConfig.configPath)

    if (!configReader.isReady()) {
        cliConfig.printer.println("Configuration not found at ${cliConfig.configPath}!", fg = Color.RED)
//        return
    }

    cliConfig.printer.println("Exporter initialized successfully.", fg = Color.GREEN)

    runBlocking {
        val monitoringService = DefaultMonitoringService(
            EnvVar["ACCESS_TOKEN"] ?: "No token",
            cliConfig,
            this
        )

        monitoringService.start()

        try {
            awaitCancellation()
        } finally {
            cliConfig.printer.println("Shutting down...", fg = Color.YELLOW)
            monitoringService.shutdown()
            cliConfig.printer.println("All services stopped.", fg = Color.GREEN)
        }
    }
}
