package gay.pizza.pork.buildext.ast

import gay.pizza.pork.buildext.codegen.KotlinWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.writeText

abstract class AstCodegenShared(val pkg: String, val outputDirectory: Path, val world: AstWorld) {
  abstract suspend fun generate()

  protected fun deleteAllContents() {
    for (child in outputDirectory.listDirectoryEntries("*.kt")) {
      child.deleteExisting()
    }
  }

  protected fun write(fileName: String, writer: KotlinWriter) {
    val content = "// GENERATED CODE FROM PORK AST CODEGEN\n$writer"
    val path = outputDirectory.resolve(fileName)
    path.deleteIfExists()
    path.writeText(content, StandardCharsets.UTF_8)
  }

  fun runUntilCompletion() {
    runBlocking {
      withContext(Dispatchers.IO) {
        generate()
      }
    }
  }
}
