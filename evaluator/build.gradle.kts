plugins {
  pork_module
}

dependencies {
  api(project(":ast"))
  api(project(":frontend"))

  implementation(project(":common"))
}
