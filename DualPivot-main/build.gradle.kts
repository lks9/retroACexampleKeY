/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java project to get you started.
 * For more details take a look at the Java Quickstart chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.6.1/userguide/tutorial_java_projects.html
 */

plugins {
    java

    application

    // Used to create a single executable jar file with all dependencies
    // see task "shadowJar" below
    // https://imperceptiblethoughts.com/shadow/
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
}

dependencies {
    // This dependency is used by the application.
    implementation(project(":DualPivot"))
}

application {
    // Define the main class for the application.
    mainClass = "java_copy.util.Main"
}

//shadowJar {
//    archiveClassifier = "exe"
//    archiveBaseName = "quicksort"
//    mergeServiceFiles()
//}