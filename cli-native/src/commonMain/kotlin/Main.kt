import app.AppServices
import com.github.ajalt.clikt.core.main
import com.github.ajalt.mordant.terminal.Terminal
import commands.YandexMonitoringApplication
import dev.scottpierce.envvar.EnvVar

fun main(args: Array<String>) {
    val loadedToken: String? = null
    val token = loadedToken ?: EnvVar["ACCESS_TOKEN"]
    val appServices = AppServices.createYandexServices(token ?: "")
    val terminal = Terminal()

    YandexMonitoringApplication(terminal, appServices).main(args)
}
