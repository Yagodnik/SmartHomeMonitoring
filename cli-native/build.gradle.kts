import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

group = "ru.yagodnik"
version = "1.0-SNAPSHOT"

dependencies {
    commonMainImplementation(project(":scrapers"))
    commonMainImplementation(project(":application"))

    commonMainImplementation(libs.kotlinxCli)
    commonMainImplementation(libs.yamlkt)
    commonMainImplementation(libs.kotlinxIoCore)
    commonMainImplementation(libs.kotlinEnvVar)
    commonMainImplementation(libs.kotlinxDatetime)
    commonMainImplementation(libs.ktorServerCore)
    commonMainImplementation(libs.ktorServerCio)
}

kotlin {
    macosArm64()
    macosX64()
    linuxX64()
    mingwX64()

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
}