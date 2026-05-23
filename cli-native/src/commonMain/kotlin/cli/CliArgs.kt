package cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import printer.BasicPrinter
import printer.ColoredPrinter
import printer.Printer

enum class ColorMode { ENABLED, DISABLED }

data class CliConfig(
    val configPath: String,
    val printer: Printer,
)

object CliDefaults {
    const val DEFAULT_CONFIG_PATH = "config.yaml"
}

fun parseCliArgs(args: Array<String>): CliConfig {
    val parser = ArgParser("smart-home-monitor")

    val configPath by parser.option(
        ArgType.String,
        shortName = "c",
        fullName = "config",
        description = "Path to configuration file"
    )

    val colorMode by parser.option(
        ArgType.Choice<ColorMode>(),
        fullName = "colored",
        description = "Enable colored terminal output"
    ).default(ColorMode.DISABLED)

    parser.parse(args)

    val printer = when (colorMode) {
        ColorMode.ENABLED -> ColoredPrinter()
        ColorMode.DISABLED -> BasicPrinter()
    }

    return CliConfig(configPath ?: CliDefaults.DEFAULT_CONFIG_PATH, printer)
}