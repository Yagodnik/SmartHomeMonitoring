package services

import bus.MetricsBus
import cli.CliConfig
import config.ConfigReader
import exporters.CsvExporter
import exporters.Exporter
import exporters.ExportersFactory
import exporters.PrometheusExporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import models.MetricsSnapshot
import printer.Color
import prometheus.PrometheusRegistry
import prometheus.PrometheusServer
import yandex.api.KtorYandexApi
import yandex.scraper.YandexScraper
import kotlin.time.Duration.Companion.seconds

class DefaultMonitoringService(
    token: String,
    private val cliConfig: CliConfig,
    private val scope: CoroutineScope,
    private val configReader: ConfigReader,
) : MonitoringService {
    private val bus = MetricsBus()
    private val api = KtorYandexApi(token)
    private val scraper = YandexScraper(api)

    private val exporters: List<Exporter>

    init {
        val exporterDefinitions = configReader.listExporters()
        val createdExporters: MutableList<Exporter> = mutableListOf()

        for (exporterDefinition in exporterDefinitions) {
            val exporter = ExportersFactory.create(exporterDefinition)
            createdExporters.add(exporter)
        }

        exporters = createdExporters
    }

    override fun start() {
        try {
            exporters.forEach { it.start() }
        } catch (e: Exception) {
            cliConfig.printer.println("Failed to start exporter due to: ${e.message}", fg = Color.RED)
            return
        }

        scope.launch {
            while (isActive) {
                val metrics = scraper.scrape()
                val snapshot = MetricsSnapshot(metrics)
                bus.publish(snapshot)

                delay(15.seconds)
            }
        }

        exporters.forEach { exporter ->
            scope.launch(Dispatchers.IO) {
                bus.events.collect { exporter.export(it) }
            }
        }
    }

    override fun shutdown() {
        exporters.forEach { exporter -> exporter.stop() }
    }
}