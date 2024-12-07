plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.med_finder"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.med_finder"
        minSdk = 24
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // ViewPager2
    implementation(libs.androidx.viewpager2)
    // CircleIndicator (untuk indikator titik)
    implementation(libs.circleindicator)
    // Material Components
    implementation(libs.material)
    implementation(libs.glide)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)
    // CameraX Core Library
    // CameraX Core library
    implementation(libs.androidx.camera.core)

    // CameraX Camera2 Implementation
    implementation(libs.androidx.camera.camera2)

    // CameraX Lifecycle library
    implementation(libs.androidx.camera.lifecycle.v132)

    // CameraX View
    implementation(libs.androidx.camera.view.v132)

    // Optional: CameraX Extensions
    implementation(libs.androidx.camera.extensions)
    // (Optional) CameraX Extensions (remove if not needed)
    // implementation(libs.camerax.extensions)
    annotationProcessor(libs.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

