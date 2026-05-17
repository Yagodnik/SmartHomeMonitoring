package yandex

import kotlinx.serialization.json.*
import yandex.parsers.impl.RangeStateParser
import kotlin.test.*

class RangeStateParserTest {
    private val parser = RangeStateParser()

    @Test
    fun `parse returns MetricValue for valid brightness instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "brightness")
            put("value", "75")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("brightness", result.name)
        assertEquals("75", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid volume instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "volume")
            put("value", "30")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("volume", result.name)
        assertEquals("30", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid temperature instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "temperature")
            put("value", "22")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("temperature", result.name)
        assertEquals("22", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid humidity instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "humidity")
            put("value", "60")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("humidity", result.name)
        assertEquals("60", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid channel instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "channel")
            put("value", "5")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("channel", result.name)
        assertEquals("5", result.value)
    }

    @Test
    fun `parse returns MetricValue for valid fan_speed instance`() {
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
    fun `parse returns MetricValue for valid brightness_k instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "brightness_k")
            put("value", "4500")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("brightness_k", result.name)
        assertEquals("4500", result.value)
    }

    @Test
    fun `parse returns MetricValue for zero value`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "brightness")
            put("value", "0")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("brightness", result.name)
        assertEquals("0", result.value)
    }

    @Test
    fun `parse returns MetricValue for max value`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "brightness")
            put("value", "100")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("brightness", result.name)
        assertEquals("100", result.value)
    }

    @Test
    fun `parse returns MetricValue for decimal value`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "temperature")
            put("value", "21.5")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("temperature", result.name)
        assertEquals("21.5", result.value)
    }

    @Test
    fun `parse returns null when instance field is missing`() {
        // Arrange
        val state = buildJsonObject {
            put("value", "50")
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
            put("instance", "brightness")
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
            put("value", "50")
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
            put("instance", "brightness")
            put("value", buildJsonArray { add(50) })
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
            put("value", "50")
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
            put("instance", "brightness")
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
    fun `parse accepts negative temperature values`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "temperature")
            put("value", "-5")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("temperature", result.name)
        assertEquals("-5", result.value)
    }

    @Test
    fun `parse accepts custom range instance`() {
        // Arrange
        val state = buildJsonObject {
            put("instance", "custom_range")
            put("value", "123")
        }

        // Act
        val result = parser.parse(state)

        // Assert
        assertNotNull(result)
        assertEquals("custom_range", result.name)
        assertEquals("123", result.value)
    }
}