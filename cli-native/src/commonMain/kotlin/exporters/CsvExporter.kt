package exporters

import cli.CliConfig
import models.MetricsSnapshot
import printer.Color

class CsvExporter : Exporter {
    companion object {
        const val EXPORTER_NAME = "csv"
    }

    override fun start() {
    }

    override fun export(snapshot: MetricsSnapshot) {
        val timestamp = snapshot.timestamp
        val metricsCount = snapshot.metrics.size
    }

    override fun stop() = Unit

    override fun getName(): String = EXPORTER_NAME
}