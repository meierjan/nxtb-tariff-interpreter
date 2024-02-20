import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinBinaryCompatibilityValidator)
    alias(libs.plugins.kotlinKover)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    id("signing")
}

group = "wtf.meier.tariffinterpreter"
version = "0.1.0-SNAPSHOT"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release", "debug")
    }

    listOf(
        iosX64(), iosArm64(), iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "wtf.meier.tariffinterpreter"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

repositories {
    google()
    mavenCentral()
}

mavenPublishing {

    publishToMavenCentral(SonatypeHost.DEFAULT)
    signAllPublications()


    val repoUrl = "https://github.com/meierjan/nxtb-tariff-interpreter"

    coordinates("wtf.meier", "tariff-interpreter", "0.1")

    pom {
        name.set("nextbike tariff interpreter")
        description.set("A library to interpret nextbike tariffs and to calculate their prices")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        url.set(repoUrl)
        issueManagement {
            system.set("Github")
            url.set("${repoUrl}/issues")
        }
        scm {
            connection.set("$repoUrl.git")
            url.set(repoUrl)
        }
        developers {
            developer {
                name.set("Jan Meier")
                email.set("jan@meier.wtf")
            }
        }
    }
}
signing {
    useInMemoryPgpKeys(
        gradleLocalProperties(rootDir).getProperty("gpgKeySecret"),
        gradleLocalProperties(rootDir).getProperty("gpgKeyPassword"),

        )
    sign(publishing.publications)
}
