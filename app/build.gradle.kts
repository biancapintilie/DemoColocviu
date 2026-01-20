plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ro.pub.cs.systems.eim.practicaltest02"
    compileSdk = 34 // Sau cat ai tu, lasa cum era daca mergea (ex: 33 sau 34)

    defaultConfig {
        applicationId = "ro.pub.cs.systems.eim.practicaltest02"
        minSdk = 24 // Recomandat sa fie minim 24 pt unele librarii, dar merge si 16
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // === AICI SUNT CELE 2 LINII CRITICE PENTRU EXAMEN ===

    // 1. Pentru HTTP Request (Vreme, Bitcoin, Autocomplete, URL Body)
    implementation("cz.msebera.android:httpclient:4.5.8")

    // 2. Pentru Google Maps (Daca pica subiectul cu harta)
    implementation("com.google.android.gms:play-services-maps:18.1.0")
}