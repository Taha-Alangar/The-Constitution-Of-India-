plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.trinitydevelopers.constitutionofindia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.trinitydevelopers.constitutionofindia"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    //lottie animation
    implementation ("com.airbnb.android:lottie:4.2.0")

    //Room Database
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.legacy.support.v4)
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    ksp ("androidx.room:room-compiler:2.6.1")
    testImplementation ("androidx.room:room-testing:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    //jsoup
    implementation ("org.jsoup:jsoup:1.14.3")

    //picasso
    implementation ("com.squareup.picasso:picasso:2.71828")

    //retorift
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}