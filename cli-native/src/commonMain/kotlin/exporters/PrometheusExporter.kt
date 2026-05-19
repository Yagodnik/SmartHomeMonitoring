package exporters

import models.MetricsSnapshot
import prometheus.PrometheusRegistry
import services.PrometheusService

class PrometheusExporter(
    private val registry: PrometheusRegistry,
) : Exporter {
    override fun start() = Unit

    override fun export(snapshot: MetricsSnapshot) {
        registry.update(snapshot)
    }

    override fun stop() = Unit
}