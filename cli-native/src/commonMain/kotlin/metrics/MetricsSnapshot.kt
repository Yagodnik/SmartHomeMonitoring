package metrics

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import models.Metric

data class MetricsSnapshot(
    val metrics: List<Metric>,
    val timestamp: Instant = Clock.System.now(),
) {
    companion object {
        fun empty() = MetricsSnapshot(emptyList())
    }
}
