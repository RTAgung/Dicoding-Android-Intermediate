// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}

// Core
val coreVersion by extra { "1.10.0" }
val appcompatVersion by extra { "1.6.1" }
val lifecycleVersion by extra { "2.6.2" }
val activityVersion by extra { "1.7.2" }
val fragmentVersion by extra { "1.6.1" }

// Testing
val junitVersion by extra { "4.13.2" }
val extVersion by extra { "1.1.5" }
val espressoVersion by extra { "3.5.1" }

// UI
val recyclerviewVersion by extra { "1.3.1" }
val materialVersion by extra { "1.9.0" }
val constraintlayoutVersion by extra { "2.1.4" }

// Photo Online Loader
val glideVersion by extra { "4.15.1" }

// Navigation
val navigationVersion by extra { "2.6.0" }

// API
val retrofit2Version by extra { "2.9.0" }
val okhttp3Version by extra { "4.9.0" }

// Preferences DataStore
val datastoreVersion by extra { "1.0.0" }

// LottieFiles
val lottieVersion by extra { "3.4.0" }
