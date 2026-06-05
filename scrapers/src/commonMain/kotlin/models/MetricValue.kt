package models

data class MetricValue(
    val name: String,
    val rawValue: String,
    val unit: String? = null,

    // Often monitoring software exposes only numeric values, so instead
    // of performing casting we will force scraper to do that instead of
    // prometheus or similar solutions
    val numericValue: Double? = null,
)
