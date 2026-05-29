package app

import Scraper
import SmartHomeApi
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
) {
    companion object {
        fun createYandexServices(token: String) : AppServices {
            val internalApi: InternalYandexApi = KtorInternalYandexApi(
                token,
                "")
            val scraper = YandexScraper(internalApi)
            val publicApi = YandexSmartHomeApi(internalApi)
            val smartHomeService = YandexSmartHomeService(publicApi)
            val accountService = YandexAccountService(internalApi)

            return AppServices(
                smartHomeService,
                accountService,
                internalApi,
                scraper,
                publicApi
            )
        }
    }
}
