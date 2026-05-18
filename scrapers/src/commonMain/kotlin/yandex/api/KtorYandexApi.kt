package yandex.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import yandex.models.YandexUserInfo

class KtorYandexApi(
    private val token: String,
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
) : YandexApi {
    companion object {
        private const val BASE_URL = "https://api.iot.yandex.net/v1.0"

        private const val USER_INFO = "/user/info"
    }

    override suspend fun queryUserInfo() : Result<YandexUserInfo> {
        return runCatching {
            client.get("$BASE_URL$USER_INFO") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body<YandexUserInfo>()
        }
    }
}