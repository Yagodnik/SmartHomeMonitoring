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
    override fun run() {
        runBlocking {
            val session = accountService.createAuthSession()

            val authData = session.requestUserCode()
            if (authData == null) {
                terminal.println(TextColors.red("Failed to receive user code!"))
                return@runBlocking
            }

            val lines = encodeAsQrCode(authData.verificationUrl)
            terminal.println(lines)

            terminal.println("Your user code is ${TextColors.brightBlue(authData.userCode)}")
            terminal.println("" +
                    "Visit link: " +
                    TextStyles.italic(TextColors.brightBlue(authData.verificationUrl)) +
                    " or scan " +
                    TextStyles.italic(TextColors.brightBlue("qr code")) +
                    " above")

            terminal.println(TextStyles.bold("Enter this code at the text input on the website!"))
            terminal.println(TextColors.brightBlue("Trying to login..."))

            val tokens = session.exchangeForToken()

            if (tokens == null) {
                terminal.println(TextColors.red("Failed to login"))
                return@runBlocking
            }

            terminal.println(TextColors.green("Successfully logged into the Yandex Account"))

            // Save token
            accountService.saveOAuthToken(tokens)
        }
    }
}