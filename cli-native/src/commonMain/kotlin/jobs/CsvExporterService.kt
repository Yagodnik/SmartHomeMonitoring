package jobs

import bus.MetricBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class CsvExporterService(
    private val bus: MetricBus<Long>,
) : AppService {
    private var job: Job? = null

    override fun launchIn(scope: CoroutineScope) {
        job = scope.launch(Dispatchers.IO) {
            bus.events.collect {
                println("Csv message: $it")
                delay(5.seconds)
            }
        }
    }

    override suspend fun stop() {
        job?.cancelAndJoin()
    }
}