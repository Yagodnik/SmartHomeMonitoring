import models.ScrapeResult

interface Scraper {
    suspend fun scrape(): ScrapeResult
}