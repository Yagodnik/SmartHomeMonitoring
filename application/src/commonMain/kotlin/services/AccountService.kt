package services

import models.Account

interface AccountService {
    suspend fun getAccount() : Account

    fun createAuthSession() : AuthSession
}