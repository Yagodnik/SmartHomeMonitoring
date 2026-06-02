import app.AppServices
import com.github.ajalt.clikt.core.main
import com.github.ajalt.mordant.terminal.Terminal
import commands.YandexMonitoringApplication

fun main(args: Array<String>) {
    val appServices = AppServices.createYandexServices()
    val terminal = Terminal()

    YandexMonitoringApplication(terminal, appServices)
        .main(args)
}
