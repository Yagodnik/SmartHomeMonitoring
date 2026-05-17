package yandex.parsers.impl

import kotlinx.serialization.json.JsonObject
import models.MetricValue
import yandex.parsers.StateParser

class ToggleStateParser : StateParser {
    override fun parse(state: JsonObject): MetricValue? {
        return null
    }
}