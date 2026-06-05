package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import services.AccountService

class LogoutYandexAccountCommand(
    private val accountService: AccountService,
) : CliktCommand("logout-yandex") {
    override fun run() {
        accountService.deleteAuthData()

        terminal.println("Credentials were deleted")
    }
}