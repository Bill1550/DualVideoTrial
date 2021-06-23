buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.kotlin}")
        classpath("com.android.tools.build:gradle:${Versions.Android.gradle_plugin}")
    }
}

group = "com.loneoaktech.tests"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
//        jcenter() // TODO remove (deprecated) needed for relinker used by twilio
    }
}