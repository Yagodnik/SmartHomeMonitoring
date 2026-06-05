package services

import models.OAuth2Token

interface AuthSession {
    suspend fun requestAuthUrl(): String?

    suspend fun exchangeForToken(userCode: String): OAuth2Token?
}