package exporters

import models.Metric

interface Exporter {
    fun export(metrics: List<Metric>)
}