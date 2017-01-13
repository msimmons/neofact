
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.script.lang.kotlin.*

val kotlinVersion by project
val gradleVersion by project

buildscript {
    val kotlinVersion by extra.properties
    repositories{
        mavenCentral()
        maven {
            setUrl("http://repo.spring.io/snapshot")
        }
        maven {
            setUrl("http://repo.spring.io/milestone")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

apply {
    plugin("kotlin")
    plugin<PublishingPlugin>()
    plugin<MavenPlugin>()
}

group = "com.cinchfinancial"
version = "1.0.0"

repositories {
    mavenCentral()
    jcenter()
}

val plantuml = configurations.create("plantuml")

dependencies {
    compile("org.apache.poi", "poi", "3.14")
    plantuml("net.sourceforge.plantuml:plantuml:8041")
}

task<Wrapper>("wrapper") {
    gradleVersion = "$gradleVersion"
}

task<JavaExec>("plantuml") {
    val srcFiles = "$projectDir/src/docs/plantuml/**"
    val outDir = "$buildDir/plantuml"
    inputs.files(fileTree("$projectDir/src/docs/plantuml/"))
    outputs.files(fileTree(outDir))
    main = "net.sourceforge.plantuml.Run"
    jvmArgs("-Djava.awt.headless=true")
    args("-o", outDir, srcFiles)
    classpath = plantuml
    doFirst {
        mkdir("$buildDir/plantuml")
    }
}

