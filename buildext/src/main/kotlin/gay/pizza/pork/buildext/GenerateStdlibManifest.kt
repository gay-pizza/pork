package gay.pizza.pork.buildext

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

open class GenerateStdlibManifest : DefaultTask() {
  @get:InputDirectory
  val porkStdlibCode: File = project.file("src/main/pork")

  @get:OutputFile
  val stdlibManifestFile: Provider<RegularFile> =
    project.layout.buildDirectory.file("generated/pork/stdlib.manifest")

  @TaskAction
  fun generate() {
    val files = porkStdlibCode.walkTopDown().mapNotNull { file ->
      if (!file.isFile || !file.name.endsWith(".pork")) {
        null
      } else {
        file.relativeTo(porkStdlibCode).path
      }
    }
    val manifestFilePath = stdlibManifestFile.get().asFile.toPath()
    manifestFilePath.deleteIfExists()
    manifestFilePath.writeText(files.joinToString("\n") + "\n")
  }
}
