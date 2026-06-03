package app

import Scraper
import SmartHomeApi
import bus.DefaultMetricsBus
import bus.MetricsBus
import com.github.ajalt.mordant.platform.MultiplatformSystem.exitProcess
import dev.scottpierce.envvar.EnvVar
import secrets.DefaultSecretsStorage
import secrets.EnvSecretsStorage
import services.AccountService
import services.SmartHomeService
import services.YandexAccountService
import services.YandexSmartHomeService
import yandex.api.YandexSmartHomeApi
import yandex.internal.KtorInternalYandexApi
import yandex.internal.InternalYandexApi
import yandex.scraper.YandexScraper

data class AppServices(
    val smartHomeService: SmartHomeService,
    val accountService: AccountService,
    private val internalApi: InternalYandexApi,
    val scraper: Scraper,
    val publicApi: SmartHomeApi,
    val metricsBus: MetricsBus
) {
    companion object {
        fun createYandexServices() : AppServices {
//            val secretsStorage = DefaultSecretsStorage()
            val secretsStorage = EnvSecretsStorage()

            val clientId = EnvVar["YANDEX_CLIENT_ID"]
            if (clientId == null) {
                println("No client ID provided!")
                exitProcess(1)
            }

            val internalApi: InternalYandexApi = KtorInternalYandexApi(secretsStorage, clientId ?: "")

            val scraper = YandexScraper(internalApi)
            val publicApi = YandexSmartHomeApi(internalApi)
            val smartHomeService = YandexSmartHomeService(publicApi)
            val accountService = YandexAccountService(internalApi, secretsStorage)
            val metricsBus = DefaultMetricsBus()

            return AppServices(
                smartHomeService,
                accountService,
                internalApi,
                scraper,
                publicApi,
                metricsBus
            )
        }
    }
}
