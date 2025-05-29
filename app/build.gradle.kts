plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

dependencies {
    implementation(libs.material)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.wheelpicker)
    implementation(libs.core)
    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

android {
    namespace = "com.mycoach.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mycoach.app"
        minSdk = 29
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