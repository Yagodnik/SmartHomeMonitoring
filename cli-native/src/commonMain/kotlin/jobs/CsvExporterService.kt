package jobs

import kotlinx.coroutines.*
import metrics.MetricsBus
import kotlin.time.Duration.Companion.seconds

class CsvExporterService(
    private val bus: MetricsBus,
) : AppService {
    private var job: Job? = null

    override fun launchIn(scope: CoroutineScope) {
        job = scope.launch(Dispatchers.IO) {
            bus.events.collect {
                println("at ${it.timestamp} ${it.metrics.size} metrics were received")
                for (metric in it.metrics) {
                    println("\t$metric")
                }

                delay(5.seconds)
            }
        }
    }

    override suspend fun stop() {
        job?.cancelAndJoin()
    }
}