/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java library project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.8/userguide/building_java_projects.html in the Gradle documentation.
 */

// name is set in settings.gradle.kts lol
group = "dev.qixils.cc4j"
version = "1.0.4"

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
    signing
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.jackson.core)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.databind)
    implementation(libs.slf4j)
    api(libs.geantyref)
    api(libs.annotations)
}

// java version
val targetJavaVersion = 21
tasks.withType<JavaCompile>().configureEach {
    options.release.set(targetJavaVersion)
    options.encoding = Charsets.UTF_8.name()
}
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    withJavadocJar()
    withSourcesJar()
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name = "crowdcontrol4j pubsub"
                description = "An implementation of the Crowd Control PubSub WebSocket protocol for Java 21+"
                url = "http://github.com/qixils/crowdcontrol4j-pubsub"
                licenses {
                    license {
                        name = "Functional Source License, Version 1.1, Apache 2.0 Future License"
                        url = "https://github.com/qixils/crowdcontrol4j-pubsub/blob/main/LICENSE.md"
                    }
                }
                developers {
                    developer {
                        id = "qixils"
                        name = "Lexi Larkin"
                        email = "lexi+git@qixils.dev"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/qixils/crowdcontrol4j-pubsub.git"
                    developerConnection = "scm:git:ssh://github.com/qixils/crowdcontrol4j-pubsub.git"
                    url = "http://github.com/qixils/crowdcontrol4j-pubsub/"
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri(if (version.toString().contains("SNAPSHOT")) "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            else "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("ossrhUsername") as String?
                password = findProperty("ossrhPassword") as String?
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
