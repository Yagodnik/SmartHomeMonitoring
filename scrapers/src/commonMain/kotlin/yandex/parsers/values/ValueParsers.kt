package yandex.parsers.values

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import yandex.parsers.ValueParser

class SceneInstanceParser : ValueParser {
    override fun parse(value: JsonElement): String? {
        return when (value) {
            is JsonPrimitive -> {
                value.contentOrNull
            }
            else -> null
        }
    }
}

class TemperatureKInstanceParser : ValueParser {
    override fun parse(value: JsonElement): String? {
        return when (value) {
            is JsonPrimitive -> {
                value.contentOrNull
            }
            else -> null
        }
    }
}

class RgbInstanceParser : ValueParser {
    override fun parse(value: JsonElement): String? {
        return when (value) {
            is JsonPrimitive -> {
                value.contentOrNull
            }
            else -> null
        }
    }
}

class HsvInstanceParser : ValueParser {
    companion object {
        val H_BOUNDS = 0..360
        val S_BOUNDS = 0..100
        val V_BOUNDS = 0..100
    }

    override fun parse(value: JsonElement): String? {
        return when (value) {
            is JsonObject -> {
                val h = (value["h"] as? JsonPrimitive)?.intOrNull ?: return null
                val s = (value["s"] as? JsonPrimitive)?.intOrNull ?: return null
                val v = (value["v"] as? JsonPrimitive)?.intOrNull ?: return null

                if (h !in H_BOUNDS || s !in S_BOUNDS || v !in V_BOUNDS) {
                    return null
                }

                "$h$s$v"
            }
            else -> null
        }
    }
}