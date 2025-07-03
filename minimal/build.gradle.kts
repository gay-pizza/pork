plugins {
  application
  id("gay.pizza.pork.module")
  id("com.gradleup.shadow") version "8.3.8"
  id("org.graalvm.buildtools.native") version "0.10.6"
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

graalvmNative {
  binaries {
    named("main") {
      imageName.set("pork-rt")
      mainClass.set("gay.pizza.pork.minimal.MainKt")
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
