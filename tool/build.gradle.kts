plugins {
  application
  id("gay.pizza.pork.module")
  id("com.gradleup.shadow") version "8.3.8"
}

dependencies {
  api(project(":minimal"))
  api(project(":compiler"))
  api(project(":vm"))
  api("com.github.ajalt.clikt:clikt:5.0.3")
  api("com.charleskorn.kaml:kaml:0.85.0")

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

tasks.run.get().outputs.upToDateWhen { false }
