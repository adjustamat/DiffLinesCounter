import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.markdownToHTML
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java")
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    alias(libs.plugins.org.jetbrains.intellij)
    alias(libs.plugins.org.jetbrains.changelog)
    alias(libs.plugins.detekt)
}
apply(from = "$rootDir/config/detekt/detekt-build.gradle")

group = Config.pluginGroup
version = Config.pluginVersion

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    pluginName.set(Config.pluginName)
    version.set(Config.platformVersion)
    type.set(Config.platformType)
    downloadSources.set(Config.shouldDownloadPlatformSources)
    updateSinceUntilBuild.set(Config.shouldAutoUpdateSinceUntilBuild)

    plugins.set(listOf(/* Plugin Dependencies */))
}

changelog {
    version.set(project.version.toString())
    path.set("${project.projectDir}/CHANGELOG.md")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        version.set(Config.pluginVersion)
        sinceBuild.set(Config.pluginSinceBuild)

        pluginDescription.set(
            provider {
                val readmeFile = File(projectDir, "README.md")
                if (!readmeFile.exists()) {
                    throw GradleException(
                        """
                            The README.md file is missing from the project directory.
                            Please ensure the file is present to extract the plugin description.
                        """.trimIndent()
                    )
                }

                val content = readmeFile.readText()
                val startMarker = "<!-- start description -->"
                val endMarker = "<!-- end description -->"

                val startIndex = content.indexOf(startMarker)
                val endIndex = content.indexOf(endMarker)

                if (startIndex == -1 || endIndex == -1) {
                    throw GradleException(
                        """
                            Could not find the required description markers in README.md.
                            Make sure the following markers are present in the file:
                            $startMarker and $endMarker
                        """.trimIndent()
                    )
                }

                val description = content.substring(startIndex + startMarker.length, endIndex).trim()
                markdownToHTML(description)
            }
        )

        changeNotes.set(provider {
            changelog.renderItem(
                changelog.get(project.version.toString()),
                org.jetbrains.changelog.Changelog.OutputType.HTML
            )
        })
    }

    runPluginVerifier {
        val ideVersionsList = Config.testedIdeVersions
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        ideVersions.set(ideVersionsList)
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    changelog {
        version.set(Config.pluginVersion)
        header.set(provider {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy")
            val currentDate = dateFormat.format(Date())
            "[${project.version}] - $currentDate"
        })
    }

    // Register a Detekt task
    register<Detekt>("detektAll") {
        description = "Runs detekt on the whole project."
        buildUponDefaultConfig = true
        allRules = true
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        setSource(files("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin"))
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/**")
        exclude("**/build/**")
        reports {
            xml.required.set(true)
            html.required.set(true)
            txt.required.set(true)
            sarif.required.set(false)
        }
    }
}

dependencies {
    implementation(dependencyNotation = "org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r") {
        exclude(group = "org.slf4j")
    }

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
}
