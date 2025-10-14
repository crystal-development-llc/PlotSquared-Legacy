plugins {
    id("p2.build-logic")
}

subprojects {
    apply {
        plugin("p2.base-conventions")
        plugin("p2.publish-conventions")
    }
}