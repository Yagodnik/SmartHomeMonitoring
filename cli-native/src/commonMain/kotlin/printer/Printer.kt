package printer

interface Printer {
    fun print(string: String, fg: Color? = null, bg: Color? = null)
    fun println(string: String, fg: Color? = null, bg: Color? = null)
}