package models

sealed interface ResultOrError<out T, out U> {
    data class Success<out T>(val data: T) : ResultOrError<T, Nothing>

    data class Error<out U>(val error: U) : ResultOrError<Nothing, U>
}