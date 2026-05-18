import metrics.MetricsBus
import cli.parseCliArgs
import config.YamlConfigReader
import dev.scottpierce.envvar.EnvVar
import jobs.CsvExporterService
import jobs.PrometheusExporterService
import jobs.ScraperService
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import printer.Color
import prometheus.PrometheusRegistry
import prometheus.PrometheusServer
import yandex.api.KtorYandexApi
import yandex.scraper.YandexScraper
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

    val bus = MetricsBus()

    val token = EnvVar["ACCESS_TOKEN"] ?: "No token specified"
    val api = KtorYandexApi(token)
    val scraper = YandexScraper(api)
    val prometheusRegistry = PrometheusRegistry()
    val prometheusServer = PrometheusServer(9091, prometheusRegistry)

    val services = listOf(
        ScraperService(bus, 10.seconds, scraper),
        CsvExporterService(bus),
        PrometheusExporterService(bus, prometheusRegistry),
    )

    runBlocking {
        services.forEach { it.launchIn(this) }

        prometheusServer.start()

        try {
            awaitCancellation()
        } finally {
            cliConfig.printer.println("Shutting down...", fg = Color.YELLOW)

            prometheusServer.stop()
            services.reversed().forEach { it.stop() }

            cliConfig.printer.println("All services stopped.", fg = Color.GREEN)
        }
    }
}
