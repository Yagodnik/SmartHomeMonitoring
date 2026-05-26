package services

import domain.MonitoringStartResult

interface MonitoringService {
    suspend fun start() : MonitoringStartResult

    fun shutdown()
}