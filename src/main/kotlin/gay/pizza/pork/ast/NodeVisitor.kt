package gay.pizza.pork.ast

import gay.pizza.pork.ast.nodes.*

interface NodeVisitor<T> {
  fun visitIntLiteral(node: IntLiteral): T
  fun visitStringLiteral(node: StringLiteral): T
  fun visitBooleanLiteral(node: BooleanLiteral): T
  fun visitListLiteral(node: ListLiteral): T
  fun visitSymbol(node: Symbol): T
  fun visitFunctionCall(node: FunctionCall): T
  fun visitDefine(node: Define): T
  fun visitSymbolReference(node: SymbolReference): T
  fun visitLambda(node: Lambda): T
  fun visitParentheses(node: Parentheses): T
  fun visitPrefixOperation(node: PrefixOperation): T
  fun visitIf(node: If): T
  fun visitInfixOperation(node: InfixOperation): T
  fun visitProgram(node: Program): T

  fun visitExpression(node: Expression): T = when (node) {
    is IntLiteral -> visitIntLiteral(node)
    is StringLiteral -> visitStringLiteral(node)
    is BooleanLiteral -> visitBooleanLiteral(node)
    is ListLiteral -> visitListLiteral(node)
    is FunctionCall -> visitFunctionCall(node)
    is Define -> visitDefine(node)
    is SymbolReference -> visitSymbolReference(node)
    is Lambda -> visitLambda(node)
    is Parentheses -> visitParentheses(node)
    is PrefixOperation -> visitPrefixOperation(node)
    is If -> visitIf(node)
    is InfixOperation -> visitInfixOperation(node)
  }

  fun visit(node: Node): T = when (node) {
    is Symbol -> visitSymbol(node)
    is Expression -> visitExpression(node)
    is Program -> visitProgram(node)
  }

  fun visitNodes(vararg nodes: Node?): List<T> =
    nodes.asSequence().filterNotNull().map { visit(it) }.toList()

  fun visitAll(vararg nodeLists: List<Node>): List<T> =
    nodeLists.asSequence().flatten().map { visit(it) }.toList()
}
