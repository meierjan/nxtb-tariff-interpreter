
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinBinaryCompatibilityValidator)
    alias(libs.plugins.kotlinKover)
    alias(libs.plugins.dokka)
    id("com.vanniktech.maven.publish") version "0.27.0"
    id("com.gradleup.nmcp") version "0.0.4"
}

group = "wtf.meier.tariffinterpreter"
version = "0.1.0"

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

val javadocJar = tasks.register<Jar>("javadocJar") {
//    dependsOn(tasks)
    archiveClassifier.set("javadoc")
    from("$buildDir/dokka")
}

koverMerged {
    enable()
}


mavenPublishing {
    coordinates("wtf.meier.tariffinterpreter", "tariffinterpreter", System.getenv("VERSION_TAG"))

    pom {
        name.set("nextbike tariff interpreter")
        description.set("A libary to interpret nextbike tariffs and to calculate their prices.")
        inceptionYear.set("2014")
        url.set("https://github.com/meierjan/nxtb-tariff-interpreter")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("meierjan")
                name.set("Jan Meier")
                url.set("https://github.com/meierjan")
            }
        }
        scm {
            url.set("https://github.com/meierjan")
            connection.set("scm:git:git://github.com/meierjan/nxtb-tariff-interpreter.git")
            developerConnection.set("scm:git:ssh://git@github.com/meierjan/nxtb-tariff-interpreter.git")
        }
    }
}


nmcp {
    publishAllPublications {
        username = System.getenv("SONATYPE_USERNAME")
        password = System.getenv("SONATYPE_PASSWORD")
        publicationType = "USER_MANAGED"
    }
}
