import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    id("com.vanniktech.maven.publish")
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "AlpineCloud"
            url = uri("https://lib.alpn.cloud" + if (isRelease) "/releases" else "/snapshots")
            credentials {
                username = System.getenv("ALPINE_MAVEN_NAME")
                password = System.getenv("ALPINE_MAVEN_SECRET")
            }
        }
    }
}

extensions.configure(MavenPublishBaseExtension::class.java) {
    configure(
        JavaLibrary(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true
        )
    )

    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString(),
    )

    pom {
        name.set(project.name)
        description.set(project.description)
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