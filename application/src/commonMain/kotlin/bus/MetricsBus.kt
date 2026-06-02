package bus

import kotlinx.coroutines.flow.SharedFlow
import models.MetricsSnapshot

interface MetricsBus {
    fun publish(snapshot: MetricsSnapshot)

    fun getEvents(): SharedFlow<MetricsSnapshot>
}