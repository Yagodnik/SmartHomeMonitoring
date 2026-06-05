import config.YamlConfigReader
import kotlin.test.Test
import kotlin.test.assertEquals

class YamlConfigReaderTests {
    private val source = MockContentSource("""
        exporters:
          csv:
            path: .
          prometheus:
            port: 9091
    """.trimIndent())
    private val configReader = YamlConfigReader(source)

    @Test
    fun `should list 2 exporters`() {
        // Act
        val exporters = configReader.listExporters()

        // Assert
        assertEquals(2, exporters.size)
    }
}