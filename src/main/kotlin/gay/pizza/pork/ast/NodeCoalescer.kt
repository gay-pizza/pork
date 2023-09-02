package gay.pizza.pork.ast

import gay.pizza.pork.ast.nodes.*

class NodeCoalescer(val handler: (Node) -> Unit) : NodeVisitor<Unit> {
  override fun visitIntLiteral(node: IntLiteral): Unit = handler(node)
  override fun visitStringLiteral(node: StringLiteral): Unit = handler(node)
  override fun visitBooleanLiteral(node: BooleanLiteral): Unit = handler(node)
  override fun visitListLiteral(node: ListLiteral): Unit = handler(node)
  override fun visitSymbol(node: Symbol): Unit = handler(node)
  override fun visitFunctionCall(node: FunctionCall): Unit = handler(node)
  override fun visitDefine(node: Define): Unit = handler(node)
  override fun visitSymbolReference(node: SymbolReference): Unit = handler(node)
  override fun visitLambda(node: Lambda): Unit = handler(node)
  override fun visitParentheses(node: Parentheses): Unit = handler(node)
  override fun visitPrefixOperation(node: PrefixOperation): Unit = handler(node)
  override fun visitIf(node: If): Unit = handler(node)
  override fun visitInfixOperation(node: InfixOperation): Unit = handler(node)
  override fun visitProgram(node: Program): Unit = handler(node)
}
