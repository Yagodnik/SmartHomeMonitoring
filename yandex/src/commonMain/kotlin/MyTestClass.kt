import kotlinx.serialization.Serializable

@Serializable
data class InputData(
    val a: Int,
    val b: Int,
)

class MyTestClass(
    private val a: Int,
    private val b: Int,
) {
    fun sum(input: InputData): Int = input.a + input.b
}