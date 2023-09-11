package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.FunctionDefinition

class FunctionContext(val compilationUnitContext: CompilationUnitContext, val node: FunctionDefinition) : CallableFunction {
  private fun resolveMaybeNative(): CallableFunction? = if (node.native == null) {
    null
  } else {
    val native = node.native!!
    val nativeFunctionProvider =
      compilationUnitContext.evaluator.nativeFunctionProvider(native.form.id)
    nativeFunctionProvider.provideNativeFunction(native.definition.text, node.arguments)
  }

  private val nativeCached by lazy { resolveMaybeNative() }

  override fun call(arguments: Arguments): Any {
    if (nativeCached != null) {
      return nativeCached!!.call(arguments)
    }

    val scope = compilationUnitContext.internalScope.fork()
    for ((index, spec) in node.arguments.withIndex()) {
      if (spec.multiple) {
        val list = arguments.values.subList(index, arguments.values.size - 1)
        scope.define(spec.symbol.id, list)
        break
      } else {
        scope.define(spec.symbol.id, arguments.values[index])
      }
    }

    if (node.block == null) {
      throw RuntimeException("Native or Block is required for FunctionDefinition")
    }

    val visitor = EvaluationVisitor(scope)
    val blockFunction = visitor.visitBlock(node.block!!)
    return blockFunction.call()
  }
}
