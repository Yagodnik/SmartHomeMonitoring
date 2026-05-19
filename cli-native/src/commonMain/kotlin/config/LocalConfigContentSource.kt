package config

import io.ktor.utils.io.readText
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class LocalConfigContentSource(
    configPath: String,
) : ConfigContentSource {
    private val path = Path(configPath)

    override fun getContent(): String? {
        if (!SystemFileSystem.exists(path)) {
            return null
        }

        return SystemFileSystem.source(path).buffered().use {
            it.readText()
        }
    }
}