package commands

import app.AppServices
import bus.DefaultMetricsBus
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import config.LocalConfigContentSource
import config.YamlConfigReader
import domain.MonitoringStartResult
import http.HttpServer
import kotlinx.coroutines.runBlocking
import prometheus.PrometheusRegistry
import services.DefaultMonitoringService
import services.DefaultPrometheusService
import kotlin.time.Duration.Companion.seconds

class YandexMonitoringApplication(
    t: Terminal,
    private val appServices: AppServices,
) : CliktCommand("smart-home-monitoring") {
    override val invokeWithoutSubcommand = true

    val configArgument by option("-c", "--config")
        .help("Path to .yaml configuration file")
        .required()

    init {
        context { terminal = t }

        subcommands(
            ListDevicesCommand(appServices.smartHomeService),
            PrintYandexAccountInfoCommand(appServices.accountService),
            LoginYandexAccountCommand(appServices.accountService),
            LogoutYandexAccountCommand(appServices.accountService),
        )
    }

    companion object {
        val registry = PrometheusRegistry()

        const val DEFAULT_PORT = 9091
        val DEFAULT_POLLING_INTERVAL = 15.seconds
    }

    override fun run() {
        if (currentContext.invokedSubcommand != null) {
            return
        }

        val configContentSource = LocalConfigContentSource(configArgument)
        val configReader = YamlConfigReader(configContentSource)

        val exporterDefinitions = configReader.listExporters()
        val pollingInterval = configReader.getPollingInterval()?.seconds ?: DEFAULT_POLLING_INTERVAL
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
                appServices.scraper,
                appServices.metricsBus,
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