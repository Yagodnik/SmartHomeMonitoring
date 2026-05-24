package utils

fun String.resolveTemplate(vars: Map<String, String>): String {
    val pattern = "\\{\\{\\s*([^}\\s]+)\\s*\\}\\}"

    return Regex(pattern).replace(this) { match ->
        val key = match.groupValues[1]
        vars[key] ?: match.value
    }
}