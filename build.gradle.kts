plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io") // maven repo where the current library resides
    }
}

group = "be.teletask.onvif"
version = "1.1.15"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.kobjects", "kxml2", "2.4.1")
    implementation("com.squareup.okhttp3", "okhttp", "4.12.0")
    implementation("io.github.rburgst", "okhttp-digest", "3.1.0")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.8.1")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

artifacts.add("archives", sourcesJar)

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/szantogab/ONVIF-Java")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register("gprRelease", MavenPublication::class) {
            groupId = "com.github.szantogab"
            artifactId = "onvif-java"
            version = project.version as String

            from(components["java"])

            artifact(sourcesJar)

            pom {
                packaging = "jar"
                name.set("ONVIF-Java")
                description.set("ONVIF support for Java and Kotlin")
                url.set("https://github.com/szantogab/ONVIF-Java")
                /*scm {
                    url.set(myGithubHttpUrl)
                }
                issueManagement {
                    url.set(myGithubIssueTrackerUrl)
                }*/
                /*                licenses {
                                    license {
                                        name.set(myLicense)
                                        url.set(myLicenseUrl)
                                    }
                                }*/
                developers {
                    developer {
                        id.set("szantogab")
                        name.set("Gabor Szanto")
                    }
                }
            }

        }
    }
}