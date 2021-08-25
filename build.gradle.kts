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

import care.data4life.sdk.auth.dependency.d4l
import care.data4life.sdk.auth.dependency.gitHub
import care.data4life.sdk.auth.dependency.jitPack

plugins {
    id("care.data4life.sdk.auth.dependency")

    id("care.data4life.sdk.auth.dependency-updates")
    id("care.data4life.sdk.auth.download-scripts")
    id("care.data4life.sdk.auth.publishing")
    id("care.data4life.sdk.auth.quality-spotless")
    id("care.data4life.sdk.auth.versioning")
}

allprojects {
    repositories {
        google()
        mavenCentral()

        gitHub(project)

        d4l()

        jitPack()
    }
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "6.9.1"
    distributionType = Wrapper.DistributionType.ALL
}