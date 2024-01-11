import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinBinaryCompatibilityValidator)
    alias(libs.plugins.kotlinKover)
    alias(libs.plugins.dokka)
    id("maven-publish")
    id("signing")
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


val javadocJar = tasks.register<Jar>("javadocJar") {
//    dependsOn(tasks)
    archiveClassifier.set("javadoc")
    from("$buildDir/dokka")
}

val repoUrl = "https://github.com/meierjan/nxtb-tariff-interpreter"
extensions.configure<PublishingExtension> {
    publications {
        withType<MavenPublication> {
            artifact(javadocJar)

            pom {
                name.set("nextbike tariff interpreter")
                description.set("A library to interpret nextbike tariffs and to calculate their prices")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
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
                        email.set("tariff-interpreter@meier.dev")
                    }
                }
            }
        }
    }


    val publishing = extensions.getByType<PublishingExtension>()
    extensions.configure<SigningExtension> {
        useInMemoryPgpKeys(
            gradleLocalProperties(rootDir).getProperty("gpgKeySecret"),
            gradleLocalProperties(rootDir).getProperty("gpgKeyPassword"),
        )

        sign(publishing.publications)
    }

    // TODO: remove after https://youtrack.jetbrains.com/issue/KT-46466 is fixed
    project.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
        dependsOn(project.tasks.withType(Sign::class.java))
    }

}

koverMerged {
    enable()
}
