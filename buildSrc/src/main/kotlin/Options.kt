import org.gradle.api.JavaVersion

object Options {

    val freeCompilerArgs  = listOf(
        "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi",
//        "-Xuse-experimental=kotlinx.serialization.ImplicitReflectionSerializer",
        "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
        "-Xopt-in=kotlin.ExperimentalUnsignedTypes",
        "-Xopt-in=kotlin.time.ExperimentalTime"
//        "-XXLanguage:+InlineClasses"
    )

    val javaVersion = JavaVersion.VERSION_1_8
}
