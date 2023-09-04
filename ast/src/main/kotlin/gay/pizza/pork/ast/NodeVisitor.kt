package gay.pizza.pork.ast

interface NodeVisitor<T> {
  fun visitIntLiteral(node: IntLiteral): T
  fun visitStringLiteral(node: StringLiteral): T
  fun visitBooleanLiteral(node: BooleanLiteral): T
  fun visitListLiteral(node: ListLiteral): T
  fun visitSymbol(node: Symbol): T
  fun visitFunctionCall(node: FunctionCall): T
  fun visitLetAssignment(node: LetAssignment): T
  fun visitSymbolReference(node: SymbolReference): T
  fun visitLambda(node: Lambda): T
  fun visitParentheses(node: Parentheses): T
  fun visitPrefixOperation(node: PrefixOperation): T
  fun visitIf(node: If): T
  fun visitInfixOperation(node: InfixOperation): T
  fun visitFunctionDeclaration(node: FunctionDefinition): T
  fun visitBlock(node: Block): T

  fun visitImportDeclaration(node: ImportDeclaration): T
  fun visitCompilationUnit(node: CompilationUnit): T

  fun visitExpression(node: Expression): T = when (node) {
    is IntLiteral -> visitIntLiteral(node)
    is StringLiteral -> visitStringLiteral(node)
    is BooleanLiteral -> visitBooleanLiteral(node)
    is ListLiteral -> visitListLiteral(node)
    is FunctionCall -> visitFunctionCall(node)
    is LetAssignment -> visitLetAssignment(node)
    is SymbolReference -> visitSymbolReference(node)
    is Lambda -> visitLambda(node)
    is Parentheses -> visitParentheses(node)
    is PrefixOperation -> visitPrefixOperation(node)
    is If -> visitIf(node)
    is InfixOperation -> visitInfixOperation(node)
  }

  fun visitDeclaration(node: Declaration): T = when (node) {
    is ImportDeclaration -> visitImportDeclaration(node)
  }

  fun visitDefinition(node: Definition): T = when (node) {
    is FunctionDefinition -> visitFunctionDeclaration(node)
  }

  fun visit(node: Node): T = when (node) {
    is Symbol -> visitSymbol(node)
    is Expression -> visitExpression(node)
    is CompilationUnit -> visitCompilationUnit(node)
    is Block -> visitBlock(node)
    is Declaration -> visitDeclaration(node)
    is Definition -> visitDefinition(node)
  }

  fun visitNodes(vararg nodes: Node?): List<T> =
    nodes.asSequence().filterNotNull().map { visit(it) }.toList()

  fun visitAll(vararg nodeLists: List<Node>): List<T> =
    nodeLists.asSequence().flatten().map { visit(it) }.toList()
}
