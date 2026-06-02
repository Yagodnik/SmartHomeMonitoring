package secrets

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig

actual fun createKSafe(): KSafe {
    return KSafe(config = KSafeConfig(appNamespace = "ru.yagodnik.smarthomemonitoring"))
}