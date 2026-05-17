package models

data class Metric(
    val deviceId: String,
    val deviceName: String,
    val value: MetricValue,
)
