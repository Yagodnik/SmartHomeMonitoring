import config.ConfigContentSource

class MockContentSource(
    private val content: String,
) : ConfigContentSource {
    override fun getContent(): String? = content
}