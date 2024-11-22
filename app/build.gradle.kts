plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id ("kotlin-parcelize")
    //alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.mp3"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mp3"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    secrets {
        // To add your Maps API key to this project:
        // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
        // 2. Add this line, where YOUR_API_KEY is your API key:
        //        MAPS_API_KEY=YOUR_API_KEY
        propertiesFileName = "secrets.properties"

        // A properties file containing default secret values. This file can be
        // checked in version control.
        defaultPropertiesFileName = "local.defaults.properties"

        // Configure which keys should be ignored by the plugin by providing regular expressions.
        // "sdk.dir" is ignored by default.
        ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
        ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
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
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

//ak


dependencies {

    //ak
    implementation ("com.google.firebase:firebase-firestore-ktx:24.7.0")
    implementation ("com.google.firebase:firebase-storage-ktx:20.1.0")
    implementation ("com.google.firebase:firebase-auth-ktx:21.1.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.compose.ui:ui-test-junit4:1.2.1")
    implementation ("com.google.code.gson:gson:2.10")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.10.2")
    // ... existing dependencies ...

    implementation ("com.google.android.gms:play-services-location:21.2.0")

// ... existing dependencies ...

    implementation (platform("com.google.firebase:firebase-bom:32.7.2"))
        implementation ("com.google.firebase:firebase-firestore-ktx")
        implementation ("com.google.firebase:firebase-analytics-ktx")



    // Firebase Firestore
    implementation ("com.google.firebase:firebase-firestore-ktx:25.1.1")
// Firebase Authentication (if needed)
    implementation ("com.google.firebase:firebase-auth-ktx:23.1.0")


    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation ("com.google.firebase:firebase-auth:21.0.6")
    //implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation ("com.google.android.gms:play-services-location:21.3.0")


    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore:25.1.1")


    implementation ("androidx.activity:activity-ktx:1.8.0")

    implementation("androidx.navigation:navigation-compose:2.8.4") // Use the latest version

    //heheuserinterkeliyetha
//    implementation ('com.google.android.libraries.maps:maps:3.1.0')
//    implementation ('com.google.maps.android:maps-compose:2.2.0')
//    implementation ("androidx.compose.material:material:1.3.1")
//    implementation ("androidx.compose.ui:ui:1.3.1")
//    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")


//        implementation ("androidx.compose.ui:ui:1.0.5")
//        implementation ("androidx.compose.material:material:1.0.5")
//        implementation ("androidx.compose.ui:ui-tooling-preview:1.0.5")
//        implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
//        implementation ("androidx.activity:activity-compose:1.3.1")
//        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
//        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
//        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

//    implementation ("androidx.compose.ui:ui:1.0.5")
//    implementation ("androidx.compose.material:material:1.0.5")
//    implementation ("androidx.compose.ui:ui-tooling-preview:1.0.5")
//    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
//    implementation ("androidx.activity:activity-compose:1.3.1")
//    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
//    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")


//
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation ("com.google.android.libraries.places:places:2.6.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
        implementation ("com.google.android.libraries.places:places:3.3.0")



    implementation ("androidx.compose.ui:ui:1.0.5")
        implementation ("androidx.compose.material:material:1.0.5")
        implementation ("androidx.compose.ui:ui-tooling-preview:1.0.5")
        implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
        implementation ("androidx.activity:activity-compose:1.3.1")
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")




    implementation ("androidx.compose.ui:ui-test-junit4:1.2.1")

    implementation ("com.google.code.gson:gson:2.10.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    //implementation(libs.mediation.test.suite)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.location)
    //implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}