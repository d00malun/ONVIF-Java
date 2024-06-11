plugins {
    kotlin("jvm") version "1.3.72"
}

repositories {
    mavenCentral()
}

group = "be.teletask.onvif"
version = "1.1.13"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains", "annotations", "15.0")
    implementation("net.sf.kxml", "kxml2", "2.3.0")
    implementation("com.squareup.okhttp3", "okhttp", "4.9.3")
    implementation("io.github.rburgst", "okhttp-digest", "2.7")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.4")
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

artifacts.add("archives", sourcesJar)
