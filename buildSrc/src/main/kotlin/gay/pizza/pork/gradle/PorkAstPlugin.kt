package gay.pizza.pork.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class PorkAstPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.tasks.create("generateAstCode", GenerateAstCode::class.java)
  }
}
