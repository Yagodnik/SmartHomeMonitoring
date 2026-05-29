package yandex.internal

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import yandex.models.YandexAccountInfo
import yandex.models.YandexDeviceCodeBody
import yandex.models.YandexTokenBody
import yandex.models.YandexUserInfo
import kotlin.time.Duration.Companion.seconds

class KtorInternalYandexApi(
    private val token: String,
    private val clientId: String? = null,
    private val clientSecret: String? = null,
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    },
) : InternalYandexApi {
    companion object {
        private const val AUTH_BASE_URL = "https://oauth.yandex.ru"
        private const val BASE_URL = "https://api.iot.yandex.net/v1.0"
        private const val ACCOUNT_BASE_URL = "https://login.yandex.ru/info"

        private const val USER_INFO = "/user/info"
        private const val REQUEST_CODE = "/device/code"
        private const val EXCHANGE_FOR_TOKEN = "/token"

        private const val AUTH_SCOPE = "iot:view"
        private const val GRANT_TYPE = "device_code"
    }

    override suspend fun requestCode(): YandexDeviceCodeBody? {
        val response = client.post("$AUTH_BASE_URL$REQUEST_CODE") {
            contentType(ContentType.Application.FormUrlEncoded)

            setBody(FormDataContent(Parameters.build {
                append("client_id", clientId ?: "")
                append("scope", AUTH_SCOPE)
            }))
        }

        if (response.status.isSuccess()) {
            return response.body<YandexDeviceCodeBody>()
        }

        println(response.status.value)

        return null
    }

    override suspend fun exchangeForOauthToken(deviceCodeDto: YandexDeviceCodeBody): String? {
        val requestsCount = deviceCodeDto.expiresIn / deviceCodeDto.interval
        val interval = deviceCodeDto.interval

        (1..requestsCount).forEach { _ ->
            val tokenBody = exchangeForToken(deviceCodeDto.deviceCode)

            if (tokenBody != null) {
                return tokenBody.accessToken
            }

            delay(interval.seconds)
        }

        return null
    }

    override suspend fun queryUserInfo() : Result<YandexUserInfo> {
        return runCatching {
            client.get("$BASE_URL$USER_INFO") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body<YandexUserInfo>()
        }
    }

    override suspend fun getAccountInfo(): Result<YandexAccountInfo> {
        return runCatching {
            client.get(ACCOUNT_BASE_URL) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body<YandexAccountInfo>()
        }
    }

    private suspend fun exchangeForToken(code: String) : YandexTokenBody? {
        val response = client.post("$AUTH_BASE_URL$EXCHANGE_FOR_TOKEN") {
            contentType(ContentType.Application.FormUrlEncoded)

            setBody(FormDataContent(Parameters.build {
                append("grant_type", GRANT_TYPE)
                append("code", code)
                append("client_id", clientId ?: "")
                append("client_secret", clientSecret ?: "")
            }))
        }

        return when (response.status.value) {
            200 -> response.body<YandexTokenBody>()
            else -> null
        }
    }
}