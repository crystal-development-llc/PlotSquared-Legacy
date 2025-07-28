import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    api(projects.plotsquaredCore)

    // Server
    compileOnly(libs.spigot.api)

    // Plugins
    compileOnly(libs.papi)
    compileOnly(libs.vault.api) {
        exclude(group = "org.bukkit")
    }
    compileOnly(libs.worldedit.bukkit) {
        exclude(group = "org.bukkit")
        exclude(group = "org.spigotmc")
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependsOn(":plotsquared-core:shadowJar")
    minimize()
    mergeServiceFiles()
}
