package yandex

import kotlinx.serialization.json.*
import yandex.parsers.impl.ModeStateParser
import kotlin.test.*

class ModeStateParserTest {
    private val parser = ModeStateParser()

    @Test
    fun `parse returns MetricValue for valid cleanup_mode instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "cleanup_mode")
            put("value", "quick")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("cleanup_mode", result.name)
        assertEquals("quick", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid fan_speed instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "fan_speed")
            put("value", "medium")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("fan_speed", result.name)
        assertEquals("medium", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid thermostat instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "thermostat")
            put("value", "eco")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("thermostat", result.name)
        assertEquals("eco", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid program instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "program")
            put("value", "cotton")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("program", result.name)
        assertEquals("cotton", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid dishwashing_program instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "dishwashing_program")
            put("value", "auto")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("dishwashing_program", result.name)
        assertEquals("auto", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid coffee_mode instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "coffee_mode")
            put("value", "espresso")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("coffee_mode", result.name)
        assertEquals("espresso", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid tea_mode instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "tea_mode")
            put("value", "black")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("tea_mode", result.name)
        assertEquals("black", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid heat_mode instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "heat_mode")
            put("value", "turbo")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("heat_mode", result.name)
        assertEquals("turbo", result.value)
    }

    @Test
    fun `parse returns null when instance field is missing`() {
        // Arrange
        val state = buildJsonObject {
            put("value", "quick")
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
            put("instance", "cleanup_mode")
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
            put("value", "quick")
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
            put("instance", "cleanup_mode")
            put("value", buildJsonArray { add("quick") })
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
            put("value", "quick")
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
            put("instance", "cleanup_mode")
            put("value", JsonNull)
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNull(result)
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

    @Test
    fun `parse accepts numeric string values`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "fan_speed")
            put("value", "3")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("fan_speed", result.name)
        assertEquals("3", result.value)
    }

    @Test
    fun `parse accepts custom mode values`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "custom_mode")
            put("value", "my_custom_value")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("custom_mode", result.name)
        assertEquals("my_custom_value", result.value)
    }
}