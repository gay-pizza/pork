package gay.pizza.pork.ast

import gay.pizza.pork.ast.nodes.*

class NodeCoalescer(val handler: (Node) -> Unit) : NodeVisitor<Unit> {
  override fun visitDefine(node: Define) = handler(node)
  override fun visitFunctionCall(node: FunctionCall) = handler(node)
  override fun visitReference(node: SymbolReference) = handler(node)
  override fun visitIf(node: If) = handler(node)
  override fun visitSymbol(node: Symbol) = handler(node)
  override fun visitLambda(node: Lambda) = handler(node)
  override fun visitIntLiteral(node: IntLiteral) = handler(node)
  override fun visitBooleanLiteral(node: BooleanLiteral) = handler(node)
  override fun visitListLiteral(node: ListLiteral) = handler(node)
  override fun visitParentheses(node: Parentheses) = handler(node)
  override fun visitPrefixOperation(node: PrefixOperation) = handler(node)
  override fun visitInfixOperation(node: InfixOperation) = handler(node)
  override fun visitProgram(node: Program) = handler(node)
}
