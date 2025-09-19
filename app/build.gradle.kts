import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
}

// Load properties from local.properties file
// NEW (Correct)
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

android {
    namespace = "io.appwrite.starterkit"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.appwrite.starterkit"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true

        // Expose the API key from local.properties to the BuildConfig file
        buildConfigField("String", "GOLD_API_KEY", "\"${localProperties.getProperty("GOLD_API_KEY")}\"")
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
        // Enable core library desugaring to use modern Java APIs (like java.time) on older Android versions.
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true // Ensure BuildConfig is enabled
    }
}

dependencies {
    // Dependency for core library desugaring. Must be included when desugaring is enabled.
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Appwrite SDK
    implementation(libs.appwrite)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Core, Compose, and Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose UI, Material3, Preview
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Compose BOM for consistent versions
    implementation(platform(libs.androidx.compose.bom))

    // AndroidX & Material Design
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.mediation.test.suite)

    // Debug libraries for tooling
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // CardView
    implementation(libs.androidx.cardview)

    // Networking & JSON Parsing
    implementation(libs.okhttp)
    implementation(libs.gson)

    // Retrofit for type-safe HTTP requests.
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    // Image Loading
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
}
