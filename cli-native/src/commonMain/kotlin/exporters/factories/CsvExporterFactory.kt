package exporters.factories

import config.ExporterParams
import exporters.CsvExporter
import exporters.Exporter

class CsvExporterFactory : ExporterFactory {
    override fun create(params: ExporterParams): Exporter {
        return CsvExporter()
    }
}