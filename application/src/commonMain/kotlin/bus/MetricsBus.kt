package bus

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import models.MetricsSnapshot

class MetricsBus(
    bufferSize: Int = 1000
) {
    private val _events = MutableSharedFlow<MetricsSnapshot>(
        replay = 0,
        extraBufferCapacity = bufferSize,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    fun publish(snapshot: MetricsSnapshot) {
        _events.tryEmit(snapshot)
    }
}