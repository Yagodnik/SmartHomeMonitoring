package services

import models.Account

interface AccountService {
    suspend fun getAccount() : Account

    suspend fun requestUserCode() : String?

    suspend fun exchangeForToken(): String?
}