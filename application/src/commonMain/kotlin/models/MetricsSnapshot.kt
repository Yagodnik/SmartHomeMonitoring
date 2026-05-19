package models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class MetricsSnapshot(
    val metrics: List<Metric>,
    val timestamp: Instant = Clock.System.now(),
) {
    companion object {
        fun empty() = MetricsSnapshot(emptyList())
    }
}