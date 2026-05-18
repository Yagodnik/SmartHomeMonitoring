package prometheus

import io.ktor.http.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class PrometheusServer(
    private val port: Int,
    private val registry: PrometheusRegistry
) {
    private var engine: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    fun start() {
        engine = embeddedServer(CIO, port = port) {
            routing {
                get("/metrics") {
                    val snapshot = registry.get()
                    val metrics = snapshot.metrics

                    val metricsText = buildString {
                        metrics.forEach { metric ->
                            try {
                                val name = metric.value.name
                                val value = metric.value.value.toDouble()

                                append("# TYPE $name gauge\n")
                                append("# HELP $name Auto-collected metric\n")

                                append("$name $value\n\n")
                            } catch (e: NumberFormatException) {}
                        }
                    }

                    call.respondText(
                        text = metricsText,
                        contentType = ContentType.parse("text/plain; version=0.0.4; charset=utf-8")
                    )
                }
            }
        }.start(wait = false)
    }

    fun stop() {
        engine?.stop(gracePeriodMillis = 1000, timeoutMillis = 5000)
    }
}