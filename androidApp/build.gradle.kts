import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream
import com.android.build.gradle.internal.dsl.DefaultConfig

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
}

group = "com.loneoaktech.apps"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.loneoaktech.apps.androidApp"
        minSdkVersion(24)
        targetSdkVersion(30)
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
}

fun readProperties(fileName: String): Map<String,String> {
    val props = Properties()
    val propsFile = rootProject.file(fileName)
    props.load(FileInputStream(propsFile))
    return props.map { entry -> entry.key.toString() to entry.value.toString() }.associate { it }
}

fun Map<String,String>.getFieldValue( name: String): String =
    "\"${this[name]?: ""}\""

fun DefaultConfig.buildConfigStringFields( map: Map<String,String>) {
    map.forEach { (key, value) ->
        buildConfigField("String", key, "\"$value\"")
    }

}