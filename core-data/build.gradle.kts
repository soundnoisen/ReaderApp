plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}


android {
    namespace = "com.example.core.data"
    compileSdk {
        version = release(36)
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "YANDEX_IAM_TOKEN",
            "\"${project.properties["YANDEX_IAM_TOKEN"]}\""
        )

        buildConfigField(
            "String",
            "YANDEX_BUCKET",
            "\"${project.properties["YANDEX_BUCKET"]}\""
        )

        buildConfigField(
            "String",
            "YANDEX_BASE_URL",
            "\"${project.properties["YANDEX_BASE_URL"]}\""
        )

        buildConfigField(
            "String",
            "CLOUDINARY_CLOUD_NAME",
            "\"${project.properties["CLOUDINARY_CLOUD_NAME"]}\""
        )

        buildConfigField(
            "String",
            "CLOUDINARY_UPLOAD_PRESET",
            "\"${project.properties["CLOUDINARY_UPLOAD_PRESET"]}\""
        )

        buildConfigField(
            "String",
            "GOOGLE_CLIENT_ID",
            "\"${project.properties["GOOGLE_CLIENT_ID"]}\""
        )
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
    implementation(project(":core-domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.okhttp)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.jsoup)

    implementation(libs.mhiew.android.pdf.viewer)

    implementation(libs.androidx.credentials)
    implementation("com.google.android.gms:play-services-auth:21.5.0")
    implementation(libs.googleid)

    implementation(libs.kotlinx.coroutines.play.services)
}