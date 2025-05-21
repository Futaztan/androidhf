plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.androidhf"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.androidhf"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.firebase.auth)


    // For Hilt Worker support
    implementation (libs.androidx.hilt.work)
    implementation(libs.firebase.firestore.ktx)
    kapt (libs.androidx.hilt.compiler)




    implementation(libs.androidx.room.common.jvm)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.hilt.common)
    ksp(libs.androidx.room.compiler)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.chart)
    implementation(libs.client.jvm)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.i18n)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.retrofit)
    implementation(libs.gson.converter)
    implementation(libs.ycharts)
    implementation(libs.compose.markdown)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation ("io.ktor:ktor-client-core:2.3.0")
    implementation ("io.ktor:ktor-client-android:2.3.0")
    implementation ("io.ktor:ktor-client-content-negotiation:2.3.0")
    implementation ("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
    implementation ("io.ktor:ktor-client-logging:2.3.0")
}
