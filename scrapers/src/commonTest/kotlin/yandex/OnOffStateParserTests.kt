package yandex

import kotlinx.serialization.json.*
import yandex.parsers.impl.OnOffStateParser
import kotlin.test.*

class OnOffStateParserTests {
    private val parser = OnOffStateParser()

    @Test
    fun `parse returns MetricValue when instance and value are valid primitives`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "on_off")
            put("value", "on")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("on_off", result.name)
        assertEquals("on", result.rawValue)
    }

    @Test
    fun `parse returns MetricValue for off state`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "on_off")
            put("value", "off")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("on_off", result.name)
        assertEquals("off", result.rawValue)
    }

    @Test
    fun `parse returns null when instance field is missing`() {
        // Arrange
        val state = buildJsonObject {
            put("value", "on")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse returns null when value field is missing`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "on_off")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse returns null when instance is not a primitive`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", buildJsonObject { put("nested", "value") })
            put("value", "on")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse returns null when value is not a primitive`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "on_off")
            put("value", buildJsonArray { add("on") })
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse returns null when instance is JsonNull`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", JsonNull)
            put("value", "on")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse returns null when value is JsonNull`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "on_off")
            put("value", JsonNull)
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse accepts boolean-like string values`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "on_off")
            put("value", "true")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("on_off", result.name)
        assertEquals("true", result.rawValue)
    }

    @Test
    fun `parse accepts numeric string values`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "on_off")
            put("value", "1")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("on_off", result.name)
        assertEquals("1", result.rawValue)
    }

    @Test
    fun `parse returns null when state is empty`() {
        // Arrange
        val state = buildJsonObject {}

        // Act
        val result = parser.parse(state)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse returns null when both fields are non-primitive`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", buildJsonArray {})
            put("value", buildJsonObject {})
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNull(result)
    }
}