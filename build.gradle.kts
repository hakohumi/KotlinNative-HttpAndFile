plugins {
    kotlin("multiplatform") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    google()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        // 参考ページ:https://kotlinlang.org/docs/curl.html#generate-bindings
        val main by compilations.getting
        val libcurl by main.cinterops.creating
        binaries {
            executable {
                entryPoint = "jp.co.erii.nativeHttpAndFile.main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting {
            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
//                implementation("org.jetbrains.kotlinx:kotlinx-io-common:0.1.3")
//                implementation("org.jetbrains.kotlinx:kotlinx-io-native:0.1.16")
//                implementation("io.ktor:ktor-client-core:1.5.4")
                // https://ktor.io/docs/http-client-engines.html#desktop
//                implementation("io.ktor:ktor-client-curl:1.5.4")
//                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
            }
        }
        val nativeTest by getting {
            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
//                implementation("org.jetbrains.kotlinx:kotlinx-io-common:0.1.3")
//                implementation("org.jetbrains.kotlinx:kotlinx-io-native:0.1.3")
            }
        }
    }
}
