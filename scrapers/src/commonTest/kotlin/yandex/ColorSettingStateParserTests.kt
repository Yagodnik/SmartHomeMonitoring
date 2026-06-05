package yandex

import kotlinx.serialization.json.*
import kotlinx.serialization.json.put
import yandex.parsers.impl.ColorSettingStateParser
import kotlin.test.*

class ColorSettingStateParserTests {
    private val parser = ColorSettingStateParser()

    @Test
    fun `parse HSV with valid values`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_HSV)
            putJsonObject("value") {
                put("h", 255)
                put("s", 100)
                put("v", 50)
            }
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
        assertEquals(ColorSettingStateParser.INSTANCE_HSV, result.name)
        assertEquals("25510050", result.rawValue)
    }

    @Test
    fun `parse HSV with out-of-range hue returns null`() {
        // Assert
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_HSV)
            putJsonObject("value") {
                put("h", 400)
                put("s", 50)
                put("v", 50)
            }
        }

        // Act
        val result = parser.parse(json)

        // Asset
        assertNull(result)
    }

    @Test
    fun `parse HSV with missing field returns null`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_HSV)
            putJsonObject("value") {
                put("h", 180)
            }
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse HSV when value is not JsonObject returns null`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_HSV)
            put("value", "not-an-object")
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse RGB with valid integer value`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_RGB)
            put("value", 13910520)
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
        assertEquals(ColorSettingStateParser.INSTANCE_RGB, result.name)
        assertEquals("13910520", result.rawValue)
    }

    @Test
    fun `parse RGB with string value returns null`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_RGB)
            put("value", "13910520")
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `parse RGB with negative value returns null if validated`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_RGB)
            put("value", -1)
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `parse temperature_k with valid value`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_TEMPERATURE_K)
            put("value", 4500)
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
        assertEquals("4500", result.rawValue)
    }

    @Test
    fun `parse temperature_k with out-of-range value`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_TEMPERATURE_K)
            put("value", 100)
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
    }


    @Test
    fun `parse scene with valid scene id`() {
        // Arrange
        val validScenes = listOf("alarm", "alice", "candle", "party", "reading")

        // Act
        validScenes.forEach { scene ->
            val json = buildJsonObject {
                put("instance", ColorSettingStateParser.INSTANCE_SCENE)
                put("value", scene)
            }

            val result = parser.parse(json)

            // Assert
            assertNotNull(result, "Failed for scene: $scene")
            assertEquals(scene, result.rawValue)
        }
    }

    @Test
    fun `parse scene with unknown scene id still returns value`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_SCENE)
            put("value", "unknown_scene")
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
        assertEquals("unknown_scene", result.rawValue)
    }

    @Test
    fun `parse when instance field is missing returns null`() {
        // arrange
        val json = buildJsonObject {
            put("value", 12345)
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse when value field is missing returns null`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", "rgb")
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse when instance is unknown returns null`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", "unknown_instance")
            put("value", "test")
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse when value is JsonNull returns null`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", ColorSettingStateParser.INSTANCE_RGB)
            put("value", JsonNull)
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse when instance is not a primitive returns null`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", buildJsonObject { put("nested", "object") })
            put("value", 123)
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNull(result)
    }

    @Test
    fun `parse real HSV example from documentation`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", "hsv")
            putJsonObject("value") {
                put("h", 255)
                put("s", 100)
                put("v", 50)
            }
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
        assertEquals("hsv", result.name)
        assertEquals("25510050", result.rawValue)
    }

    @Test
    fun `parse real RGB example from documentation`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", "rgb")
            put("value", 13910520)
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
        assertEquals("13910520", result.rawValue)
    }

    @Test
    fun `parse real temperature_k example from documentation`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", "temperature_k")
            put("value", 4500)
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
        assertEquals("4500", result.rawValue)
    }

    @Test
    fun `parse real scene example from documentation`() {
        // Arrange
        val json = buildJsonObject {
            put("instance", "scene")
            put("value", "party")
        }

        // Act
        val result = parser.parse(json)

        // Assert
        assertNotNull(result)
        assertEquals("party", result.rawValue)
    }
}