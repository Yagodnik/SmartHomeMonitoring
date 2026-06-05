package yandex.internal

import SecretsStorage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.AttributeKey
import io.ktor.util.logging.Logger
import kotlinx.serialization.json.Json
import models.OAuth2Token
import models.ResultOrError
import yandex.models.*
import yandex.utils.asYandexError
import yandex.utils.generatePkce

class KtorInternalYandexApi(
    private val secretsStorage: SecretsStorage,
    private val clientId: String,
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        install(Logging) { level = LogLevel.NONE }

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
                    val response = client.post("https://oauth.yandex.ru/token") {
                        contentType(ContentType.Application.FormUrlEncoded)

                        setBody(FormDataContent(Parameters.build {
                            append("grant_type", "refresh_token")
                            append("client_id", clientId)
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

                sendWithoutRequest { request ->
                    val shouldSkip = request.attributes.getOrNull(SkipAuthKey) ?: false
                    !shouldSkip
                }
            }
        }
    },
) : InternalYandexApi {
    companion object {
        private const val AUTH_BASE_URL = "https://oauth.yandex.ru"
        private const val BASE_URL = "https://api.iot.yandex.net/v1.0"
        private const val ACCOUNT_BASE_URL = "https://login.yandex.ru/info"
        private const val OAUTH_AUTHORIZE_URL = "https://oauth.yandex.ru/authorize"
        private const val REDIRECT_URL = "https://oauth.yandex.ru/verification_code"
        private const val CODE_CHALLENGE_METHOD = "S256"

        private const val USER_INFO = "/user/info"
        private const val EXCHANGE_FOR_TOKEN = "/token"
        private const val GRANT_TYPE = "authorization_code"

        private val SkipAuthKey = AttributeKey<Boolean>("SkipAuthKey")
    }

    override suspend fun generateAuthUrl(): YandexAuthData {
        val (verifier, challenge) = generatePkce()

        val url = URLBuilder(OAUTH_AUTHORIZE_URL).apply {
            parameters.append("response_type", "code")
            parameters.append("client_id", clientId)
            parameters.append("code_challenge", challenge)
            parameters.append("code_challenge_method", CODE_CHALLENGE_METHOD)
        }.buildString()

        return YandexAuthData(url, verifier, challenge)
    }

    override suspend fun exchangeForOAuthToken(code: String, dto: YandexAuthData)
        : ResultOrError<OAuth2Token, YandexError>
    {
        val response = httpClient.post("$AUTH_BASE_URL$EXCHANGE_FOR_TOKEN") {
            attributes.put(SkipAuthKey, true)

            contentType(ContentType.Application.FormUrlEncoded)

            setBody(FormDataContent(Parameters.build {
                append("grant_type", GRANT_TYPE)
                append("code", code)
                append("client_id", clientId)
                append("code_verifier", dto.verifier)
                append("redirect_uri", REDIRECT_URL)
            }))
        }

        return when (response.status) {
            HttpStatusCode.OK -> {
                val body = response.body<YandexTokenBody>()
                val tokens = OAuth2Token(body.accessToken, body.refreshToken)
                ResultOrError.Success(tokens)
            }
            else -> runCatching { response.body<YandexError>() }
                .map { ResultOrError.Error(it) }
                .getOrElse { ResultOrError.Error(response.asYandexError()) }
        }
    }

    override suspend fun queryUserInfo() : ResultOrError<YandexUserInfo, YandexError> {
        val response = httpClient.get("$BASE_URL$USER_INFO")

        return when (response.status) {
            HttpStatusCode.OK -> ResultOrError.Success(response.body<YandexUserInfo>())
            else -> runCatching { response.body<YandexError>() }
                .map { ResultOrError.Error(it) }
                .getOrElse { ResultOrError.Error(response.asYandexError()) }
        }
    }

    override suspend fun getAccountInfo(): ResultOrError<YandexAccountInfo, YandexError> {
        val response = httpClient.get(ACCOUNT_BASE_URL)

        return when (response.status) {
            HttpStatusCode.OK -> ResultOrError.Success(response.body<YandexAccountInfo>())
            else -> runCatching { response.body<YandexError>() }
                .map { ResultOrError.Error(it) }
                .getOrElse { ResultOrError.Error(response.asYandexError()) }
        }
    }

    override fun close() = httpClient.close()
}