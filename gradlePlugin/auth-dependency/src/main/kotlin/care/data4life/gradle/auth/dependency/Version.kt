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

package care.data4life.gradle.auth.dependency

object Version {

    val gradlePlugin = GradlePlugin
    val multiplatform = Multiplatform
    val multiplatformTest = MultiplatformTest
    val jvm = Jvm
    val jvmTest = JvmTest
    val android = Android
    val androidTest = AndroidTest

    /**
     * [Kotlin](https://github.com/JetBrains/kotlin)
     */
    const val kotlin = "1.6.10"

    object GradlePlugin {
        const val kotlin = Version.kotlin
        const val android = "7.1.3"
    }

    object Multiplatform {

        val kotlin = Kotlin
        val d4l = D4L

        object Kotlin {
            /**
             * [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
             */
            const val coroutines = "1.6.0-native-mt"

            /**
             * [Serialization](https://github.com/Kotlin/kotlinx.serialization)
             */
            const val serialization = "1.3.2"

            /**
             * [DateTime](https://github.com/Kotlin/kotlinx-datetime)
             */
            const val dateTime = "0.3.2"
        }

        object D4L {
            // D4L
            /**
             * [hc-securestore-kmp](https://github.com/d4l-data4life/hc-securestore-kmp)
             */
            const val securestore = "1.15.0"

            /**
             * [hc-util-kmp](https://github.com/d4l-data4life/hc-util-kmp)
             */
            const val utilSdk = "1.13.0"
        }
    }

    object MultiplatformTest {
        /**
         * [mockk](http://mockk.io)
         */
        const val mockK = "1.10.6"
    }

    object Jvm {
        // Authorization
        /**
         * [scribe](https://github.com/scribejava/scribejava)
         */
        const val scribe = "6.1.0"

        // Serialization
        /**
         * [moshi](https://github.com/square/moshi)
         */
        const val moshi = "1.13.0"
    }

    object JvmTest {
        const val jUnit = "4.13.2"
    }

    object Android {

        val androidX = AndroidX

        /**
         * [Android Desugar](https://developer.android.com/studio/write/java8-support)
         */
        const val androidDesugar = "1.1.5"

        object AndroidX {
            /**
             * [AndroidX](https://developer.android.com/jetpack/androidx)
             */
            const val core = "1.1.0"
            const val ktx = "1.7.0"
            const val appCompat = "1.4.1"
            const val browser = "1.4.0"

            const val constraintLayout = "2.1.3"
        }

        /**
         * [Material Android](https://github.com/material-components/material-components-android)
         */
        const val material = "1.5.0"

        // Authorization
        /**
         * [appAuth](https://github.com/openid/AppAuth-Android)
         */
        const val appAuth = "0.10.0"

        // Serialization
        /**
         * [moshi](https://github.com/square/moshi)
         */
        const val moshi = "1.12.0"

        // Crypto
        /**
         * [tink](https://github.com/google/tink)
         */
        const val tink = "1.2.2"
    }

    object AndroidTest {
        /**
         * [Android Testing](https://developer.android.com/testing)
         */
        const val androidXTestCore = "1.4.0"
        const val androidXEspresso = "3.4.0"
        const val androidXUiAutomator = "2.2.0"

        /**
         * [Robolectric](https://github.com/robolectric/robolectric)
         */
        const val robolectric = "4.5.1"
    }
}
