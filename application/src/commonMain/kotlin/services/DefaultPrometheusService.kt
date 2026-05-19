package services

import models.MetricsSnapshot

class DefaultPrometheusService : PrometheusService {
    override fun formatToPrometheus(snapshot: MetricsSnapshot): String {
        val metrics = snapshot.metrics

        val metricsText = buildString {
            metrics.forEach { metric ->
                try {
                    val name = metric.value.name
                    val value = metric.value.value.toDouble()

                    append("# TYPE $name gauge\n")
                    append("# HELP $name Auto-collected metric\n")

                    append("$name{device_id=\"${metric.deviceId}\"} $value\n\n")
                } catch (e: NumberFormatException) {}
            }
        }

        return metricsText
    }
}