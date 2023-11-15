package gay.pizza.pork.stdlib

import gay.pizza.pork.frontend.ContentSource
import gay.pizza.pork.tokenizer.CharSource
import gay.pizza.pork.tokenizer.StringCharSource

object PorkStdlib : ContentSource {
  private val stdlibClass = PorkStdlib::class.java

  private fun readManifestFiles(): List<String> {
    val manifestContent = read("stdlib.manifest")
    return manifestContent.split("\n").filter { line ->
      val trimmed = line.trim()
      trimmed.isNotEmpty() && !trimmed.startsWith("#")
    }
  }

  val files: List<String> = readManifestFiles()

  private fun readPorkFile(path: String): String {
    if (!files.contains(path)) {
      throw RuntimeException("Stdlib does not contain file '${path}'")
    }
    return read("stdlib/${path}")
  }

  private fun read(path: String): String {
    val stream = stdlibClass.getResourceAsStream("/pork/${path}")
      ?: throw RuntimeException("Unable to find file '${path}'")
    return String(stream.readAllBytes())
  }

  override fun loadAsCharSource(path: String): CharSource {
    return StringCharSource(readPorkFile(path))
  }

  override fun stableContentPath(path: String): String {
    return path
  }
}
