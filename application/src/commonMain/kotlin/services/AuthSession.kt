package services

import models.AuthData

interface AuthSession {
    suspend fun requestUserCode(): AuthData?

    suspend fun exchangeForToken(): String?
}