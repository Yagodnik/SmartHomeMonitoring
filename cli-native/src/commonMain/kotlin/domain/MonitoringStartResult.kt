package domain

sealed interface MonitoringStartResult {
    object Success : MonitoringStartResult
    data class Failure(val message: String = "") : MonitoringStartResult
}
