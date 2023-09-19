plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.submission1"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.submission1"
        minSdk = 26
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
}

dependencies {
    // Core
    val coreVersion: String by rootProject.extra
    val appcompatVersion: String by rootProject.extra
    val lifecycleVersion: String by rootProject.extra
    val activityVersion: String by rootProject.extra
    val fragmentVersion: String by rootProject.extra
    implementation("androidx.core:core-ktx:$coreVersion")
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-ktx:$activityVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    // Testing
    val junitVersion: String by rootProject.extra
    val extVersion: String by rootProject.extra
    val espressoVersion: String by rootProject.extra
    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("androidx.test.ext:junit:$extVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")

    // UI
    val recyclerviewVersion: String by rootProject.extra
    val materialVersion: String by rootProject.extra
    val constraintlayoutVersion: String by rootProject.extra
    implementation("androidx.recyclerview:recyclerview:$recyclerviewVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintlayoutVersion")

    // Photo Online Loader
    val glideVersion: String by rootProject.extra
    implementation("com.github.bumptech.glide:glide:$glideVersion")

    // Navigation
    val navigationVersion: String by rootProject.extra
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    // Preferences DataStore
    val datastoreVersion: String by rootProject.extra
    implementation("androidx.datastore:datastore-preferences:$datastoreVersion")

    // API
    val retrofit2Version: String by rootProject.extra
    val okhttp3Version: String by rootProject.extra
    implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2Version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp3Version")

    // LottieFiles
    val lottieVersion: String by rootProject.extra
    implementation("com.airbnb.android:lottie:$lottieVersion")
}