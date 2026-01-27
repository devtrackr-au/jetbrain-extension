plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.devtrackr"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2023.3")
    type.set("IC") // IntelliJ IDEA Community Edition (also compatible with Rider, PyCharm, WebStorm, etc.)
    // Git plugin is optional - plugin will work without it
    plugins.set(listOf())
    
    // Alternative: To test specifically with Rider, uncomment and use:
    // type.set("RD") // Rider
    // version.set("2023.3")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("203") // Support from 2020.3 onwards (wider compatibility)
        // untilBuild removed - plugin will be compatible with all future IDE versions
        // If you want to limit compatibility, set to latest version like: untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        // Token can be set via:
        // 1. Environment variable: ORG_GRADLE_PROJECT_intellijPublishToken
        // 2. Gradle property: -Dorg.gradle.project.intellijPublishToken=TOKEN
        // 3. Or uncomment below and set PUBLISH_TOKEN environment variable
        // token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
}
