package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.FunctionDefinition

class FunctionContext(val compilationUnitContext: CompilationUnitContext, val node: FunctionDefinition) : CallableFunction {
  private fun resolveMaybeNative(): CallableFunction? = if (node.native == null) {
    null
  } else {
    val native = node.native!!
    val nativeFunctionProvider =
      compilationUnitContext.evaluator.nativeFunctionProvider(native.form.id)
    nativeFunctionProvider.provideNativeFunction(native.definition.text)
  }

  private val nativeCached by lazy { resolveMaybeNative() }

  override fun call(arguments: Arguments): Any {
    if (nativeCached != null) {
      return nativeCached!!.call(arguments)
    }

    val scope = compilationUnitContext.internalScope.fork()
    for ((index, argumentSymbol) in node.arguments.withIndex()) {
      scope.define(argumentSymbol.id, arguments.values[index])
    }

    if (node.block == null) {
      throw RuntimeException("Native or Block is required for FunctionDefinition")
    }

    val visitor = EvaluationVisitor(scope)
    val blockFunction = visitor.visitBlock(node.block!!)
    return blockFunction.call()
  }
}
