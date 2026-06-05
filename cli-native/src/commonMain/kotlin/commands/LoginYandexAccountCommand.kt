package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import kotlinx.coroutines.runBlocking
import services.AccountService
import utils.QrCodeAscii.encodeAsQrCode

class LoginYandexAccountCommand(
    private val accountService: AccountService,
) : CliktCommand("login-yandex") {
    companion object {
        const val AUTH_ATTEMPTS_LIMIT = 3
    }

    override fun run() {
        runBlocking {
            val session = accountService.createAuthSession()

            val verificationUrl = session.requestAuthUrl()
            if (verificationUrl == null) {
                terminal.println(TextColors.red("Failed to receive authorization url!"))
                return@runBlocking
            }

            val lines = encodeAsQrCode(verificationUrl)
            terminal.println(lines)

            terminal.println("" +
                    "Visit link: " +
                    TextStyles.italic(TextColors.brightBlue(verificationUrl)) +
                    " or scan " +
                    TextStyles.italic(TextColors.brightBlue("qr code")) +
                    " above")

            for (i in 1..AUTH_ATTEMPTS_LIMIT) {
                terminal.print("Enter code: ")
                val secretCode = terminal.readLineOrNull(false)
                secretCode?.let {
                    terminal.println("Received: $it")
                }

                val tokens = session.exchangeForToken(secretCode ?: "")

                if (tokens == null) {
                    terminal.println(TextColors.red("" +
                            "Failed to login. Try again. " +
                            "You have ${AUTH_ATTEMPTS_LIMIT - i} attempts"))
                } else {
                    terminal.println(TextColors.green("" +
                            "Successfully logged into the Yandex Account"))

                    accountService.saveOAuthToken(tokens)
                    return@runBlocking
                }
            }

            terminal.println(TextColors.red("Failed to login"))
        }
    }
}