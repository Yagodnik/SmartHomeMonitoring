package config

interface ConfigContentSource {
    fun getContent(): String?
}