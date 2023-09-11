plugins {
  id("gay.pizza.pork.module")
  id("gay.pizza.pork.stdlib")
}

tasks.processResources {
  dependsOn(tasks.generateStdlibManifest)
  inputs.file(tasks.generateStdlibManifest.get().stdlibManifestFile)

  from("src/main/pork") {
    into("pork/stdlib")
  }

  from(tasks.generateStdlibManifest.get().outputs) {
    into("pork")
  }
}

dependencies {
  implementation(project(":frontend"))
}
