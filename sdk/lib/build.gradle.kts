/*!
 * Copyright (c) Friendly Captcha GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.vanniktech.maven.publish")
}

val sdkVersion by extra("1.0.1")
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
}

afterEvaluate {
    tasks.register("copyAar", Copy::class) {
        from(layout.buildDirectory.dir("outputs/aar"))
        into(layout.projectDirectory.dir("../aar-repo"))
        include("*.aar")
    }

    mavenPublishing {
        coordinates("com.friendlycaptcha.android", sdkName, sdkVersion)

        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
        signAllPublications()

        pom {
            name.set("Friendly Captcha SDK")
            description.set("A SDK for integrating Friendly Captcha into your Android apps.")
            url.set("https://github.com/FriendlyCaptcha/friendly-captcha-android")
            inceptionYear.set("2024")

            licenses {
                license {
                    name.set("Mozilla Public License Version 2.0")
                    url.set("https://www.mozilla.org/en-US/MPL/2.0/")
                }
            }

            developers {
                developer {
                    id.set("friendlycaptcha")
                    name.set("Friendly Captcha Developers")
                    email.set("dev@friendlycaptcha.com")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/FriendlyCaptcha/friendly-captcha-android.git")
                developerConnection.set("scm:git:ssh://github.com/FriendlyCaptcha/friendly-captcha-android.git")
                url.set("https://github.com/FriendlyCaptcha/friendly-captcha-android")
            }
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
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.espresso.core)
}