package prometheus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import metrics.MetricsSnapshot

class PrometheusRegistry {
    private val _metrics = MutableStateFlow(MetricsSnapshot.empty())
    val metrics = _metrics.asStateFlow()

    fun update(snapshot: MetricsSnapshot) {
        _metrics.value = snapshot
    }

    fun get(): MetricsSnapshot = _metrics.value
}