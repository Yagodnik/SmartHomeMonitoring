package yandex.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import yandex.models.YandexUserInfo

class KtorYandexApi(
    private val client: HttpClient = HttpClient(),
) : YandexApi {
    companion object {
        private const val baseUrl = "https://api.iot.yandex.net/v1.0"

        private const val userInfo = "/user/info"
    }

    override suspend fun queryUserInfo() : Result<YandexUserInfo> {
        val token = ""

        return runCatching {
            client.get("$baseUrl$userInfo") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body<YandexUserInfo>()
        }
    }
}