package yandex.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import yandex.models.YandexUserInfo

class KtorYandexApi(
    engine: HttpClientEngine = CIO.create(),
) : YandexApi {
    companion object {
        private const val baseUrl = "https://api.iot.yandex.net/v1.0"

        private const val userInfo = "/user/info"
    }

    private val client = HttpClient(engine) {}

    override suspend fun queryUserInfo() : Result<YandexUserInfo> {
        val token = ""

        return runCatching {
            client.get("$baseUrl$userInfo") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body<YandexUserInfo>()
        }
    }
}