package gay.pizza.pork.ast

import gay.pizza.pork.ast.nodes.*

interface NodeVisitor<T> {
  fun visitDefine(node: Define): T
  fun visitFunctionCall(node: FunctionCall): T
  fun visitReference(node: SymbolReference): T
  fun visitIf(node: If): T
  fun visitSymbol(node: Symbol): T
  fun visitLambda(node: Lambda): T

  fun visitIntLiteral(node: IntLiteral): T
  fun visitBooleanLiteral(node: BooleanLiteral): T
  fun visitListLiteral(node: ListLiteral): T
  fun visitStringLiteral(node: StringLiteral): T

  fun visitParentheses(node: Parentheses): T
  fun visitPrefixOperation(node: PrefixOperation): T
  fun visitInfixOperation(node: InfixOperation): T

  fun visitProgram(node: Program): T

  fun visitExpression(node: Expression): T = when (node) {
    is IntLiteral -> visitIntLiteral(node)
    is BooleanLiteral -> visitBooleanLiteral(node)
    is ListLiteral -> visitListLiteral(node)
    is StringLiteral -> visitStringLiteral(node)
    is Parentheses -> visitParentheses(node)
    is InfixOperation -> visitInfixOperation(node)
    is PrefixOperation -> visitPrefixOperation(node)
    is Define -> visitDefine(node)
    is Lambda -> visitLambda(node)
    is FunctionCall -> visitFunctionCall(node)
    is SymbolReference -> visitReference(node)
    is If -> visitIf(node)
  }

  fun visit(node: Node): T = when (node) {
    is Expression -> visitExpression(node)
    is Symbol -> visitSymbol(node)
    is Program -> visitProgram(node)
  }

  fun visitNodes(vararg nodes: Node?): List<T> =
    nodes.filterNotNull().map { visit(it) }

  fun visitAll(vararg nodeLists: List<Node>): List<T> =
    nodeLists.asSequence().flatten().map { visit(it) }.toList()
}
