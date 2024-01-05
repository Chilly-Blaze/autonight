val sdks: Pair<Int, Int> by rootProject.extra
val versions: Pair<String, Int> by rootProject.extra
val packageName: String by rootProject.extra

plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin)
}

android {
    namespace = packageName
    compileSdk = sdks.second

    defaultConfig {
        applicationId = packageName
        minSdk = sdks.first
        targetSdk = sdks.second
        versionName = versions.first
        versionCode = versions.second
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    buildFeatures {
        compose = true
        aidl = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.ktx.core)
    implementation(libs.ktx.runtime)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.viewmodel)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material3)
    implementation(libs.libsu.core)
    implementation(libs.libsu.service)
}