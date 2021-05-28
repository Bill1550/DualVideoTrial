plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    kotlin( "plugin.serialization" ) version (Versions.Kotlin.kotlin)
}

group = "com.loneoaktech.tests"
version = "1.0-SNAPSHOT"

kotlin {
    android()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api( "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}" )
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.Kotlin.serialization}" )
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.material:material:${Versions.Google.android_material}")
                api("com.jakewharton.timber:timber:${Versions.JakeWharton.timber}")

            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
    }
}

android {
    compileSdkVersion(Versions.Android.compile_sdk)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(Versions.Android.min_sdk)
        targetSdkVersion(Versions.Android.target_sdk)
    }
    buildFeatures {
        viewBinding = true
    }
}