package exporters.factories

import config.ExporterParams
import exporters.Exporter

interface ExporterFactory {
    fun create(params: ExporterParams): Exporter
}