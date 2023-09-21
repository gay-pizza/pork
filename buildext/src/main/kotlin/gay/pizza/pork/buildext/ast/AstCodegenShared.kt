package gay.pizza.pork.buildext.ast

import gay.pizza.pork.buildext.codegen.KotlinWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.deleteExisting
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.writeBytes

abstract class AstCodegenShared(val pkg: String, val outputDirectory: Path, val world: AstWorld) {
  abstract suspend fun generate()

  protected fun deleteAllContents() {
    for (child in outputDirectory.listDirectoryEntries("*.kt")) {
      child.deleteExisting()
    }
  }

  protected fun write(fileName: String, writer: KotlinWriter) {
    writeWithHeader(fileName, writer.buffer)
  }

  private fun writeWithHeader(fileName: String, content: CharSequence) {
    val textContent = buildString {
      append("// GENERATED CODE FROM PORK AST CODEGEN\n")
      append(content)
    }
    val path = outputDirectory.resolve(fileName)
    val bytes = textContent.toByteArray(StandardCharsets.UTF_8)
    path.writeBytes(bytes, StandardOpenOption.CREATE_NEW)
  }

  fun runUntilCompletion() {
    runBlocking {
      withContext(Dispatchers.IO) {
        generate()
      }
    }
  }
}
