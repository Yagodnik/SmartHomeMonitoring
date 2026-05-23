package yandex.parsers.impl

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import models.MetricValue
import yandex.parsers.StateParser

class RangeStateParser : StateParser {
    override fun parse(state: JsonObject, parameters: JsonObject): MetricValue? {
        val instance = (state["instance"] as? JsonPrimitive)?.contentOrNull ?: return null
        val value = (state["value"] as? JsonPrimitive)?.contentOrNull ?: return null
        val unit = (parameters["unit"] as? JsonPrimitive)?.contentOrNull

        return MetricValue(
            name = instance,
            rawValue = value,
            unit = unit,
            numericValue = value.toDoubleOrNull()
        )
    }
}