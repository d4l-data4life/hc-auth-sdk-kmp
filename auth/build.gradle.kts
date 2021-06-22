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

import care.data4life.sdk.auth.dependency.Dependency
import care.data4life.sdk.auth.LibraryConfig

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.kapt")

    // Android
    id("com.android.library")

    // Publish
    id("care.data4life.sdk.auth.publishing-config")
}

group = LibraryConfig.group

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }

    jvm { }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependency.multiplatform.kotlin.stdLib.common)

                implementation(Dependency.multiplatform.d4l.util.common)
                implementation(Dependency.multiplatform.d4l.securestore.common)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Dependency.multiplatformTest.kotlin.common)
                implementation(Dependency.multiplatformTest.kotlin.commonAnnotations)

                implementation(Dependency.multiplatformTest.mockK.common)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Dependency.multiplatform.kotlin.stdLib.android)

                implementation(Dependency.android.androidX.ktx)
                implementation(Dependency.multiplatform.kotlin.coroutines.android)

                implementation(Dependency.multiplatform.d4l.util.android)
                implementation(Dependency.multiplatform.d4l.securestore.android)

                implementation(Dependency.android.androidX.appCompat)
                implementation(Dependency.android.androidX.browser)
                implementation(Dependency.android.appAuthPatch)
                implementation(Dependency.android.tink)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(Dependency.multiplatformTest.kotlin.jvm)
                implementation(Dependency.multiplatformTest.kotlin.jvmJunit)

                implementation(Dependency.multiplatformTest.mockK.junit)

                implementation(Dependency.androidTest.core)
                implementation(Dependency.androidTest.robolectric)
            }
        }
        val androidAndroidTest by getting {
            dependsOn(androidMain)
            dependencies {
                implementation(Dependency.multiplatformTest.kotlin.jvm)
                implementation(Dependency.multiplatformTest.kotlin.jvmJunit)

                implementation(Dependency.multiplatformTest.mockK.junit)

                implementation(Dependency.androidTest.runner)
                implementation(Dependency.androidTest.espressoCore)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Dependency.multiplatform.kotlin.stdLib.jdk8)

                implementation(Dependency.multiplatform.d4l.util.jvm)
                implementation(Dependency.multiplatform.d4l.securestore.jvm)

                implementation(Dependency.jvm.moshi)
                configurations["kapt"].dependencies.add(
                    project.dependencies.create(Dependency.jvm.moshiCodeGen)
                )
                implementation(Dependency.jvm.scribeCore)
                implementation(Dependency.multiplatform.kotlin.stdLib.jdk8)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(Dependency.multiplatformTest.kotlin.jvm)
                implementation(Dependency.multiplatformTest.kotlin.jvmJunit)

                configurations["kaptTest"].dependencies.add(
                    project.dependencies.create(Dependency.jvm.moshiCodeGen)
                )

                implementation(Dependency.multiplatformTest.mockK.junit)
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

android {
    compileSdkVersion(LibraryConfig.android.compileSdkVersion)

    defaultConfig {
        minSdkVersion(LibraryConfig.android.minSdkVersion)
        targetSdkVersion(LibraryConfig.android.targetSdkVersion)

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

    resourcePrefix(LibraryConfig.android.resourcePrefix)

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

val d4lClientConfig = care.data4life.sdk.auth.D4LConfigHelper.loadClientConfigAndroid("$rootDir")
val jvmAssetsPath = "${projectDir}/src/jvmTest/resources"

val provideTestConfig: Task by tasks.creating {
    doLast {
        val asset = File(jvmAssetsPath)
        if (!asset.exists()) asset.mkdirs()
        File(jvmAssetsPath, "client_config.json").writeText(care.data4life.sdk.auth.D4LConfigHelper.toJson(d4lClientConfig))
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
