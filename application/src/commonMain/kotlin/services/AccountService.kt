package services

import models.Account
import models.OAuth2Token

interface AccountService {
    suspend fun getAccount() : Account

    fun createAuthSession() : AuthSession

    fun saveOAuthToken(tokens: OAuth2Token)
}