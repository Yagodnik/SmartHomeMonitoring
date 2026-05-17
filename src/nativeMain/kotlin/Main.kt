import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class Message(
    val topic: String,
    val content: String,
)

private val PrettyPrintJson = Json {
    prettyPrint = true
}

fun main() {
    val message = Message(
        topic = "Kotlin/Native",
        content = "Hello!"
    )

    val red = "\u001b[31m"
    val reset = "\u001b[0m"

    println(red + PrettyPrintJson.encodeToString(message) + reset)
}
