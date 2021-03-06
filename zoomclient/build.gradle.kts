configurations.create("default")
artifacts.add("default", file("mobilertc.aar"))

dependencies.add("default","androidx.security:security-crypto:1.1.0-alpha02")
dependencies.add("default","com.google.crypto.tink:tink-android:1.5.0")
dependencies.add("default","androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")

dependencies.add("default","androidx.appcompat:appcompat:1.0.0")
dependencies.add("default","androidx.constraintlayout:constraintlayout:1.1.3")
dependencies.add("default","com.google.android.material:material:${Versions.Google.android_material}")
//dependencies.add("default","com.google.android:flexbox:2.0.1")
dependencies.add("default","com.google.android.flexbox:flexbox:${Versions.Google.flexbox}")
dependencies.add("default","androidx.multidex:multidex:2.0.0")

