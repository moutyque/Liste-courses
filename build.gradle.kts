buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android").version("1.7.0").apply(false)
    kotlin("jvm") version "1.6.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

allprojects {
    ext {
        set("supportLibraryVersion", "26.0.1")
    }
}