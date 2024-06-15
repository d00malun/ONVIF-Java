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
version = "1.1.13"

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
