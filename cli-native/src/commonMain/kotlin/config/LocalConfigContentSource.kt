package config

import io.ktor.utils.io.readText
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class LocalConfigContentSource(
    configPath: String,
) : ConfigContentSource {
    private val path = resolveRealPath(configPath)

    override fun getContent(): String? {
        println(path)

        if (!SystemFileSystem.exists(path)) {
            return null
        }

        return SystemFileSystem.source(path).buffered().use {
            it.readText()
        }
    }

    private fun resolveRealPath(path: String): Path = if (path.startsWith("~")) {
        val relative= path
            .removePrefix("~")
            .removePrefix("/")
            .removePrefix("\\")
        Path("", relative)
    } else {
        Path(path)
    }
}