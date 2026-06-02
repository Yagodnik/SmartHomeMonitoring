package yandex.internal

import SecretsStorage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import models.OAuth2Token
import models.ResultOrError
import yandex.models.YandexAccountInfo
import yandex.models.YandexDeviceCodeBody
import yandex.models.YandexError
import yandex.models.YandexTokenBody
import yandex.models.YandexUserInfo
import yandex.utils.asYandexError
import kotlin.time.Duration.Companion.seconds

class KtorInternalYandexApi(
    private val secretsStorage: SecretsStorage,
    private val clientId: String? = null,
    private val clientSecret: String? = null,
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val accessToken = secretsStorage.getSecret("YANDEX_ACCESS_TOKEN")?.takeIf { it.isNotBlank() }
                    val refreshToken = secretsStorage.getSecret("YANDEX_REFRESH_TOKEN")?.takeIf { it.isNotBlank() }

                    if (accessToken != null) {
                        BearerTokens(accessToken, refreshToken ?: "")
                    } else {
                        null
                    }
                }

                refreshTokens {
                    val response = this.client.post("https://oauth.yandex.ru/token") {
                        contentType(ContentType.Application.FormUrlEncoded)

                        setBody(FormDataContent(Parameters.build {
                            append("grant_type", "refresh_token")
                            append("client_id", clientId ?: "")
                            append("client_secret", clientSecret ?: "")
                            append("refresh_token", oldTokens?.refreshToken ?: "")
                        }))
                    }

                    when (response.status) {
                        HttpStatusCode.OK -> {
                            val newTokens = response.body<YandexTokenBody>()

                            secretsStorage.saveSecret("YANDEX_ACCESS_TOKEN", newTokens.accessToken)
                            secretsStorage.saveSecret("YANDEX_REFRESH_TOKEN", newTokens.refreshToken)

                            BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                        }
                        HttpStatusCode.BadRequest -> {
                            response.body<YandexError>().let {
                                if (it.error == "invalid_grant") {
                                    secretsStorage.saveSecret("YANDEX_ACCESS_TOKEN", "")
                                    secretsStorage.saveSecret("YANDEX_REFRESH_TOKEN", "")
                                }
                            }

                            null
                        }
                        else -> null
                    }
                }
            }
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

    override suspend fun requestCode(): ResultOrError<YandexDeviceCodeBody, YandexError> {
        val response = client.post("$AUTH_BASE_URL$REQUEST_CODE") {
            headers.remove(HttpHeaders.Authorization)

            contentType(ContentType.Application.FormUrlEncoded)

            setBody(FormDataContent(Parameters.build {
                append("client_id", clientId ?: "")
                append("scope", AUTH_SCOPE)
            }))
        }

        return when (response.status) {
            HttpStatusCode.OK -> ResultOrError.Success(response.body<YandexDeviceCodeBody>())
            else -> runCatching { response.body<YandexError>() }
                .map { ResultOrError.Error(it) }
                .getOrElse { ResultOrError.Error(response.asYandexError()) }
        }
    }

    override suspend fun exchangeForOAuthToken(deviceCodeDto: YandexDeviceCodeBody)
        : ResultOrError<OAuth2Token, YandexError>
    {
        val requestsCount = deviceCodeDto.expiresIn / deviceCodeDto.interval
        val interval = deviceCodeDto.interval

        (1..requestsCount).forEach { _ ->
            when (val result = exchangeForToken(deviceCodeDto.deviceCode)) {
                is ResultOrError.Success -> {
                    val token = OAuth2Token(result.data.accessToken, result.data.refreshToken)
                    return ResultOrError.Success(token)
                }
                else -> {}
            }

            delay(interval.seconds)
        }

        val error = YandexError(
            "Whole time interval $interval exceeded without successful authorization",
            "Timeout exceeded"
        )
        return ResultOrError.Error(error)
    }

    override suspend fun queryUserInfo() : ResultOrError<YandexUserInfo, YandexError> {
        val response = client.get("$BASE_URL$USER_INFO")

        return when (response.status) {
            HttpStatusCode.OK -> ResultOrError.Success(response.body<YandexUserInfo>())
            else -> runCatching { response.body<YandexError>() }
                .map { ResultOrError.Error(it) }
                .getOrElse { ResultOrError.Error(response.asYandexError()) }
        }
    }

    override suspend fun getAccountInfo(): ResultOrError<YandexAccountInfo, YandexError> {
        val response = client.get(ACCOUNT_BASE_URL)

        return when (response.status) {
            HttpStatusCode.OK -> ResultOrError.Success(response.body<YandexAccountInfo>())
            else -> runCatching { response.body<YandexError>() }
                .map { ResultOrError.Error(it) }
                .getOrElse { ResultOrError.Error(response.asYandexError()) }
        }
    }

    private suspend fun exchangeForToken(code: String) : ResultOrError<YandexTokenBody, YandexError> {
        val response = client.post("$AUTH_BASE_URL$EXCHANGE_FOR_TOKEN") {
            headers.remove(HttpHeaders.Authorization)

            contentType(ContentType.Application.FormUrlEncoded)

            setBody(FormDataContent(Parameters.build {
                append("grant_type", GRANT_TYPE)
                append("code", code)
                append("client_id", clientId ?: "")
                append("client_secret", clientSecret ?: "")
            }))
        }

        return when (response.status) {
            HttpStatusCode.OK -> ResultOrError.Success(response.body<YandexTokenBody>())
            else -> runCatching { response.body<YandexError>() }
                .map { ResultOrError.Error(it) }
                .getOrElse { ResultOrError.Error(response.asYandexError()) }
        }
    }
}