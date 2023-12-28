package gay.pizza.pork.compiler

import gay.pizza.pork.ast.gen.FunctionDefinition
import gay.pizza.pork.ast.gen.LetDefinition
import gay.pizza.pork.ast.gen.NativeFunctionDescriptor
import gay.pizza.pork.ast.gen.visit
import gay.pizza.pork.bir.IrCodeBlock
import gay.pizza.pork.bir.IrDefinition
import gay.pizza.pork.bir.IrDefinitionType
import gay.pizza.pork.bir.IrSymbolTag
import gay.pizza.pork.frontend.scope.ScopeSymbol

class CompilableSymbol(val compilableSlab: CompilableSlab, val scopeSymbol: ScopeSymbol) {
  val compiledIrDefinition: IrDefinition by lazy { compileIrDefinition() }
  val compiledStubOps: CompiledSymbolResult by lazy { compile() }

  val usedSymbols: List<ScopeSymbol>
    get() = scopeSymbol.scope.usedSymbols

  private fun compile(): CompiledSymbolResult {
    val code = CodeBuilder(this)
    val ir = compiledIrDefinition
    val emitter = IrStubOpEmitter(ir, code)
    emitter.visit(ir.code)
    emitter.final()
    return emitter.code.build()
  }

  private fun compileIrDefinition(): IrDefinition {
    val compiler = compilableSlab.compiler
    val functionSymbol = compiler.irSymbolWorld.create(scopeSymbol, IrSymbolTag.Function, scopeSymbol.symbol.id)
    val irCodeEmitter = AstIrEmitter(
      self = functionSymbol,
      irSymbolWorld = compiler.irSymbolWorld,
      irSymbolAssignment = compiler.irSymbolAssignment,
      scope = compilableSlab.slab.scope
    )
    irCodeEmitter.enterLocalScope()
    val what = if (scopeSymbol.definition is FunctionDefinition) {
      val functionDefinition = scopeSymbol.definition as FunctionDefinition
      irCodeEmitter.createFunctionArguments(functionDefinition)
      functionDefinition.block ?: functionDefinition.nativeFunctionDescriptor!!
    } else {
      val letDefinition = scopeSymbol.definition as LetDefinition
      letDefinition.value
    }
    val type = if (what is NativeFunctionDescriptor) {
      IrDefinitionType.NativeFunction
    } else IrDefinitionType.CodeFunction
    val irCodeElement = irCodeEmitter.visit(what)
    val irCodeBlock = if (irCodeElement is IrCodeBlock) {
      irCodeElement
    } else IrCodeBlock(listOf(irCodeElement))
    irCodeEmitter.exitLocalScope()
    return IrDefinition(
      symbol = functionSymbol,
      type = type,
      arguments = irCodeEmitter.functionArguments,
      code = irCodeBlock
    )
  }

  val id: String
    get() = "${compilableSlab.slab.location.commonLocationIdentity} ${scopeSymbol.symbol.id}"

  override fun toString(): String = "${compilableSlab.slab.location.commonLocationIdentity} ${scopeSymbol.symbol.id}"
}
