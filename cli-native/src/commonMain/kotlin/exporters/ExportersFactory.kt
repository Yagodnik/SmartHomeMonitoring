package exporters

import config.ExporterParams
import exporters.factories.CsvExporterFactory
import exporters.factories.ExporterFactory
import exporters.factories.PrometheusExporterFactory

class ExportersFactory {
    companion object {
        val definedExporters: Map<String, ExporterFactory> = mapOf(
            CsvExporter.EXPORTER_NAME to CsvExporterFactory(),
            PrometheusExporter.EXPORTER_NAME to PrometheusExporterFactory(),
        )

        fun create(params: ExporterParams): Exporter {
            val factory = definedExporters.getValue(params.name)
            return factory.create(params)
        }
    }
}