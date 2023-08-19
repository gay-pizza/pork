package gay.pizza.pork.ast

interface Visitor<T> {
  fun visitDefine(node: Define): T
  fun visitFunctionCall(node: FunctionCall): T
  fun visitReference(node: SymbolReference): T
  fun visitSymbol(node: Symbol): T
  fun visitLambda(node: Lambda): T

  fun visitIntLiteral(node: IntLiteral): T
  fun visitBooleanLiteral(node: BooleanLiteral): T
  fun visitListLiteral(node: ListLiteral): T

  fun visitParentheses(node: Parentheses): T
  fun visitInfixOperation(node: InfixOperation): T

  fun visitProgram(node: Program): T

  fun visitExpression(node: Expression): T = when (node) {
    is IntLiteral -> visitIntLiteral(node)
    is BooleanLiteral -> visitBooleanLiteral(node)
    is ListLiteral -> visitListLiteral(node)
    is Parentheses -> visitParentheses(node)
    is InfixOperation -> visitInfixOperation(node)
    is Define -> visitDefine(node)
    is Lambda -> visitLambda(node)
    is FunctionCall -> visitFunctionCall(node)
    is SymbolReference -> visitReference(node)
    else -> throw RuntimeException("Unknown Expression")
  }

  fun visit(node: Node): T = when (node) {
    is Expression -> visitExpression(node)
    is Symbol -> visitSymbol(node)
    is Program -> visitProgram(node)
    else -> throw RuntimeException("Unknown Node")
  }
}
