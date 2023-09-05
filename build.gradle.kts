plugins {
  kotlin("jvm") version "1.9.10" apply false
  kotlin("plugin.serialization") version "1.9.10" apply false
}

tasks.withType<Wrapper> {
  gradleVersion = "8.3"
}
