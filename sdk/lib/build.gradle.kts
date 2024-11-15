plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
}

val sdkVersion by extra("1.0.0")
val sdkName by extra("friendly-captcha-android")

android {
    namespace = "com.friendlycaptcha.android.sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 16
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SDK_VERSION", "\"$sdkVersion\"")
        buildConfigField("String", "SDK_NAME", "\"$sdkName\"")
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    sourceSets {
        getByName("androidTest") {
            manifest.srcFile("src/androidTest/AndroidManifest.xml")
        }
    }

    publishing {
        singleVariant("release")
    }
}

afterEvaluate {
    tasks.register("copyAar", Copy::class) {
        from(layout.buildDirectory.dir("outputs/aar"))
        into(layout.projectDirectory.dir("../aar-repo"))
        include("*.aar")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenAndroid") {
                from(components["release"])
                groupId = "com.friendlycaptcha"
                artifactId = sdkName
                version = sdkVersion
            }
        }
        repositories {
            mavenLocal()
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.rules)
    testImplementation(libs.junit)
    testImplementation(libs.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.espresso.core)
}