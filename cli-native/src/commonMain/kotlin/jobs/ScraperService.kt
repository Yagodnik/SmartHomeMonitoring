package jobs

import bus.MetricBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration

class ScraperService(
    private val bus: MetricBus<Long>,
    private val interval: Duration,
) : AppService {
    override fun launchIn(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                bus.publish(Random.nextLong())
                delay(interval)
            }
        }
    }

    override suspend fun stop() = Unit
}