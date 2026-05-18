package jobs

import metrics.MetricsBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import prometheus.PrometheusRegistry

class PrometheusExporterService(
    private val bus: MetricsBus,
    private val prometheusRegistry: PrometheusRegistry
) : AppService {
    private var job: Job? = null

    override fun launchIn(scope: CoroutineScope) {
        job = scope.launch(Dispatchers.IO) {
            bus.events.collect {
                println("Prometheus snapshot: $it")
                prometheusRegistry.update(it)
            }
        }
    }

    override suspend fun stop() {
        job?.cancelAndJoin()
    }
}