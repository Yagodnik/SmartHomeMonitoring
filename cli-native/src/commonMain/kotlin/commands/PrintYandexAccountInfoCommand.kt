package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.table
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import services.AccountService
import kotlin.time.Duration.Companion.seconds

class PrintYandexAccountInfoCommand(
    private val accountService: AccountService,
) : CliktCommand("yandex-account") {
    companion object {
        val DEFAULT_TIMEOUT = 5.seconds
    }

    override fun run() {
        runBlocking {
            val account = try {
                withTimeout(DEFAULT_TIMEOUT) {
                    accountService.getAccount()
                }
            } catch (_: TimeoutCancellationException) {
                terminal.println(TextColors.red("Timeout: failed to receive account info"))
                return@runBlocking
            }

            val header = table {
                header { row("Yandex Account") }
                body {
                    row(account.username)
                    row(account.email)
                }
            }

            terminal.println(header)
            terminal.println()
        }
    }
}