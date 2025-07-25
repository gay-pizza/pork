package gay.pizza.pork.tool

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import gay.pizza.dough.fs.PlatformFsProvider
import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.bir.IrSymbolGraph
import gay.pizza.pork.bir.IrWorld
import gay.pizza.pork.bytecode.CompiledWorld
import gay.pizza.pork.bytecode.ConstantTag
import gay.pizza.pork.bytecode.Opcode
import gay.pizza.pork.compiler.Compiler
import gay.pizza.pork.minimal.FileTool

class CompileCommand : CliktCommand("compile") {
  val showIrCode by option("--show-ir-code").flag(default = false)
  val showIrSymbolGraph by option("--show-ir-symbol-graph").flag(default = false)

  val path by argument("file")

  override fun help(context: Context): String = "Compile Pork"

  private val yaml = Yaml(
    configuration = Yaml.default.configuration.copy(
      polymorphismStyle = PolymorphismStyle.Property
    )
  )

  override fun run() {
    val tool = FileTool(PlatformFsProvider.resolve(path))
    val world = tool.buildWorld()
    val compiler = Compiler(world)
    val slab = world.load(tool.rootImportLocator)
    val compiledSlab = compiler.compilableSlabs.of(slab)
    val compiledMain = compiledSlab.resolve(Symbol("main"))
      ?: throw RuntimeException("'main' function not found.")
    val irWorld = compiler.compileIrWorld(compiledMain)
    val compiledWorld = compiler.compile(compiledMain)
    if (showIrCode) {
      printCompiledIr(irWorld)
    }

    if (showIrSymbolGraph) {
      val irSymbolGraph = IrSymbolGraph()
      irSymbolGraph.buildFromRoot(irWorld)
      println(yaml.encodeToString(IrSymbolGraph.serializer(), irSymbolGraph))
    }

    printCompiledWorld(compiledWorld)
  }

  private fun printCompiledIr(irWorld: IrWorld) {
    println(yaml.encodeToString(IrWorld.serializer(), irWorld))
  }

  private fun printCompiledWorld(compiledWorld: CompiledWorld) {
    for (symbol in compiledWorld.symbolTable.symbols) {
      val code = compiledWorld.code.subList(symbol.offset.toInt(), (symbol.offset + symbol.size).toInt())
      println(symbol.commonSymbolIdentity)
      for ((index, op) in code.withIndex()) {
        var annotation = ""
        val annotations = compiledWorld.annotations.filter { it.inst == (symbol.offset + index.toUInt()) }
        if (annotations.isNotEmpty()) {
          annotation = " ; ${annotations.joinToString(", ") { it.text }}"
        }
        print("  ${symbol.offset + index.toUInt()} ${op}${annotation}")
        if (op.code == Opcode.Constant || op.code == Opcode.NativeFunction || op.code == Opcode.NativeType) {
          val constant = compiledWorld.constantPool.constants[op.args[0].toInt()]
          val constantString = when (constant.tag) {
            ConstantTag.String -> "string = \"" + constant.readAsString() + "\""
            ConstantTag.NativeDefinition -> "native definition = " + constant.readAsNativeDefinition().joinToString(" ") { def -> "\"${def}\"" }
          }
          print(" ; constant: $constantString")
        }
        println()
      }
    }
  }
}
