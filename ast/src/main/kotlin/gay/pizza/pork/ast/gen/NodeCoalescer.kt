// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

class NodeCoalescer(val followChildren: Boolean = true, val handler: (Node) -> Unit) : NodeVisitor<Unit> {
  override fun visitArgumentSpec(node: ArgumentSpec): Unit =
    handle(node)

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

  override fun visitDoubleLiteral(node: DoubleLiteral): Unit =
    handle(node)

  override fun visitForIn(node: ForIn): Unit =
    handle(node)

  override fun visitForInItem(node: ForInItem): Unit =
    handle(node)

  override fun visitFunctionCall(node: FunctionCall): Unit =
    handle(node)

  override fun visitFunctionDefinition(node: FunctionDefinition): Unit =
    handle(node)

  override fun visitIf(node: If): Unit =
    handle(node)

  override fun visitImportDeclaration(node: ImportDeclaration): Unit =
    handle(node)

  override fun visitImportPath(node: ImportPath): Unit =
    handle(node)

  override fun visitIndexedBy(node: IndexedBy): Unit =
    handle(node)

  override fun visitIndexedSetAssignment(node: IndexedSetAssignment): Unit =
    handle(node)

  override fun visitInfixOperation(node: InfixOperation): Unit =
    handle(node)

  override fun visitIntegerLiteral(node: IntegerLiteral): Unit =
    handle(node)

  override fun visitLetAssignment(node: LetAssignment): Unit =
    handle(node)

  override fun visitLetDefinition(node: LetDefinition): Unit =
    handle(node)

  override fun visitListLiteral(node: ListLiteral): Unit =
    handle(node)

  override fun visitLongLiteral(node: LongLiteral): Unit =
    handle(node)

  override fun visitNativeFunctionDescriptor(node: NativeFunctionDescriptor): Unit =
    handle(node)

  override fun visitNativeTypeDescriptor(node: NativeTypeDescriptor): Unit =
    handle(node)

  override fun visitNoneLiteral(node: NoneLiteral): Unit =
    handle(node)

  override fun visitParentheses(node: Parentheses): Unit =
    handle(node)

  override fun visitPrefixOperation(node: PrefixOperation): Unit =
    handle(node)

  override fun visitReturn(node: Return): Unit =
    handle(node)

  override fun visitStringLiteral(node: StringLiteral): Unit =
    handle(node)

  override fun visitSuffixOperation(node: SuffixOperation): Unit =
    handle(node)

  override fun visitSymbol(node: Symbol): Unit =
    handle(node)

  override fun visitSymbolReference(node: SymbolReference): Unit =
    handle(node)

  override fun visitSymbolSetAssignment(node: SymbolSetAssignment): Unit =
    handle(node)

  override fun visitTypeDefinition(node: TypeDefinition): Unit =
    handle(node)

  override fun visitTypeSpec(node: TypeSpec): Unit =
    handle(node)

  override fun visitVarAssignment(node: VarAssignment): Unit =
    handle(node)

  override fun visitWhile(node: While): Unit =
    handle(node)

  fun handle(node: Node) {
    handler(node)
    if (followChildren) {
      node.visitChildren(this)
    }
  }
}
