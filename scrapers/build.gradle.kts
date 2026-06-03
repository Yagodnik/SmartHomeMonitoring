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

    jvm()
//    linuxArm64()
//    linuxX64()
//    mingwX64()
    macosArm64()
    macosX64()

    sourceSets {
        commonMain.dependencies {
            api(libs.ktorClientCore)
            api(libs.ktorSerializationJson)
            api(libs.ktorClientContentNegotiation)
            api(libs.ktorClientAuth)
            api(libs.kotlinxSerializationJson)
            implementation(libs.kotlinEnvVar)
            implementation(libs.ksafe)
            implementation(libs.kotlinCryptoSha2)
            implementation(libs.kotlinCryptoSecureRandom)
        }

        commonTest.dependencies {
            implementation(libs.kotlinTest)
            implementation(libs.ktorClientMock)
        }

        jvmTest.dependencies {
            runtimeOnly("org.junit.platform:junit-platform-launcher")
        }

        jvmMain.dependencies    { implementation(libs.ktorClientCio) }
        linuxMain.dependencies  { implementation(libs.ktorClientCurl) }
        mingwMain.dependencies  { implementation(libs.ktorClientWinhttp) }
        macosMain.dependencies  { implementation(libs.ktorClientDarwin) }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel"){}