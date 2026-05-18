import bus.MetricBus
import cli.parseCliArgs
import config.YamlConfigReader
import jobs.AppService
import jobs.CsvExporterService
import jobs.PrometheusExporterService
import jobs.ScraperService
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import printer.Color
import kotlin.time.Duration.Companion.seconds

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

    val bus = MetricBus<Long>()

    val services = listOf(
        ScraperService(bus, 1.seconds),
        CsvExporterService(bus),
        PrometheusExporterService(bus),
    )

    runBlocking {
        services.forEach { it.launchIn(this) }

        try {
            awaitCancellation()
        } finally {
            cliConfig.printer.println("Shutting down...", fg = Color.YELLOW)
            services.reversed().forEach { it.stop() }
            cliConfig.printer.println("All services stopped.", fg = Color.GREEN)
        }
    }
}
