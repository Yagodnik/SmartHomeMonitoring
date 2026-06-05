import com.github.ajalt.clikt.core.main
import com.github.ajalt.mordant.terminal.Terminal
import commands.YandexMonitoringApplication

fun main(args: Array<String>) {
    val terminal = Terminal()

    YandexMonitoringApplication(terminal)
        .main(args)
}
