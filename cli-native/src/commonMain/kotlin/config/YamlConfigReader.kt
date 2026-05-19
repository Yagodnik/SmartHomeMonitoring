package config

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import net.mamoe.yamlkt.Yaml

class YamlConfigReader(
    configContentSource: ConfigContentSource
) : ConfigReader {
    @Serializable
    data class YamlConfig(
        val exporters: Map<String, Map<String, String>>
    )

    private var config: YamlConfig? = null

    init {
        configContentSource.getContent()?.let { content ->
            config = Yaml.decodeFromString<YamlConfig>(content)
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