package services

import models.AuthData
import models.OAuth2Token

interface AuthSession {
    suspend fun requestUserCode(): AuthData?

    suspend fun exchangeForToken(): OAuth2Token?
}