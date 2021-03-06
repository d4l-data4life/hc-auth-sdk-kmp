import care.data4life.gradle.auth.dependency.d4l
import care.data4life.gradle.auth.dependency.ensureKotlinVersion

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

plugins {
    `kotlin-dsl`
    id("care.data4life.gradle.auth.dependency")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    d4l()
}

ensureKotlinVersion("1.5.31")

dependencies {
    implementation(care.data4life.gradle.auth.dependency.GradlePlugin.kotlin)
    implementation(care.data4life.gradle.auth.dependency.GradlePlugin.android)

    // d4l config
    implementation("com.google.code.gson:gson:2.8.9")

    // dependency-updates.gradle.kts
    implementation("com.github.ben-manes:gradle-versions-plugin:0.42.0")
    // download-scripts.gradle.kts
    implementation("de.undercouch:gradle-download-task:4.1.2")
    // publishing.gradle.kts
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.11.0.202103091610-r")
    // quality-spotless.gradle.kts
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.5.1")
    implementation("com.pinterest:ktlint:0.45.2")
    // versioning.gradle.kts
    implementation("care.data4life.gradle.gitversion:gradle-git-version:0.12.4-d4l")
}
