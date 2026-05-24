package exporters

import kotlinx.datetime.Instant
import models.Metric
import models.MetricsSnapshot
import utils.resolveTemplate

class CsvExporter(
    private val format: String
) : Exporter {
    companion object {
        const val EXPORTER_NAME = "csv"
    }

    override fun start() = Unit

    override fun export(snapshot: MetricsSnapshot) {
        val timestamp = snapshot.timestamp

        for (metric in snapshot.metrics) {
            val values = buildValuesForMetric(timestamp, metric)
            val formattedString = format.resolveTemplate(values)

            println(formattedString)
        }
    }

    override fun stop() = Unit

    override fun getName(): String = EXPORTER_NAME

    private fun buildValuesForMetric(timestamp: Instant, metric: Metric): Map<String, String> =
        mapOf(
            "timestamp" to timestamp.toString(),
            "deviceId" to metric.deviceId,
            "deviceName" to metric.deviceName,
            "value" to metric.value.numericValue.toString(),
            "rawValue" to metric.value.rawValue,
        )
}