plugins {
  application
  id("gay.pizza.pork.module")
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("org.graalvm.buildtools.native") version "0.9.25"
}

dependencies {
  api(project(":ast"))
  api(project(":parser"))
  api(project(":frontend"))
  api(project(":evaluator"))
  implementation(libs.clikt)
  implementation(project(":common"))
}

application {
  applicationName = "pork"
  mainClass.set("gay.pizza.pork.tool.MainKt")
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
    }
  }
}

tasks.run.get().outputs.upToDateWhen { false }
