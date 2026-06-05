package yandex.parsers.impl

import kotlinx.serialization.json.JsonObject
import models.MetricValue
import yandex.parsers.StateParser

class VideoStreamStateParser : StateParser {
    override fun parse(state: JsonObject, parameters: JsonObject): MetricValue? {
        return null
    }
}