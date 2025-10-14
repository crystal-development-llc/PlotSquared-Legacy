dependencies {
    compileOnlyApi(libs.annotations)

    // Server
    compileOnlyApi(libs.gson)
    compileOnlyApi(libs.guava)
    compileOnlyApi(libs.snakeyaml)

    // Plugins
    compileOnly(libs.worldeditCore) {
        exclude("org.mockito")
    }

    // Testing
    testImplementation(libs.guava)
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
}

tasks {
    processResources {
        val props = mutableMapOf(
            "version" to project.version.toString(),
            "commit" to project.latestCommitHash(),
            "date" to project.latestCommitDateTime(),
        )
        inputs.properties(props)
        filesMatching("plugin.properties") {
            expand(props)
        }
    }
    javadoc {
        applyLinks(
            "https://intellectualsites.github.io/fastasyncworldedit-javadocs/worldedit-core/",
        )
    }
}