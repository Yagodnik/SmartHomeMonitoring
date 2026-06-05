package yandex.scraper

import Scraper
import kotlinx.serialization.json.Json
import models.Metric
import models.ResultOrError
import models.ScrapeResult
import yandex.internal.InternalYandexApi
import yandex.models.YandexCapabilityType
import yandex.models.YandexError
import yandex.models.YandexPropertyType
import yandex.models.YandexUserInfo
import yandex.parsers.StateParser
import yandex.parsers.impl.*

class YandexScraper(
    private val api: InternalYandexApi,
) : Scraper {
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

    override suspend fun scrape(): ScrapeResult {
        return when (val result = api.queryUserInfo()) {
            is ResultOrError.Success<YandexUserInfo> -> {
                ScrapeResult.Success(scrapeFromUserInfo(result.data))
            }
            is ResultOrError.Error<YandexError> -> {
                ScrapeResult.Error(result.error.errorDescription)
            }
        }
    }

    fun scrapeFromText(content: String): List<Metric> {
        val info = json.decodeFromString<YandexUserInfo>(content)
        return scrapeFromUserInfo(info)
    }

    fun scrapeFromUserInfo(info: YandexUserInfo): List<Metric> {
        val metrics = mutableListOf<Metric>()

        for (device in info.devices) {
            for (capability in device.capabilities) {
                val metricValue = capabilityParsers[capability.type]?.parse(capability.state, capability.parameters)

                metricValue?.let { metric -> metrics.add(
                    Metric(device.id, device.name, metric))
                }
            }

            for (property in device.properties) {
                val metricValue = propertiesParsers[property.type]?.parse(property.state, property.parameters)

                metricValue?.let { metric -> metrics.add(
                    Metric(device.id, device.name, metric))
                }
            }

            println()
        }

        return metrics
    }
}