package yandex.parsers

import io.ktor.http.Parameters
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import models.MetricValue

interface StateParser {
    fun parse(state: JsonObject, parameters: JsonObject = buildJsonObject {}): MetricValue?
}