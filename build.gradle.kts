import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    java
    `java-library`
    signing

    alias(libs.plugins.shadow)
    alias(libs.plugins.grgit)
    alias(libs.plugins.publish)

    eclipse
    idea
}

group = "co.crystaldev"
version = "3.809.${project.findProperty("revision")}" + (if (isRelease()) ""  else "-SNAPSHOT")
description = rootProject.name

tasks {
    clean {
        delete("builds")
    }
    getByName<Jar>("jar") {
        enabled = false
    }
    register<Javadoc>("aggregatedJavadocs") {
        group = "documentation"
        title = "$project.name $version API"
        description = "Generate javadocs from all child projects as if it was a single project"
        setDestinationDir(file("./docs/javadoc"))

        val opt = options as StandardJavadocDocletOptions
        opt.links("https://docs.spring.io/spring/docs/4.3.x/javadoc-api/")
        opt.links("https://docs.spring.io/spring-ws/docs/2.3.0.RELEASE/api/")
        opt.links("https://docs.spring.io/spring-security/site/docs/4.0.4.RELEASE/apidocs/")
        opt.links("https://docs.oracle.com/javase/8/docs/api/")
        opt.addStringOption("Xdoclint:none", "-quiet")

        opt.encoding("UTF-8")
        opt.noTimestamp()

        delete("./docs")

        subprojects.forEach { project ->
            project.tasks.withType<Javadoc>().forEach { javadocTask ->
                source += javadocTask.source
                classpath += javadocTask.classpath
                excludes += javadocTask.excludes
                includes += javadocTask.includes
            }
        }
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        maven {
            name = "AlpineCloud"
            url = uri("https://lib.alpn.cloud/mirrors/")
        }
        maven {
            name = "Jitpack"
            url = uri("https://jitpack.io")
            content {
                includeModule("com.github.MilkBowl", "VaultAPI")
            }
        }
        maven {
            name = "EngineHub"
            url = uri("https://maven.enginehub.org/repo/")
        }
    }

    apply {
        plugin<JavaPlugin>()
        plugin<JavaLibraryPlugin>()
        plugin<com.vanniktech.maven.publish.MavenPublishPlugin>()
        plugin<ShadowPlugin>()

        plugin<EclipsePlugin>()
        plugin<IdeaPlugin>()
    }

    plugins.withId("java") {
        the<JavaPluginExtension>().toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.compileJava.configure {
        options.release.set(11)
    }

    configurations.all {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
    }

    val javaComponent = components["java"] as AdhocComponentWithVariants
    javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
        skip()
    }

    publishing {
        repositories {
            maven {
                name = "AlpineCloud"
                url = uri("https://lib.alpn.cloud" + if (isRelease()) "/releases" else "/snapshots")
                credentials {
                    username = System.getenv("ALPINE_MAVEN_NAME")
                    password = System.getenv("ALPINE_MAVEN_SECRET")
                }
            }
        }
    }

    mavenPublishing {
        coordinates(
            groupId = "$group",
            artifactId = project.name,
            version = "${project.version}",
        )

        pom {
            name.set(project.name)
            description.set("PlotSquared, a land and world management plugin for Minecraft.")
            url.set("https://github.com/crystal-development-llc/PlotSquared-Legacy/")

            licenses {
                license {
                    name.set("GNU General Public License, Version 3.0")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                    distribution.set("repo")
                }
            }

            scm {
                url.set("https://github.com/crystal-development-llc/PlotSquared-Legacy")
                connection.set("scm:git:https://github.com/crystal-development-llc/PlotSquared-Legacy.git")
                developerConnection.set("scm:git:git@github.com:crystal-development-llc/PlotSquared-Legacy.git")
                tag.set("${project.version}")
            }

            issueManagement {
                system.set("GitHub")
                url.set("https://github.com/crystal-development-llc/PlotSquared-Legacy/issues")
            }
        }
    }

    tasks {
        compileJava {
            options.compilerArgs.add("-parameters")
            options.encoding = "UTF-8"
        }

        shadowJar {
            this.archiveClassifier.set(null as String?)
            this.archiveFileName.set("${project.name}-${project.version}.${this.archiveExtension.getOrElse("jar")}")
            doLast {
                val input = archiveFile.get()
                val outputDir = File(rootProject.rootDir, "builds")
                val outputFile = File(outputDir, input.asFile.name)
                outputDir.mkdirs()
                Files.copy(input.asFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }

        named("build") {
            dependsOn(named("shadowJar"))
        }

        test {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                showExceptions = true
                showCauses = true
                showStackTraces = true
                showStandardStreams = true
            }
        }

        withType<AbstractArchiveTask>().configureEach {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }
    }
}

fun isRelease(): Boolean {
    return (project.findProperty("release") as? String)?.toBoolean() ?: false
}
