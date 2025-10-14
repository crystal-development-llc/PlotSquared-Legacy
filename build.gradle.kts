plugins {
    id("p2.build-logic")
}

subprojects {
    apply {
        plugin("p2.shadow-conventions")
    }
}