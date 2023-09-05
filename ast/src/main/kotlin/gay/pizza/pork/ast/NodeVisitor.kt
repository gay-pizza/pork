package gay.pizza.pork.ast

interface NodeVisitor<T> {
  fun visitBlock(node: Block): T

  fun visitBooleanLiteral(node: BooleanLiteral): T

  fun visitCompilationUnit(node: CompilationUnit): T

  fun visitFunctionCall(node: FunctionCall): T

  fun visitFunctionDefinition(node: FunctionDefinition): T

  fun visitIf(node: If): T

  fun visitImportDeclaration(node: ImportDeclaration): T

  fun visitInfixOperation(node: InfixOperation): T

  fun visitIntLiteral(node: IntLiteral): T

  fun visitLambda(node: Lambda): T

  fun visitLetAssignment(node: LetAssignment): T

  fun visitListLiteral(node: ListLiteral): T

  fun visitParentheses(node: Parentheses): T

  fun visitPrefixOperation(node: PrefixOperation): T

  fun visitStringLiteral(node: StringLiteral): T

  fun visitSymbol(node: Symbol): T

  fun visitSymbolReference(node: SymbolReference): T

  fun visitNodes(vararg nodes: Node?): List<T> =
    nodes.asSequence().filterNotNull().map { visit(it) }.toList()

  fun visitAll(vararg nodeLists: List<Node>): List<T> =
    nodeLists.asSequence().flatten().map { visit(it) }.toList()

  fun visit(node: Node): T =
    when (node) {
      is Symbol -> visitSymbol(node)
      is Block -> visitBlock(node)
      is CompilationUnit -> visitCompilationUnit(node)
      is LetAssignment -> visitLetAssignment(node)
      is InfixOperation -> visitInfixOperation(node)
      is BooleanLiteral -> visitBooleanLiteral(node)
      is FunctionCall -> visitFunctionCall(node)
      is FunctionDefinition -> visitFunctionDefinition(node)
      is If -> visitIf(node)
      is ImportDeclaration -> visitImportDeclaration(node)
      is IntLiteral -> visitIntLiteral(node)
      is Lambda -> visitLambda(node)
      is ListLiteral -> visitListLiteral(node)
      is Parentheses -> visitParentheses(node)
      is PrefixOperation -> visitPrefixOperation(node)
      is StringLiteral -> visitStringLiteral(node)
      is SymbolReference -> visitSymbolReference(node)
    }
}
