package jobs

import Scraper
import metrics.MetricsBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import metrics.MetricsSnapshot
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.TimeSource

class ScraperService(
    private val bus: MetricsBus,
    private val interval: Duration,
    private val scraper: Scraper
) : AppService {
    override fun launchIn(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                val metrics = scraper.scrape()
                val snapshot = MetricsSnapshot(metrics)
                bus.publish(snapshot)

                delay(interval)
            }
        }
    }

    override suspend fun stop() = Unit
}