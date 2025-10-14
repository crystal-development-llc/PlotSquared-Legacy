import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-library`
    idea
    eclipse
}

plugins.withId("java") {
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        sourceCompatibility = JavaVersion.VERSION_11
    }
}

configurations.all {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 11)
}

tasks {
    withType<JavaCompile>().configureEach {
        configureCompiler()
    }
    withType<ProcessResources>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.WARN
        filteringCharset = Charsets.UTF_8.name()
    }
    withType<Javadoc>().configureEach {
        configureJavadocs()
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

fun JavaCompile.configureCompiler() {
    options.release.set(11)
    options.encoding = Charsets.UTF_8.name()
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Xlint:-options",
            "-Xlint:deprecation",
            "-Xlint:unchecked"
        )
    )
}

fun Javadoc.configureJavadocs() {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:all", "-quiet")
        charset(Charsets.UTF_8.name())
        encoding(Charsets.UTF_8.name())
        noTimestamp()
        use()

        links(
            "https://docs.oracle.com/en/java/javase/11/docs/api/",
        )
    }
}