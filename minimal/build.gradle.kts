plugins {
  application
  id("gay.pizza.pork.module")
  id("com.gradleup.shadow") version "8.3.8"
}

dependencies {
  api(project(":ast"))
  api(project(":parser"))
  api(project(":frontend"))
  api(project(":evaluator"))
  api(project(":vm"))
  api(project(":stdlib"))
  api(project(":ffi"))
  implementation(project(":common"))
}

application {
  applicationName = "pork-rt"
  mainClass.set("gay.pizza.pork.minimal.MainKt")
  applicationDefaultJvmArgs += "-XstartOnFirstThread"
  applicationDefaultJvmArgs += "--enable-native-access=ALL-UNNAMED"
}

for (task in arrayOf(tasks.shadowDistTar, tasks.shadowDistZip, tasks.shadowJar)) {
  val suffix = when {
    task == tasks.shadowJar -> ""
    task.name.startsWith("shadow") -> "-shadow"
    else -> ""
  }
  task.get().archiveBaseName.set("pork-rt${suffix}")
}

tasks.run.get().outputs.upToDateWhen { false }
