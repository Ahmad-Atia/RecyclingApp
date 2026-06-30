import java.util.Properties
import java.io.FileInputStream


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.recyclingapp"
    compileSdk = 36


    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.example.recyclingapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"



        // 2. Load local.properties
        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(FileInputStream(propertiesFile))
        }


        // 3. Create the BuildConfig fields
        // Note the extra escaped quotes "\"...\"" for Strings in Kotlin DSL
        buildConfigField("String", "MISTRAL_API_KEY", properties.getProperty("MISTRAL_API_KEY") ?: "\"\"")






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
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"

            excludes += "META-INF/NOTICE.md"

            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.txt"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.camera.camera2.pipe)
    implementation(libs.cronet.embedded)
    implementation(libs.tracing.perfetto.handshake)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Networking (Your Backend Integration)
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("androidx.browser:browser:1.8.0")
    implementation("androidx.camera:camera-camera2-pipe:1.0.0-beta03")


    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")

    val cameraVersion = "1.3.4"
    implementation("androidx.camera:camera-core:$cameraVersion")
    implementation("androidx.camera:camera-camera2:$cameraVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraVersion")
    implementation("androidx.camera:camera-view:$cameraVersion")

    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
}