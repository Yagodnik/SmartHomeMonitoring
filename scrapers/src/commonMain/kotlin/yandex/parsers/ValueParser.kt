package yandex.parsers

import kotlinx.serialization.json.JsonElement

interface ValueParser {
    fun parse(value: JsonElement): String?
}