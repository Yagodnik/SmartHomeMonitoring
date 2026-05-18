package config

import io.ktor.utils.io.*
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml

class YamlConfigReader(
    configPath: String
) : ConfigReader {
    @Serializable
    data class YamlConfig(
        val exporters: Map<String, Map<String, String>>
    )

    private var config: YamlConfig? = null

    init {
        if (SystemFileSystem.exists(Path(configPath))) {
            val yamlContent = SystemFileSystem.source(Path(configPath)).buffered().use {
                it.readText()
            }

            config = Yaml().decodeFromString<YamlConfig>(yamlContent)
        }
    }

    override fun isReady(): Boolean = config != null

    override fun listExporters(): List<ExporterParams> {
        if (config == null) {
            return emptyList()
        }

        val result = mutableListOf<ExporterParams>()

        config?.exporters?.forEach { exporter ->
            val exporterName = exporter.key
            val exporterParams: MutableMap<String, String> = mutableMapOf()

            exporter.value.forEach { (key, value) ->
                exporterParams += key to value
            }

            val params = ExporterParams(exporterName, exporterParams)
            result.add(params)
        }

        return result
    }
}