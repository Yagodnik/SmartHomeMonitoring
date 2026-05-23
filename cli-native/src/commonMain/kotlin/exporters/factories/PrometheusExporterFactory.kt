package exporters.factories

import config.ExporterParams
import exporters.Exporter
import exporters.PrometheusExporter
import prometheus.PrometheusRegistry

class PrometheusExporterFactory : ExporterFactory {
    companion object {
        const val PROMETHEUS_PORT_PARAM_NAME = "port"
    }

    override fun create(params: ExporterParams): Exporter {
        val registry = PrometheusRegistry()

        val port = params.params[PROMETHEUS_PORT_PARAM_NAME]?.toIntOrNull()

        return PrometheusExporter(registry, port)
    }
}