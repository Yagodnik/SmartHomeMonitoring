package bus

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import models.MetricsSnapshot

class DefaultMetricsBus(
    bufferSize: Int = 1000
) : MetricsBus {
    private val _events = MutableSharedFlow<MetricsSnapshot>(
        replay = 0,
        extraBufferCapacity = bufferSize,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    override fun publish(snapshot: MetricsSnapshot) {
        _events.tryEmit(snapshot)
    }

    override fun getEvents(): SharedFlow<MetricsSnapshot> = _events.asSharedFlow()
}