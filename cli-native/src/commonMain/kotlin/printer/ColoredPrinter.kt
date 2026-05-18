package printer

enum class Color(val fg: String, val bg: String) {
    BLACK("\u001b[30m", "\u001b[40m"),
    RED("\u001b[31m", "\u001b[41m"),
    GREEN("\u001b[32m", "\u001b[42m"),
    YELLOW("\u001b[33m", "\u001b[43m"),
    BLUE("\u001b[34m", "\u001b[44m"),
    MAGENTA("\u001b[35m", "\u001b[45m"),
    CYAN("\u001b[36m", "\u001b[46m"),
    WHITE("\u001b[37m", "\u001b[47m");

    companion object {
        const val RESET = "\u001b[0m"
    }
}

class ColoredPrinter : Printer {
    override fun print(string: String, fg: Color?, bg: Color?) {
        kotlin.io.print(wrap(string, fg, bg))
    }

    override fun println(string: String, fg: Color?, bg: Color?) {
        kotlin.io.println(wrap(string, fg, bg))
    }

    private fun wrap(text: String, fg: Color?, bg: Color?): String = buildString {
        append(fg?.fg ?: "")
        append(bg?.bg ?: "")
        append(text)
        if (fg != null || bg != null) append(Color.RESET)
    }
}