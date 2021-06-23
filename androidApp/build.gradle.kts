import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream
import com.android.build.gradle.internal.dsl.DefaultConfig

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version (Versions.Kotlin.kotlin)
}

group = "com.loneoaktech.tests"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":shared"))
    implementation(project(":zoomclient"))
    implementation(project(":zoomcommonlib"))

    implementation("com.twilio:video-android:6.3.0")

    implementation("androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraint_layout}")
//    implementation("com.google.android.material:material:${Versions.Google.android_material}")
//    implementation("androidx.appcompat:appcompat:${Versions.AndroidX.appcompat}")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.AndroidX.lifecycle}")
//    implementation("androidx.core:core-ktx:${Versions.AndroidX.core_ktx}")
//    implementation("androidx.activity:activity-ktx:${Versions.AndroidX.activity_ktx}")
//    implementation("androidx.fragment:fragment-ktx:${Versions.AndroidX.fragment_ktx}")

    implementation("com.twilio.sdk:twilio:8.13.0")
}

android {
    compileSdkVersion(Versions.Android.compile_sdk)
    defaultConfig {
        applicationId = "com.loneoaktech.test.dualVideoTrial"
        minSdkVersion(Versions.Android.min_sdk)
        targetSdkVersion(Versions.Android.target_sdk)
        versionCode = 1
        versionName = "1.0"

        val keysProps = readProperties("keys.properties")
        buildConfigStringFields(keysProps)
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
//        incremental = true
        sourceCompatibility = Options.javaVersion
        targetCompatibility = Options.javaVersion
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
//        exclude("META-INF/LICENSE")
//        exclude("META-INF/LICENSE.txt")
//        exclude("META-INF/license.txt")
//        exclude("META-INF/NOTICE")
//        exclude("META-INF/NOTICE.txt")
//        exclude("META-INF/notice.txt")
//        exclude("META-INF/ASL2.0")
//        exclude("META-INF/*.kotlin_module")
    }
}

fun readProperties(fileName: String): Map<String, String> {
    val props = Properties()
    val propsFile = rootProject.file(fileName)
    props.load(FileInputStream(propsFile))
    return props.map { entry -> entry.key.toString() to entry.value.toString() }.associate { it }
}

fun Map<String, String>.getFieldValue(name: String): String =
    "\"${this[name] ?: ""}\""

fun DefaultConfig.buildConfigStringFields(map: Map<String, String>) {
    map.forEach { (key, value) ->
        buildConfigField("String", key, "\"$value\"")
    }

}