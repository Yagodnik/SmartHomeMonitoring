package config

interface ConfigReader {
    fun isReady(): Boolean

    fun listExporters(): List<ExporterParams>

    fun getPollingInterval(): Long?
}