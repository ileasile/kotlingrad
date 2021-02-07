import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  idea
  id("com.github.ben-manes.versions") version "0.36.0"
  kotlin("jvm") version "1.4.30" // Keep in sync with README
}

idea.module {
  excludeDirs.add(file("latex"))
  isDownloadJavadoc = true
  isDownloadSources = true
}

allprojects {
  repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/mipt-npm/dev")
    maven("https://dl.bintray.com/hotkeytlt/maven")
    maven("https://dl.bintray.com/egor-bogomolov/astminer")
    maven("https://maven.jzy3d.org/releases")
    maven("https://jetbrains.bintray.com/lets-plot-maven")
  }

  group = "com.github.breandan"
  version = "0.4.0"

  apply(plugin = "org.jetbrains.kotlin.jvm")

  dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib-jdk8"))
  }

  tasks {
    withType<KotlinCompile> {
      kotlinOptions {
//        languageVersion = "1.5"
//        apiVersion = "1.5"
        jvmTarget = VERSION_1_8.toString()
      }
    }

    test {
      minHeapSize = "1024m"
      maxHeapSize = "4096m"
      useJUnitPlatform()
      testLogging {
        events = setOf(FAILED, PASSED, SKIPPED, STANDARD_OUT)
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
      }
    }
  }
}