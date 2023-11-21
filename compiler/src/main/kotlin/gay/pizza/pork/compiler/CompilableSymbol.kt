package gay.pizza.pork.compiler

import gay.pizza.pork.ast.gen.FunctionDefinition
import gay.pizza.pork.ast.gen.LetDefinition
import gay.pizza.pork.ast.gen.visit
import gay.pizza.pork.frontend.scope.ScopeSymbol

class CompilableSymbol(val compilableSlab: CompilableSlab, val scopeSymbol: ScopeSymbol) {
  val compiledStubOps: CompiledSymbolResult by lazy { compile() }

  val usedSymbols: List<ScopeSymbol>
    get() = scopeSymbol.scope.usedSymbols

  private fun compile(): CompiledSymbolResult {
    val emitter = StubOpEmitter(compilableSlab.compiler, this)
    emitter.enter()
    val what = if (scopeSymbol.definition is FunctionDefinition) {
      val functionDefinition = scopeSymbol.definition as FunctionDefinition
      emitter.allocateOuterScope(functionDefinition)
      functionDefinition.block ?: functionDefinition.nativeFunctionDescriptor!!
    } else {
      val letDefinition = scopeSymbol.definition as LetDefinition
      letDefinition.value
    }
    emitter.visit(what)
    emitter.exit()
    return emitter.code.build()
  }

  val id: String
    get() = "${compilableSlab.slab.location.commonFriendlyName} ${scopeSymbol.symbol.id}"

  override fun toString(): String = "${compilableSlab.slab.location.commonFriendlyName} ${scopeSymbol.symbol.id}"
}
