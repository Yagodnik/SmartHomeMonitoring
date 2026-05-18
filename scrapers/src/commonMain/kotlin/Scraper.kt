import models.Metric

interface Scraper {
    suspend fun scrape(): List<Metric>
}