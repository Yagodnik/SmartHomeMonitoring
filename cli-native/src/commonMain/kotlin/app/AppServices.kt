package app

import Scraper
import SmartHomeApi
import bus.DefaultMetricsBus
import bus.MetricsBus
import secrets.FileSecretsStorage
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
        fun createYandexServices(configuration: Configuration) : AppServices {
//            val secretsStorage = DefaultSecretsStorage()
//            val secretsStorage = EnvSecretsStorage()

            val secretsStorage = FileSecretsStorage(
                configuration.credentialsDir,
                configuration.masterKey)
            val internalApi: InternalYandexApi = KtorInternalYandexApi(secretsStorage, configuration.yandexClientId)
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

    fun close() {
        internalApi.close()
    }
}
