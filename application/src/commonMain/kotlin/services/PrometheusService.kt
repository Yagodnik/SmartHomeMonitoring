package services

import models.MetricsSnapshot

interface PrometheusService {
    fun formatToPrometheus(snapshot: MetricsSnapshot): String
}