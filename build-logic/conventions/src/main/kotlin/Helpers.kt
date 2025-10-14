import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.getByType
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.text.trim

val Project.libs: LibrariesForLibs
    get() = rootProject.extensions.getByType()

val Project.isRelease: Boolean
    get() = !project.version.toString().contains("-")

fun Javadoc.applyLinks(vararg links: String) {
    (options as StandardJavadocDocletOptions).apply {
        links(*links)
    }
}

fun Project.latestCommitHash(): String {
    return runGitCommand(listOf("rev-parse", "--short=7", "HEAD"))
}

fun Project.latestCommitDateTime(): String {
    return runGitCommand(listOf("log", "-1", "--format=%cd", "--date=format:%y.%m.%d", "HEAD"))
}

fun Project.runGitCommand(args: List<String>): String {
    return providers.of(GitCommand::class.java) { parameters.args.set(args) }.getOrNull() ?: "unknown"
}

// https://github.com/ViaVersion/ViaVersion/blob/84296898378d24d0ae1fa21c0a91e8a2f81d973a/build-logic/src/main/kotlin/extensions.kt#L27
abstract class GitCommand : ValueSource<String, GitCommand.GitCommandParameters> {

    @get:Inject
    abstract val execOperations: ExecOperations

    interface GitCommandParameters : ValueSourceParameters {
        val args: ListProperty<String>
    }

    override fun obtain(): String? {
        try {
            val command = listOf("git") + parameters.args.get()
            val output = ByteArrayOutputStream()
            execOperations.exec {
                commandLine = command
                standardOutput = output
                isIgnoreExitValue = true
            }

            return output.toString(Charsets.UTF_8).trim().takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            return null
        }
    }
}