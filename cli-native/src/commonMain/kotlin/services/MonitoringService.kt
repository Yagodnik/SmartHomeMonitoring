package services

import domain.MonitoringStartResult

interface MonitoringService {
    fun start() : MonitoringStartResult

    fun shutdown()
}