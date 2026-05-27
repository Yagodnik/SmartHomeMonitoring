package services

import Scraper
import bus.MetricsBus
import config.ExporterParams
import domain.MonitoringStartResult
import exporters.Exporter
import exporters.ExportersFactory
import kotlinx.coroutines.*
import models.MetricsSnapshot
import kotlin.time.Duration

class DefaultMonitoringService(
    private val scope: CoroutineScope,
    exporterDefinitions: List<ExporterParams>,
    private val pollingInterval: Duration,
    private val scraper: Scraper
) : MonitoringService {
    private val bus = MetricsBus()
    private val exporters: List<Exporter>

    init {
        val createdExporters: MutableList<Exporter> = mutableListOf()

        for (exporterDefinition in exporterDefinitions) {
            val exporter = ExportersFactory.create(exporterDefinition)
            createdExporters.add(exporter)
        }

        exporters = createdExporters
    }

    override fun start() : MonitoringStartResult {
        try {
            exporters.forEach { it.start() }
        } catch (e: Exception) {
            return MonitoringStartResult.Failure("Failed to start exporter due to: ${e.message}")
        }

        scope.launch {
            while (isActive) {
                val metrics = scraper.scrape()
                val snapshot = MetricsSnapshot(metrics)
                bus.publish(snapshot)

                delay(pollingInterval)
            }
        }

        exporters.forEach { exporter ->
            scope.launch(Dispatchers.IO) {
                bus.events.collect {
                    exporter.export(it)
                }
            }
        }

        return MonitoringStartResult.Success
    }

    override fun shutdown() {
        exporters.forEach { exporter -> exporter.stop() }
    }
}