package bus

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MetricBus<T>(
    bufferSize: Int = 1000
) {
    private val _events = MutableSharedFlow<T>(
        replay = 0,
        extraBufferCapacity = bufferSize,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    fun publish(snapshot: T) {
        _events.tryEmit(snapshot)
    }
}