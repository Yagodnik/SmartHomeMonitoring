package exporters

import cli.CliConfig
import models.MetricsSnapshot
import printer.Color

class CsvExporter(
    private val cliConfig: CliConfig,
) : Exporter {
    override fun start() {
        cliConfig.printer.println("Kinda creating a file...?", fg = Color.YELLOW)
    }

    override fun export(snapshot: MetricsSnapshot) {
        val timestamp = snapshot.timestamp
        val metricsCount = snapshot.metrics.size

        cliConfig.printer.println("[$timestamp] Received $metricsCount metrics")
    }

    override fun stop() = Unit
}