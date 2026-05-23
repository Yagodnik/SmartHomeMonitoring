package exporters

import models.MetricsSnapshot
import prometheus.PrometheusRegistry
import prometheus.PrometheusServer
import services.DefaultPrometheusService

class PrometheusExporter(
    private val registry: PrometheusRegistry,
    port: Int? = null
) : Exporter {
    companion object {
        const val EXPORTER_NAME = "prometheus"
        const val DEFAULT_PORT = 9091
    }

    private val prometheusServer = PrometheusServer(
        port ?: DEFAULT_PORT,
        registry,
        DefaultPrometheusService()
    )

    override fun start() = prometheusServer.start()

    override fun export(snapshot: MetricsSnapshot) {
        registry.update(snapshot)
    }

    override fun stop() = prometheusServer.stop()

    override fun getName(): String = EXPORTER_NAME
}