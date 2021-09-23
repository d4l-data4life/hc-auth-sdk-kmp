/*
 * Copyright (c) 2021 D4L data4life gGmbH / All rights reserved.
 *
 * D4L owns all legal rights, title and interest in and to the Software Development Kit ("SDK"),
 * including any intellectual property rights that subsist in the SDK.
 *
 * The SDK and its documentation may be accessed and used for viewing/review purposes only.
 * Any usage of the SDK for other purposes, including usage for the development of
 * applications/third-party applications shall require the conclusion of a license agreement
 * between you and D4L.
 *
 * If you are interested in licensing the SDK for your own applications/third-party
 * applications and/or if you’d like to contribute to the development of the SDK, please
 * contact D4L by email to help@data4life.care.
 */

import care.data4life.gradle.auth.dependency.Dependency

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.kapt")

    // Android
    id("com.android.library")

    // Publish
    id("care.data4life.sdk.auth.publishing-config")
}

group = care.data4life.gradle.auth.config.LibraryConfig.group

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }

    jvm { }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.kotlin.stdLib.common)

                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.d4l.util.common)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.d4l.result.errorCommon)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.d4l.securestore.common)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.kotlin.common)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.kotlin.commonAnnotations)

                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.mockK.common)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.kotlin.stdLib.android)

                implementation(care.data4life.gradle.auth.dependency.Dependency.android.androidX.ktx)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.kotlin.coroutines.android)

                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.d4l.util.android)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.d4l.securestore.android)

                implementation(care.data4life.gradle.auth.dependency.Dependency.android.androidX.appCompat)
                implementation(care.data4life.gradle.auth.dependency.Dependency.android.androidX.browser)
                implementation(care.data4life.gradle.auth.dependency.Dependency.android.appAuth)
                implementation(care.data4life.gradle.auth.dependency.Dependency.android.tink)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.kotlin.jvm)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.kotlin.jvmJunit)

                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.mockK.junit)

                implementation(care.data4life.gradle.auth.dependency.Dependency.androidTest.core)
                implementation(care.data4life.gradle.auth.dependency.Dependency.androidTest.robolectric)
            }
        }
        val androidAndroidTest by getting {
            dependsOn(androidMain)
            dependencies {
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.kotlin.jvm)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.kotlin.jvmJunit)

                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.mockK.junit)

                implementation(care.data4life.gradle.auth.dependency.Dependency.androidTest.runner)
                implementation(care.data4life.gradle.auth.dependency.Dependency.androidTest.espressoCore)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.kotlin.stdLib.jdk8)

                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.d4l.util.jvm)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.d4l.securestore.jvm)

                implementation(care.data4life.gradle.auth.dependency.Dependency.jvm.moshi)
                configurations["kapt"].dependencies.add(
                    project.dependencies.create(care.data4life.gradle.auth.dependency.Dependency.jvm.moshiCodeGen)
                )
                implementation(care.data4life.gradle.auth.dependency.Dependency.jvm.scribeCore)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatform.kotlin.stdLib.jdk8)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.kotlin.jvm)
                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.kotlin.jvmJunit)

                configurations["kaptTest"].dependencies.add(
                    project.dependencies.create(care.data4life.gradle.auth.dependency.Dependency.jvm.moshiCodeGen)
                )

                implementation(care.data4life.gradle.auth.dependency.Dependency.multiplatformTest.mockK.junit)
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

android {
    compileSdkVersion(care.data4life.gradle.auth.config.LibraryConfig.android.compileSdkVersion)

    defaultConfig {
        minSdkVersion(care.data4life.gradle.auth.config.LibraryConfig.android.minSdkVersion)
        targetSdkVersion(care.data4life.gradle.auth.config.LibraryConfig.android.targetSdkVersion)

        versionCode = 1
        versionName = "${project.version}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments(
            mapOf(
                "clearPackageData" to "true"
            )
        )

        buildTypes {
            getByName("debug") {
                setMatchingFallbacks("debug", "release")
            }
        }
    }

    resourcePrefix(care.data4life.gradle.auth.config.LibraryConfig.android.resourcePrefix)

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.setSrcDirs(setOf("src/androidMain/kotlin"))
            res.setSrcDirs(setOf("src/androidMain/res"))
        }

        getByName("test") {
            java.setSrcDirs(setOf("src/androidTest/kotlin"))
            res.setSrcDirs(setOf("src/androidTest/res"))
        }

        getByName("androidTest") {
            java.setSrcDirs(setOf("src/androidAndroidTest/kotlin"))
            res.setSrcDirs(setOf("src/androidAndroidTest/res"))
        }
    }
}

val d4lClientConfig = care.data4life.gradle.auth.D4LConfigHelper.loadClientConfigAndroid("$rootDir")
val jvmAssetsPath = "${projectDir}/src/jvmTest/resources"

val provideTestConfig: Task by tasks.creating {
    doLast {
        val asset = File(jvmAssetsPath)
        if (!asset.exists()) asset.mkdirs()
        File(jvmAssetsPath, "client_config.json").writeText(care.data4life.gradle.auth.D4LConfigHelper.toJson(d4lClientConfig))
    }
}

tasks.named("clean") {
    doLast {
        delete("${jvmAssetsPath}/client_config.json")
    }
}

tasks.getByName("jvmTest") {
    dependsOn("provideTestConfig")
    mustRunAfter("provideTestConfig")
}
