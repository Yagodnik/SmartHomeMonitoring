package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import config.LocalConfigContentSource
import config.YamlConfigReader
import dev.scottpierce.envvar.EnvVar
import domain.MonitoringStartResult
import http.HttpServer
import kotlinx.coroutines.runBlocking
import prometheus.PrometheusRegistry
import services.DefaultMonitoringService
import services.DefaultPrometheusService
import yandex.api.YandexSmartHomeApi
import yandex.internal.KtorYandexApi
import yandex.internal.YandexApi
import yandex.scraper.YandexScraper
import kotlin.time.Duration.Companion.seconds

class YandexMonitoringApplication(
    t: Terminal
) : CliktCommand("smart-home-monitoring") {
    override val invokeWithoutSubcommand = true

    val tokenArgument by option("-t", "--token")
        .help("OAuth2 access token for yandex smart home api")
        .default("no token")

    val configArgument by option("-c", "--config")
        .help("Path to .yaml configuration file")
        .required()

//    val smartHomeService: SmartHomeService

    init {
        context { terminal = t }

//        subcommands(ListDevicesCommand(smartHomeService))
    }

    companion object {
        val registry = PrometheusRegistry()
        const val DEFAULT_PORT = 9091
    }

    override fun run() {
        val token = EnvVar["ACCESS_TOKEN"] ?: tokenArgument
        val internalApi: YandexApi = KtorYandexApi(token)
        val scraper = YandexScraper(internalApi)
        val publicApi = YandexSmartHomeApi(internalApi)
//        smartHomeService = YandexSmartHomeService(publicApi)

        val configContentSource = LocalConfigContentSource(configArgument)
        val configReader = YamlConfigReader(configContentSource)

        val exporterDefinitions = configReader.listExporters()
        val pollingInterval = configReader.getPollingInterval()?.seconds ?: 15.seconds
        val port = configReader.getServerPort() ?: DEFAULT_PORT

        val httpServer = HttpServer(
            port,
            registry,
            DefaultPrometheusService()
        )

        httpServer.start()

        runBlocking {
            val monitoringService = DefaultMonitoringService(
                this,
                exporterDefinitions,
                pollingInterval,
                scraper,
            )

            val result = monitoringService.start()

            if (result is MonitoringStartResult.Success) {
                terminal.println(TextColors.green("Monitoring started successfully"))
            } else if (result is MonitoringStartResult.Failure) {
                terminal.println(TextColors.red("Monitoring starting failed ${result.message}"))
                return@runBlocking
            }
        }

        httpServer.stop()

        terminal.println(TextColors.green("Bye!"))
    }
}