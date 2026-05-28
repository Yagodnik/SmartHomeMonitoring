package app

import Scraper
import SmartHomeApi
import services.AccountService
import services.SmartHomeService
import services.YandexAccountService
import services.YandexSmartHomeService
import yandex.api.YandexSmartHomeApi
import yandex.internal.KtorYandexApi
import yandex.internal.YandexApi
import yandex.scraper.YandexScraper

data class AppServices(
    val smartHomeService: SmartHomeService,
    val accountService: AccountService,
    private val internalApi: YandexApi,
    val scraper: Scraper,
    val publicApi: SmartHomeApi,
) {
    companion object {
        fun createYandexServices(token: String) : AppServices {
            val internalApi: YandexApi = KtorYandexApi(token)
            val scraper = YandexScraper(internalApi)
            val publicApi = YandexSmartHomeApi(internalApi)
            val smartHomeService = YandexSmartHomeService(publicApi)
            val accountService = YandexAccountService(publicApi)

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
