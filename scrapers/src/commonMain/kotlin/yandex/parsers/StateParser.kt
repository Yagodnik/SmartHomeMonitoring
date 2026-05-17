package yandex.parsers

import kotlinx.serialization.json.JsonObject
import models.MetricValue

interface StateParser {
    fun parse(state: JsonObject): MetricValue?
}