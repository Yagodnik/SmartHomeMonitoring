package http

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import prometheus.PrometheusRegistry
import services.PrometheusService

class HttpServer(
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

                get("/health") {
                    call.respondText("Healthy", status = HttpStatusCode.OK)
                }
            }
        }.start(wait = false)
    }

    fun stop() {
        engine?.stop(gracePeriodMillis = 1000, timeoutMillis = 5000)
    }
}