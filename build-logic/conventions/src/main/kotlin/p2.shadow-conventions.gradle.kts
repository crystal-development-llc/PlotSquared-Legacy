import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("p2.base-conventions")
    id("com.gradleup.shadow")
}

shadow {
    addShadowVariantIntoJavaComponent.set(false)
}

tasks {
    named<Jar>("jar") {
        archiveClassifier.set("unshaded")
    }
    named<ShadowJar>("shadowJar") {
        // https://gradleup.com/shadow/configuration/merging/#handling-duplicates-strategy
        duplicatesStrategy = DuplicatesStrategy.WARN
        failOnDuplicateEntries.set(true)
        archiveClassifier.set("")

        mergeServiceFiles()
    }
    named("assemble") {
        dependsOn(named("shadowJar"))
    }
}