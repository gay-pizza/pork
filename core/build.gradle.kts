plugins {
  application
  pork_module
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("org.graalvm.buildtools.native") version "0.9.25"
}

dependencies {
  api(project(":ast"))
  implementation(libs.clikt)
}

application {
  mainClass.set("gay.pizza.pork.cli.MainKt")
}

graalvmNative {
  binaries {
    named("main") {
      imageName.set("pork")
      mainClass.set("gay.pizza.pork.cli.MainKt")
      sharedLibrary.set(false)
    }
  }
}

tasks.run.get().outputs.upToDateWhen { false }
