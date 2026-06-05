package models

sealed interface ScrapeResult {
    data class Success(val metrics: List<Metric>) : ScrapeResult

    data class Error(val error: String) : ScrapeResult
}