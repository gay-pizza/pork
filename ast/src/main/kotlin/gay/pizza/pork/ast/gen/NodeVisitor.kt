// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

interface NodeVisitor<T> {
  fun visitArgumentSpec(node: ArgumentSpec): T

  fun visitBlock(node: Block): T

  fun visitBooleanLiteral(node: BooleanLiteral): T

  fun visitBreak(node: Break): T

  fun visitCompilationUnit(node: CompilationUnit): T

  fun visitContinue(node: Continue): T

  fun visitDoubleLiteral(node: DoubleLiteral): T

  fun visitForIn(node: ForIn): T

  fun visitForInItem(node: ForInItem): T

  fun visitFunctionCall(node: FunctionCall): T

  fun visitFunctionDefinition(node: FunctionDefinition): T

  fun visitIf(node: If): T

  fun visitImportDeclaration(node: ImportDeclaration): T

  fun visitImportPath(node: ImportPath): T

  fun visitIndexedBy(node: IndexedBy): T

  fun visitIndexedSetAssignment(node: IndexedSetAssignment): T

  fun visitInfixOperation(node: InfixOperation): T

  fun visitIntegerLiteral(node: IntegerLiteral): T

  fun visitLetAssignment(node: LetAssignment): T

  fun visitLetDefinition(node: LetDefinition): T

  fun visitListLiteral(node: ListLiteral): T

  fun visitLongLiteral(node: LongLiteral): T

  fun visitNativeFunctionDescriptor(node: NativeFunctionDescriptor): T

  fun visitNativeTypeDescriptor(node: NativeTypeDescriptor): T

  fun visitNoneLiteral(node: NoneLiteral): T

  fun visitParentheses(node: Parentheses): T

  fun visitPrefixOperation(node: PrefixOperation): T

  fun visitReturn(node: Return): T

  fun visitStringLiteral(node: StringLiteral): T

  fun visitSuffixOperation(node: SuffixOperation): T

  fun visitSymbol(node: Symbol): T

  fun visitSymbolReference(node: SymbolReference): T

  fun visitSymbolSetAssignment(node: SymbolSetAssignment): T

  fun visitTypeDefinition(node: TypeDefinition): T

  fun visitTypeSpec(node: TypeSpec): T

  fun visitVarAssignment(node: VarAssignment): T

  fun visitWhile(node: While): T
}
