plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") //
}

android {
    namespace = "com.example.famcare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.famcare"
        minSdk = 30
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation ("com.google.firebase:firebase-firestore:25.0.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("androidx.activity:activity:1.7.0")
    implementation ("androidx.core:core:1.10.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0" )


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}