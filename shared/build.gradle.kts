plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.example.taskconvertaiapp.shared"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "sharedKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                // Compose Multiplatform (cross-platform)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Material Icons Extended (cross-platform)
                implementation(libs.material.icons.extended)

                // Serialization (cross-platform)
                implementation(libs.kotlinx.serialization.json)

                // DataStore Preferences Core (cross-platform)
                implementation(libs.androidx.datastore.preferences.core)

                // Navigation Compose KMP (cross-platform)
                implementation(libs.androidx.navigation.compose.kmp)

                // Lifecycle ViewModel Compose KMP (cross-platform)
                implementation(libs.androidx.lifecycle.viewmodel.compose.kmp)

                // Coil3 for image loading (cross-platform)
                implementation(libs.coil3.compose)
                implementation(libs.coil3.network.ktor)

                // Ktor Client for networking (cross-platform alternative to Retrofit)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.

                // Android-specific Compose libraries
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.lifecycle.runtime.ktx)

                // DataStore Android implementation
                implementation(libs.androidx.datastore.preferences)

                // Ktor Client Android engine
                implementation(libs.ktor.client.android)

                // Retrofit - Android only (can coexist with Ktor or migrate later)
                implementation(libs.retrofit2.kotlinx.serialization.converter)
                implementation(libs.retrofit)
                implementation(libs.converter.gson)
                implementation(libs.gson)
                implementation(libs.okhttp)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP's default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.

                // Ktor Client Darwin engine (for iOS)
                implementation(libs.ktor.client.darwin)
            }
        }
    }

}