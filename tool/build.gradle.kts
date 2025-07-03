plugins {
  application
  id("gay.pizza.pork.module")
  id("com.gradleup.shadow") version "8.3.8"
  id("org.graalvm.buildtools.native") version "0.10.6"
}

dependencies {
  api(project(":minimal"))
  api(project(":compiler"))
  api(project(":vm"))
  api("com.github.ajalt.clikt:clikt:5.0.3")
  api("com.charleskorn.kaml:kaml:0.83.0")

  implementation(project(":common"))
}

application {
  applicationName = "pork"
  mainClass.set("gay.pizza.pork.tool.MainKt")
  applicationDefaultJvmArgs += "-XstartOnFirstThread"
  applicationDefaultJvmArgs += "--enable-native-access=ALL-UNNAMED"
}

for (task in arrayOf(tasks.shadowDistTar, tasks.shadowDistZip, tasks.shadowJar)) {
  val suffix = when {
    task == tasks.shadowJar -> ""
    task.name.startsWith("shadow") -> "-shadow"
    else -> ""
  }
  task.get().archiveBaseName.set("pork${suffix}")
}

graalvmNative {
  binaries {
    named("main") {
      imageName.set("pork")
      mainClass.set("gay.pizza.pork.tool.MainKt")
      sharedLibrary.set(false)
      buildArgs("-march=compatibility")
      resources {
        includedPatterns.addAll(listOf(
          ".*/*.pork$",
          ".*/*.manifest$"
        ))
      }
    }
  }
}

tasks.run.get().outputs.upToDateWhen { false }
