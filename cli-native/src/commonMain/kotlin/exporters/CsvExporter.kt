package exporters

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import kotlinx.io.RawSink
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString
import models.Metric
import models.MetricsSnapshot
import utils.resolveTemplate

class CsvExporter(
    private val format: String,
    private val outputDir: String,
) : Exporter {
    companion object {
        const val EXPORTER_NAME = "csv"
        const val FEATURE_NAME_PREFIX = "export"
        const val EXTENSION = ".csv"
    }

    private var currentSink: Sink? = null
    private var currentFileDate: LocalDate? = null

    override fun start() = Unit

    override fun export(snapshot: MetricsSnapshot) {
        val timestamp = snapshot.timestamp

        for (metric in snapshot.metrics) {
            val values = buildValuesForMetric(timestamp, metric)
            val formattedString = format.resolveTemplate(values)

//            println(formattedString)
            writeString(timestamp, formattedString)
        }
    }

    override fun stop() {
        try {
            currentSink?.close()
        } catch (_: Exception) {}

        currentSink = null
        currentFileDate = null
    }

    override fun getName(): String = EXPORTER_NAME

    private fun buildValuesForMetric(timestamp: Instant, metric: Metric): Map<String, String> =
        mapOf(
            "timestamp" to timestamp.toString(),
            "deviceId" to metric.deviceId,
            "deviceName" to metric.deviceName,
            "value" to metric.value.numericValue.toString(),
            "rawValue" to metric.value.rawValue,
            "metric" to metric.value.name
        )

    private fun writeString(timestamp: Instant, formattedLine: String) {
        try {
            val snapshotDate = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date

            if (currentFileDate != snapshotDate || currentSink == null) {
                currentSink?.close()
                openNewFileForDate(snapshotDate)
            }

            val sink = currentSink ?: return
            sink.writeString("$formattedLine\n")
            sink.flush()
        } catch (e: IOException) {
            println("CsvExporter failed: $e")
        }
    }

    private fun openNewFileForDate(date: LocalDate) {
        val fileName = "$FEATURE_NAME_PREFIX-$date$EXTENSION"
        val newPath = Path(outputDir, fileName)

        currentSink = SystemFileSystem.sink(newPath, append = true).buffered()
        currentFileDate = date
    }
}