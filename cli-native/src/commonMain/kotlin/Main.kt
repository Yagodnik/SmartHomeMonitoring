import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
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

fun main(args: Array<String>) {
    val parser = ArgParser("Smart Home Monitoring")

    val name by parser.option(
        ArgType.String,
        shortName = "n",
        description = "User name"
    )

    parser.parse(args)

    println("Hello, $name!")

    val message = Message(
        topic = "Kotlin/Native",
        content = "Hello!"
    )

    val red = "\u001b[31m"
    val reset = "\u001b[0m"

    println(red + PrettyPrintJson.encodeToString(message) + reset)
}
