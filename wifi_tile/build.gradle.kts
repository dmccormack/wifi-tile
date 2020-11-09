plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Android.compileSdk)
    defaultConfig {
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
        applicationId = Android.applicationId
        versionCode = Android.versionCode
        versionName = Android.versionName
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        map { it.java.srcDir("src/${it.name}/kotlin") }
    }
}

dependencies {
    implementation(kotlin(module = "stdlib", version = Versions.kotlinVersion))
    implementation(androidx(library = "appcompat", version = Versions.appCompatVersion))
}
