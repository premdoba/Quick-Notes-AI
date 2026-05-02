import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.quicknotes"
    compileSdk = 36

//    // ADD THIS LINE
//    compileSdkExtension = 19

    defaultConfig {
        applicationId = "com.example.quicknotes"
        minSdk = 24
        targetSdk = 36
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.generativeai)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // 1. For the UI (Jetpack Compose)
//    implementation("androidx.activity:activity-compose:1.9.0")
//
//    // 2. For creating/editing the PDF file
//    implementation("com.itextpdf:itext7-core:7.2.5")
//
//    // 3. For displaying the PDF to the user (Crucial!)
//    implementation("androidx.pdf:pdf-viewer:1.0.0-alpha18")

    // Material 3
    implementation ("androidx.compose.material3:material3:1.2.1")

    // Navigation Compose
    implementation ("androidx.navigation:navigation-compose:2.7.7")

    // Room Database
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    //ml kit for ocr(image to text converter)
    implementation("com.google.mlkit:text-recognition:16.0.1")

    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("androidx.activity:activity-compose:1.9.0")
}