// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

fun <T> NodeVisitor<T>.visit(node: Node): T =
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
    is ListLiteral -> visitListLiteral(node)
    is Parentheses -> visitParentheses(node)
    is PrefixOperation -> visitPrefixOperation(node)
    is StringLiteral -> visitStringLiteral(node)
    is SymbolReference -> visitSymbolReference(node)
  }

fun <T> NodeVisitor<T>.visitNodes(vararg nodes: Node?): List<T> =
  nodes.asSequence().filterNotNull().map { visit(it) }.toList()

fun <T> NodeVisitor<T>.visitAll(vararg nodeLists: List<Node>): List<T> =
  nodeLists.asSequence().flatten().map { visit(it) }.toList()
