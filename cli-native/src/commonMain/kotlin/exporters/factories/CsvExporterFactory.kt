package exporters.factories

import config.ExporterParams
import exporters.CsvExporter
import exporters.Exporter

class CsvExporterFactory : ExporterFactory {
    companion object {
        const val DEFAULT_FORMAT = "{{ deviceName }} - {{ value }}"
    }

    override fun create(params: ExporterParams): Exporter {
        val format = params.params["format"] ?: DEFAULT_FORMAT
        val outputDir = params.params["outputDir"] ?: throw IllegalArgumentException("No path provided")

        return CsvExporter(format, outputDir)
    }
}