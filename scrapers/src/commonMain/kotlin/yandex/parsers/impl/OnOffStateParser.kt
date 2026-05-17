package yandex.parsers.impl

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import models.MetricValue
import yandex.parsers.StateParser

class OnOffStateParser : StateParser {
    override fun parse(state: JsonObject): MetricValue? {
        val instance = (state["instance"] as? JsonPrimitive)?.contentOrNull ?: return null
        val value = (state["value"] as? JsonPrimitive)?.contentOrNull ?: return null

        return MetricValue(instance, value)
    }
}