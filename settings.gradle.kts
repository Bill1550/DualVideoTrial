pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    
}
rootProject.name = "DualVideoTrial"


include(":androidApp")
include(":shared")
include(":zoomclient")
include(":zoomcommonlib")

