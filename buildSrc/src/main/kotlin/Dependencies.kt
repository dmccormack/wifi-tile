object Android {
    const val applicationId = "app.atebit.wifitile"
    const val versionCode = 1
    const val versionName = "1.0"

    const val minSdk = 24
    const val compileSdk = 30
    const val targetSdk = 30
}

object Versions {
    const val kotlinVersion = "1.4.10"
    const val buildToolsVersion = "4.1.0"
    const val appCompatVersion = "1.2.0"
}

fun androidx(library: String, artifact: String = library, version: String) =
    "androidx.$library:$artifact:$version"
