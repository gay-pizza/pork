plugins {
  pork_module
}

dependencies {
  api(project(":ast"))
  api(project(":parser"))

  implementation(project(":common"))
}
