package yandex.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import yandex.models.YandexUserInfo

class KtorYandexApi(
    private val token: String,
    engine: HttpClientEngine = CIO.create(),
) : YandexApi {
    companion object {
        private const val BASE_URL = "https://api.iot.yandex.net/v1.0"

        private const val USER_INFO = "/user/info"
    }

    private val client = HttpClient(engine) {}

    override suspend fun queryUserInfo() : Result<YandexUserInfo> {
        return runCatching {
            client.get("$BASE_URL$USER_INFO") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body<YandexUserInfo>()
        }
    }
}