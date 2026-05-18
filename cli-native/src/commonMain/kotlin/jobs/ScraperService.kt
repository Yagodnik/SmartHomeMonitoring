package jobs

import Scraper
import bus.MetricBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration

class ScraperService(
    private val bus: MetricBus<Long>,
    private val interval: Duration,
    private val scraper: Scraper
) : AppService {
    override fun launchIn(scope: CoroutineScope) {
        scope.launch {
            while (true) {
                val metrics = scraper.scrape()

                println("${metrics.size} metrics received")
                for (metric in metrics) {
                    println("\t$metric")
                }

                bus.publish(Random.nextLong())
                delay(interval)
            }
        }
    }

    override suspend fun stop() = Unit
}