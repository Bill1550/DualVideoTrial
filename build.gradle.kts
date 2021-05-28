buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("com.android.tools.build:gradle:4.1.1")
    }
}

group = "com.loneoaktech.tests"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}