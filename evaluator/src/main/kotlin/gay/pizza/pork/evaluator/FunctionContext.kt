package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.FunctionDefinition

class FunctionContext(val compilationUnitContext: CompilationUnitContext, val node: FunctionDefinition) : CallableFunction {
  override fun call(arguments: Arguments): Any {
    val scope = compilationUnitContext.internalScope.fork()
    for ((index, argumentSymbol) in node.arguments.withIndex()) {
      scope.define(argumentSymbol.id, arguments.values[index])
    }

    if (node.native != null) {
      val native = node.native!!
      val nativeFunctionProvider =
        compilationUnitContext.evaluator.nativeFunctionProvider(native.form.id)
      val nativeFunction = nativeFunctionProvider.provideNativeFunction(native.definition.text)
      return nativeFunction.call(arguments)
    }

    if (node.block == null) {
      throw RuntimeException("Native or Block is required for FunctionDefinition")
    }

    val visitor = EvaluationVisitor(scope)
    val blockFunction = visitor.visitBlock(node.block!!)
    return blockFunction.call()
  }
}
