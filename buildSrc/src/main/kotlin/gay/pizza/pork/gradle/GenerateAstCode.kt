package gay.pizza.pork.gradle

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import gay.pizza.pork.gradle.ast.AstDescription
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import gay.pizza.pork.gradle.ast.AstWorld
import java.io.File

open class GenerateAstCode : DefaultTask() {
  init {
    outputs.upToDateWhen { false }
  }

  @get:InputFile
  val astDescriptionFile: File = project.file("src/main/ast/pork.yml")

  @TaskAction
  fun generate() {
    val astYamlText = astDescriptionFile.readText()
    val mapper = ObjectMapper(YAMLFactory())
    mapper.registerModules(KotlinModule.Builder().build())
    val astDescription = mapper.readValue(astYamlText, AstDescription::class.java)
    val world = AstWorld()
    world.build(astDescription)
  }
}
