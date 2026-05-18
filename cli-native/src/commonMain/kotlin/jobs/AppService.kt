package jobs

import kotlinx.coroutines.CoroutineScope

interface AppService {
    fun launchIn(scope: CoroutineScope)
    suspend fun stop()
}