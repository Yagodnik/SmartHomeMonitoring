package yandex.utils

import io.ktor.client.statement.*
import yandex.models.YandexError

fun HttpResponse.asYandexError(): YandexError {
    // TODO: Add body conversion somehow
    return YandexError(status.description, status.toString())
}