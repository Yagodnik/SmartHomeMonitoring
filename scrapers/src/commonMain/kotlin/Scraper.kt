import models.Metric

interface Scraper {
    fun scrape(): List<Metric>
}