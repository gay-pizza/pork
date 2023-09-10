// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

interface NodeVisitor<T> {
  fun visitBlock(node: Block): T

  fun visitBooleanLiteral(node: BooleanLiteral): T

  fun visitBreak(node: Break): T

  fun visitCompilationUnit(node: CompilationUnit): T

  fun visitContinue(node: Continue): T

  fun visitDoubleLiteral(node: DoubleLiteral): T

  fun visitFunctionCall(node: FunctionCall): T

  fun visitFunctionDefinition(node: FunctionDefinition): T

  fun visitIf(node: If): T

  fun visitImportDeclaration(node: ImportDeclaration): T

  fun visitInfixOperation(node: InfixOperation): T

  fun visitIntegerLiteral(node: IntegerLiteral): T

  fun visitLetAssignment(node: LetAssignment): T

  fun visitListLiteral(node: ListLiteral): T

  fun visitNative(node: Native): T

  fun visitParentheses(node: Parentheses): T

  fun visitPrefixOperation(node: PrefixOperation): T

  fun visitSetAssignment(node: SetAssignment): T

  fun visitStringLiteral(node: StringLiteral): T

  fun visitSymbol(node: Symbol): T

  fun visitSymbolReference(node: SymbolReference): T

  fun visitVarAssignment(node: VarAssignment): T

  fun visitWhile(node: While): T
}
