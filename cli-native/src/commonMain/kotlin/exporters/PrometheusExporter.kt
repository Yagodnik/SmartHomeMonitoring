package exporters

import models.MetricsSnapshot
import prometheus.PrometheusRegistry

class PrometheusExporter(
    private val registry: PrometheusRegistry,
) : Exporter {
    companion object {
        const val EXPORTER_NAME = "prometheus"
        const val DEFAULT_PORT = 9091
    }

    override fun start() = Unit

    override fun export(snapshot: MetricsSnapshot) {
        registry.update(snapshot)
    }

    override fun stop() = Unit

    override fun getName(): String = EXPORTER_NAME
}