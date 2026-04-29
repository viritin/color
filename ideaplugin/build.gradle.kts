plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.15.0"
    id("org.jetbrains.changelog") version "2.5.0"
    id("net.researchgate.release") version "3.1.0"
}

group = "in.virit"
// version is sourced from gradle.properties so net.researchgate.release can rewrite it.

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea("2026.1.1")
        bundledPlugin("com.intellij.java")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "261"
            untilBuild = provider { null }
        }
        // Inject the [Unreleased] / [version] entry from CHANGELOG.md as <change-notes>
        // in the packaged plugin.xml. Single source of truth for release notes.
        changeNotes = provider {
            with(changelog) {
                renderItem(
                    (getOrNull(project.version.toString()) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    org.jetbrains.changelog.Changelog.OutputType.HTML
                )
            }
        }
    }
    pluginVerification {
        ides {
            recommended()
        }
    }
    // Signing + publishing read credentials from ~/.gradle/gradle.properties — see RELEASING.md.
    // Properties are read lazily; tasks fail only when actually invoked without configured creds.
    signing {
        certificateChainFile = layout.projectDirectory.file(providers.gradleProperty("certificatePath"))
        privateKeyFile = layout.projectDirectory.file(providers.gradleProperty("privateKeyPath"))
        password = providers.gradleProperty("privateKeyPassword")
    }
    publishing {
        token = providers.gradleProperty("marketplaceToken")
                .orElse(providers.environmentVariable("JETBRAINS_MARKETPLACE_TOKEN"))
    }
}

tasks.runIde {
    args(layout.projectDirectory.dir("sample-project").asFile.absolutePath)
}

changelog {
    repositoryUrl = "https://github.com/viritin/color"
}

release {
    // Standard `./gradlew release` ritual: confirm release version, run build, tag,
    // bump to next -SNAPSHOT, push.
    tagTemplate.set("v\$version")
}

// Roll the [Unreleased] section into the [<release>] section before tagging.
tasks.named("preTagCommit") {
    dependsOn("patchChangelog")
}

// Sign + publish to JetBrains Marketplace as part of the release ritual.
tasks.named("afterReleaseBuild") {
    dependsOn("publishPlugin")
}
