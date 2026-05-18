import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

group = "ru.yagodnik"
version = "1.0-SNAPSHOT"

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    linuxX64()
    mingwX64()
    macosArm64()
    macosX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktorClientCore)
            implementation(libs.ktorClientCio)
            implementation(libs.ktorClientContentNegotiation)
            api(libs.kotlinxSerializationJson)
        }

        commonTest.dependencies {
            implementation(libs.kotlinTest)
            implementation(libs.ktorClientMock)
        }

        jvmTest.dependencies {
            runtimeOnly("org.junit.platform:junit-platform-launcher")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel"){}