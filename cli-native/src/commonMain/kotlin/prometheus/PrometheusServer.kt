package prometheus

import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.PrometheusService

class PrometheusServer(
    private val port: Int,
    private val registry: PrometheusRegistry,
    private val prometheusService: PrometheusService
) {
    private var engine: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null
    private val contentType = ContentType.parse("text/plain; version=0.0.4; charset=utf-8")

    fun start() {
        engine = embeddedServer(CIO, port = port) {
            routing {
                get("/metrics") {
                    val snapshot = registry.get()
                    val metricsText = prometheusService.formatToPrometheus(snapshot)

                    call.respondText(metricsText, contentType)
                }
            }
        }.start(wait = false)
    }

    fun stop() {
        engine?.stop(gracePeriodMillis = 1000, timeoutMillis = 5000)
    }
}