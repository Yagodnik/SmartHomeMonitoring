import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
    macosArm64()
    macosX64()
    linuxX64()
    mingwX64()

//    linuxArm64 {
//        binaries {
//            executable {
//                entryPoint = "main"
//
//                val libgccAarch64 = fileTree("/usr/lib/gcc-cross/aarch64-linux-gnu") {
//                    include("*/libgcc.a")
//                }.files.maxByOrNull { it.parentFile.name }
//                if (libgccAarch64 != null) {
//                    binaries.all {
//                        linkerOpts(libgccAarch64.absolutePath)
//                    }
//                }
//            }
//        }
//    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":scrapers"))
            implementation(project(":application"))
            implementation(libs.yamlkt)
            implementation(libs.kotlinxIoCore)
            implementation(libs.kotlinEnvVar)
            implementation(libs.kotlinxDatetime)
            implementation(libs.ktorServerCore)
            implementation(libs.ktorServerCio)
            implementation(libs.clikt)
            implementation(libs.mordant)
        }

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

tasks.named<Jar>("jvmJar") {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }

    from(configurations.getByName("jvmRuntimeClasspath").map {
        if (it.isDirectory) it else zipTree(it)
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}