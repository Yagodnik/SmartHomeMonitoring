package yandex.parsers.impl

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import models.MetricValue
import yandex.parsers.StateParser
import yandex.parsers.ValueParser
import yandex.parsers.values.HsvInstanceParser
import yandex.parsers.values.RgbInstanceParser
import yandex.parsers.values.SceneInstanceParser
import yandex.parsers.values.TemperatureKInstanceParser

class ColorSettingStateParser : StateParser {
    companion object {
        const val INSTANCE_HSV = "hsv"
        const val INSTANCE_RGB = "rgb"
        const val INSTANCE_TEMPERATURE_K = "temperature_k"
        const val INSTANCE_SCENE = "scene"

        private val parsers: Map<String, ValueParser> = mapOf(
            INSTANCE_HSV to HsvInstanceParser(),
            INSTANCE_RGB to RgbInstanceParser(),
            INSTANCE_TEMPERATURE_K to TemperatureKInstanceParser(),
            INSTANCE_SCENE to SceneInstanceParser()
        )
    }

    override fun parse(state: JsonObject, parameters: JsonObject): MetricValue? {
        val instance = (state["instance"] as? JsonPrimitive)?.content ?: return null
        val valueObject = state["value"] ?: return null
        val value = parsers[instance]?.parse(valueObject) ?: return null
        var unit = (parameters["unit"] as? JsonPrimitive)?.contentOrNull

        if (instance == INSTANCE_TEMPERATURE_K) {
            unit = "kelvin"
        }

        return MetricValue(
            name =instance,
            rawValue = value,
            unit = unit,
            numericValue = value.toDoubleOrNull()
        )
    }
}