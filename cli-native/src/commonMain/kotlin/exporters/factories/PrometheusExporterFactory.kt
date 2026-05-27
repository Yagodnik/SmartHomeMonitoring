package exporters.factories

import commands.YandexMonitoringApplication
import config.ExporterParams
import exporters.Exporter
import exporters.PrometheusExporter

class PrometheusExporterFactory : ExporterFactory {
    override fun create(params: ExporterParams): Exporter {
        val registry = YandexMonitoringApplication.registry
        return PrometheusExporter(registry)
    }
}