package commands

import app.AppServices
import app.AppServices.Companion.createYandexServices
import app.Configuration
import app.Configuration.Companion.DEFAULT_CREDENTIALS_DIR
import bus.DefaultMetricsBus
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.mordant.platform.MultiplatformSystem.exitProcess
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.warning
import config.LocalConfigContentSource
import config.YamlConfigReader
import dev.scottpierce.envvar.EnvVar
import domain.MonitoringStartResult
import http.HttpServer
import kotlinx.coroutines.runBlocking
import prometheus.PrometheusRegistry
import services.DefaultMonitoringService
import services.DefaultPrometheusService
import kotlin.time.Duration.Companion.seconds

class YandexMonitoringApplication(private val t: Terminal) : CliktCommand("smart-home-monitoring") {
    companion object {
        val registry = PrometheusRegistry()

        const val DEFAULT_PORT = 9091
        val DEFAULT_POLLING_INTERVAL = 15.seconds

        const val CREDENTIALS_DIR_PARAM_NAME = "CREDENTIALS_DIR"
        const val MASTER_KEY_PARAM_NAME = "MASTER_KEY"
        const val YANDEX_CLIENT_ID_PARAM_NAME = "YANDEX_CLIENT_ID"

        const val FAILURE_CODE = 1
    }

    override val invokeWithoutSubcommand = true

    private val configArgument by option("-c", "--config")
        .help("Path to .yaml configuration file")
        .required()

    private lateinit var appServices: AppServices

    init {
        context { terminal = t }

        createServices()?.let {
            appServices = it
        } ?: run {
            t.println(TextColors.red("Failed to initialize app services"))
            exitProcess(FAILURE_CODE)
        }

        appServices.let {
            subcommands(
                ListDevicesCommand(it.smartHomeService),
                PrintYandexAccountInfoCommand(it.accountService),
                LoginYandexAccountCommand(it.accountService),
                LogoutYandexAccountCommand(it.accountService),
            )
        }
    }

    private fun createServices(): AppServices? {
        val credentialsDir = EnvVar[CREDENTIALS_DIR_PARAM_NAME]
        val masterKey = EnvVar[MASTER_KEY_PARAM_NAME]
        val clientId = EnvVar[YANDEX_CLIENT_ID_PARAM_NAME]

        if (credentialsDir == null) {
            t.warning("" +
                    "No credentials directory specified. Fallback to default")
        } else {
            t.println(TextColors.green("" +
                    "Using $credentialsDir as credentials directory"))
        }

        if (masterKey == null) {
            t.println(TextColors.red("" +
                    "Provide a master key via $MASTER_KEY_PARAM_NAME env variable"))
            return null
        } else {
            t.println(TextColors.green("" +
                    "Found a master key"))
        }

        if (clientId == null) {
            t.println(TextColors.red("" +
                    "Client ID for yandex was not provided. " +
                    "Specify it via $YANDEX_CLIENT_ID_PARAM_NAME env variable"))
            return null
        } else {
            t.println(TextColors.green("" +
                    "Yandex Client ID is provided"))
        }

        val configuration = Configuration(
            credentialsDir ?: DEFAULT_CREDENTIALS_DIR,
            masterKey,
            clientId)

        t.println(TextColors.green("" +
                "App services are ready"))

        return createYandexServices(configuration)
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