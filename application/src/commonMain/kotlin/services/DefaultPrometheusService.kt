package services

import models.MetricsSnapshot

class DefaultPrometheusService : PrometheusService {
    override fun formatToPrometheus(snapshot: MetricsSnapshot): String {
        val metrics = snapshot.metrics

        val metricsText = buildString {
            metrics.forEach { metric ->
                val metricName = "metric_${metric.value.name}"
                val metricValue = metric.value.numericValue

                metricValue?.let {
                    append("# TYPE $metricName gauge\n")
                    append("# HELP $metricName Auto-collected metric\n")

                    append("$metricName{device_id=\"${metric.deviceId}\", device_name=\"${metric.deviceName}\"} $metricValue\n\n")
                }
            }
        }

        return metricsText
    }
}