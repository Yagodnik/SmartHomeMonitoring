package jobs

import bus.MetricBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class PrometheusExporterService(
    private val bus: MetricBus<Long>
) : AppService {
    private var job: Job? = null

    override fun launchIn(scope: CoroutineScope) {
        job = scope.launch(Dispatchers.IO) {
            bus.events.collect { println("Prometheus message: $it") }
        }
    }

    override suspend fun stop() {
        job?.cancelAndJoin()
    }
}