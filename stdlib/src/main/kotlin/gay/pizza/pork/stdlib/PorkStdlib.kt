package gay.pizza.pork.stdlib

import gay.pizza.pork.frontend.ContentSource
import gay.pizza.pork.parser.CharSource
import gay.pizza.pork.parser.StringCharSource

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

  private fun read(path: String): String {
    val stream = stdlibClass.getResourceAsStream("/pork/stdlib/${path}")
      ?: throw RuntimeException("Stdlib does not contain file '${path}'")
    return String(stream.readAllBytes())
  }

  override fun loadAsCharSource(path: String): CharSource {
    return StringCharSource(read(path))
  }

  override fun stableContentIdentity(path: String): String {
    return path
  }
}
