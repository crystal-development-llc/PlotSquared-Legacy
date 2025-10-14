plugins {
    id("p2.shadow-conventions")
}

dependencies {
    api(projects.plotsquaredCore)

    // Server
    compileOnly(libs.spigotApi)

    // Plugins
    compileOnly(libs.papi)
    compileOnly(libs.vault) {
        exclude(group = "org.bukkit")
    }
    compileOnly(libs.worldeditBukkit) {
        exclude(group = "org.bukkit")
        exclude(group = "org.spigotmc")
    }
}

tasks {
    processResources {
        val v = project.version.toString()
        inputs.property("version", v)
        filesMatching("plugin.yml") {
            expand("version" to v)
        }
    }
    javadoc {
        applyLinks(
            "https://hub.spigotmc.org/javadocs/spigot/",
            "https://intellectualsites.github.io/fastasyncworldedit-javadocs/worldedit-bukkit/",
        )
    }
}