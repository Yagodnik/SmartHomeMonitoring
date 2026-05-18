package printer

class BasicPrinter : Printer {
    override fun print(string: String, fg: Color?, bg: Color?) = kotlin.io.print(string)

    override fun println(string: String, fg: Color?, bg: Color?) = kotlin.io.println(string)
}