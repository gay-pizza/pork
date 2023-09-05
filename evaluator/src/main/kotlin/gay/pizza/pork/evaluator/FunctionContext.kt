package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.FunctionDefinition

class FunctionContext(val node: FunctionDefinition, val internalScope: Scope) : CallableFunction {
  override fun call(arguments: Arguments): Any {
    val scope = internalScope.fork()
    for ((index, argumentSymbol) in node.arguments.withIndex()) {
      scope.define(argumentSymbol.id, arguments.values[index])
    }
    val visitor = EvaluationVisitor(scope)
    val blockFunction = visitor.visitBlock(node.block)
    return blockFunction.call()
  }
}
