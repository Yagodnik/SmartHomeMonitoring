package yandex.scraper

import Scraper
import kotlinx.serialization.json.Json
import models.Metric
import yandex.models.YandexCapabilityType
import yandex.models.YandexPropertyType
import yandex.models.YandexUserInfo
import yandex.parsers.StateParser
import yandex.parsers.impl.*

class YandexScraper : Scraper {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val capabilityParsers: Map<YandexCapabilityType, StateParser> = mapOf(
        YandexCapabilityType.ON_OFF to OnOffStateParser(),
        YandexCapabilityType.VIDEO_STREAM to VideoStreamStateParser(),
        YandexCapabilityType.COLOR_SETTING to ColorSettingStateParser(),
        YandexCapabilityType.MODE to ModeStateParser(),
        YandexCapabilityType.RANGE to RangeStateParser(),
        YandexCapabilityType.TOGGLE to ToggleStateParser(),
    )

    private val propertiesParsers: Map<YandexPropertyType, StateParser> = mapOf(
        YandexPropertyType.EVENT to EventPropertyParser(),
        YandexPropertyType.FLOAT to FloatPropertyParser(),
    )

    override fun scrape(): List<Metric> {
        TODO("Not yet implemented")
    }

    fun scrapeFromText(content: String): List<Metric> {
        val info = json.decodeFromString<YandexUserInfo>(content)
        val metrics = mutableListOf<Metric>()

        println(info.status)
        println(info.requestId)
        println("Devices count: ${info.devices.size}")

        for (device in info.devices) {
            println("Device [${device.id}]: ${device.name}")

            println("Capabilities (${device.capabilities.size})")
            for (capability in device.capabilities) {
                val metricValue = capabilityParsers[capability.type]?.parse(capability.state)
                println("\t${metricValue}")

                metricValue?.let { metric -> metrics.add(
                    Metric(device.id, device.name, metric))
                }
            }

            println("Properties (${device.properties.size})")
            for (property in device.properties) {
                val metricValue = propertiesParsers[property.type]?.parse(property.state)
                println("\t${metricValue}")

                metricValue?.let { metric -> metrics.add(
                    Metric(device.id, device.name, metric))
                }
            }

            println()
        }

        return metrics
    }
}