// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

class NodeCoalescer(val handler: (Node) -> Unit) : NodeVisitor<Unit> {
  override fun visitBlock(node: Block): Unit =
    handle(node)

  override fun visitBooleanLiteral(node: BooleanLiteral): Unit =
    handle(node)

  override fun visitBreak(node: Break): Unit =
    handle(node)

  override fun visitCompilationUnit(node: CompilationUnit): Unit =
    handle(node)

  override fun visitContinue(node: Continue): Unit =
    handle(node)

  override fun visitFunctionCall(node: FunctionCall): Unit =
    handle(node)

  override fun visitFunctionDefinition(node: FunctionDefinition): Unit =
    handle(node)

  override fun visitIf(node: If): Unit =
    handle(node)

  override fun visitImportDeclaration(node: ImportDeclaration): Unit =
    handle(node)

  override fun visitInfixOperation(node: InfixOperation): Unit =
    handle(node)

  override fun visitIntLiteral(node: IntLiteral): Unit =
    handle(node)

  override fun visitLetAssignment(node: LetAssignment): Unit =
    handle(node)

  override fun visitListLiteral(node: ListLiteral): Unit =
    handle(node)

  override fun visitNative(node: Native): Unit =
    handle(node)

  override fun visitParentheses(node: Parentheses): Unit =
    handle(node)

  override fun visitPrefixOperation(node: PrefixOperation): Unit =
    handle(node)

  override fun visitStringLiteral(node: StringLiteral): Unit =
    handle(node)

  override fun visitSymbol(node: Symbol): Unit =
    handle(node)

  override fun visitSymbolReference(node: SymbolReference): Unit =
    handle(node)

  override fun visitWhile(node: While): Unit =
    handle(node)

  fun handle(node: Node) {
    handler(node)
    node.visitChildren(this)
  }
}
