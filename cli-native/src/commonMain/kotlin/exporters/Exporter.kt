package exporters

import models.MetricsSnapshot

interface Exporter {
    fun start()

    fun export(snapshot: MetricsSnapshot)

    fun stop()

    fun getName(): String
}