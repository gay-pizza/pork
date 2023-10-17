package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.dough.fs.createDirectories
import gay.pizza.dough.fs.exists
import gay.pizza.dough.fs.writeString
import gay.pizza.pork.tokenizer.readToString
import gay.pizza.pork.stdlib.PorkStdlib

class CopyStdlibCommand : CliktCommand(help = "Copy Stdlib", name = "copy-stdlib") {
  val output by argument("output").default("stdlib")

  override fun run() {
    val outputFsPath = PlatformFsProvider.resolve(output)
    for (filePath in PorkStdlib.files) {
      val outputFilePath = outputFsPath.resolve(filePath)
      if (outputFilePath.parent?.exists() == false) {
        outputFilePath.parent?.createDirectories()
      }
      outputFilePath.writeString(PorkStdlib.loadAsCharSource(filePath).readToString())
    }
  }
}
