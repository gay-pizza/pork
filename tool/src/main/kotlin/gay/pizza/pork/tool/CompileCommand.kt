package gay.pizza.pork.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.bir.IrWorld
import gay.pizza.pork.bytecode.CompiledWorld
import gay.pizza.pork.compiler.Compiler
import gay.pizza.pork.minimal.FileTool
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class CompileCommand : CliktCommand(help = "Compile Pork to Bytecode", name = "compile") {
  val path by argument("file")

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    val world = tool.buildWorld()
    val compiler = Compiler(world)
    val slab = world.load(tool.rootImportLocator)
    val compiledSlab = compiler.compilableSlabs.of(slab)
    val compiledMain = compiledSlab.resolve(Symbol("main"))
      ?: throw RuntimeException("'main' function not found.")
    val compiledWorld = compiler.compile(compiledMain)
    val irWorld = compiler.compileIrWorld()
    printCompiledIr(irWorld)
    printCompiledWorld(compiledWorld)
  }

  private fun printCompiledIr(irWorld: IrWorld) {
    val json = Json {
      prettyPrint = true
      prettyPrintIndent = "  "
    }
    println(json.encodeToString(IrWorld.serializer(), irWorld))
  }

  private fun printCompiledWorld(compiledWorld: CompiledWorld) {
    for (symbol in compiledWorld.symbolTable.symbols) {
      val code = compiledWorld.code.subList(symbol.offset.toInt(), (symbol.offset + symbol.size).toInt())
      println(symbol.commonSymbolIdentity)
      for ((index, op) in code.withIndex()) {
        var annotation = ""
        val annotations = compiledWorld.annotations.filter { it.inst == (symbol.offset + index.toUInt()) }
        if (annotations.isNotEmpty()) {
          annotation = " ; ${annotations.joinToString(", ") { it.text}}"
        }
        println("  ${symbol.offset + index.toUInt()} ${op}${annotation}")
      }
    }
  }
}
