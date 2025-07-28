import java.time.format.DateTimeFormatter

dependencies {
    compileOnlyApi(libs.annotations)

    // Server
    compileOnlyApi(libs.gson)
    compileOnlyApi(libs.guava)
    compileOnlyApi(libs.snakeyaml)

    // Plugins
    compileOnly(libs.worldedit.core) {
        exclude(group = "bukkit-classloader-check")
        exclude(group = "mockito-core")
        exclude(group = "dummypermscompat")
    }

    // Testing
    testImplementation(rootProject.libs.guava)
    testImplementation(rootProject.libs.junit.jupiter)
    testRuntimeOnly(rootProject.libs.junit.platform.launcher)
}

tasks.processResources {
    filesMatching("plugin.properties") {
        expand(
                "version" to project.version.toString(),
                "commit" to rootProject.grgit.head().abbreviatedId,
                "date" to rootProject.grgit.head().dateTime.format(DateTimeFormatter.ofPattern("yy.MM.dd"))
        )
    }

    doLast {
        copy {
            from(layout.buildDirectory.file("$rootDir/LICENSE"))
            into(layout.buildDirectory.dir("resources/main"))
        }
    }
}
