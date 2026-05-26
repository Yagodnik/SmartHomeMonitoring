import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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

    commonMainImplementation(libs.yamlkt)
    commonMainImplementation(libs.kotlinxIoCore)
    commonMainImplementation(libs.kotlinEnvVar)
    commonMainImplementation(libs.kotlinxDatetime)
    commonMainImplementation(libs.ktorServerCore)
    commonMainImplementation(libs.ktorServerCio)
    commonMainImplementation(libs.clikt)
    commonMainImplementation(libs.mordant)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jvm()
    macosArm64()
    macosX64()
    linuxArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlinTest)
            implementation(libs.ktorClientMock)
        }

        jvmTest.dependencies {
            runtimeOnly("org.junit.platform:junit-platform-launcher")
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
}