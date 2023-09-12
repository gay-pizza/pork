// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

fun <T> NodeVisitor<T>.visit(node: Node): T =
  when (node) {
    is Symbol -> visitSymbol(node)
    is Block -> visitBlock(node)
    is CompilationUnit -> visitCompilationUnit(node)
    is LetAssignment -> visitLetAssignment(node)
    is VarAssignment -> visitVarAssignment(node)
    is SetAssignment -> visitSetAssignment(node)
    is InfixOperation -> visitInfixOperation(node)
    is BooleanLiteral -> visitBooleanLiteral(node)
    is FunctionCall -> visitFunctionCall(node)
    is FunctionDefinition -> visitFunctionDefinition(node)
    is LetDefinition -> visitLetDefinition(node)
    is If -> visitIf(node)
    is ImportDeclaration -> visitImportDeclaration(node)
    is IntegerLiteral -> visitIntegerLiteral(node)
    is LongLiteral -> visitLongLiteral(node)
    is DoubleLiteral -> visitDoubleLiteral(node)
    is ListLiteral -> visitListLiteral(node)
    is Parentheses -> visitParentheses(node)
    is PrefixOperation -> visitPrefixOperation(node)
    is SuffixOperation -> visitSuffixOperation(node)
    is StringLiteral -> visitStringLiteral(node)
    is SymbolReference -> visitSymbolReference(node)
    is While -> visitWhile(node)
    is ForIn -> visitForIn(node)
    is Break -> visitBreak(node)
    is Continue -> visitContinue(node)
    is NoneLiteral -> visitNoneLiteral(node)
    is Native -> visitNative(node)
  }

fun <T> NodeVisitor<T>.visitNodes(vararg nodes: Node?): List<T> =
  nodes.asSequence().filterNotNull().map { visit(it) }.toList()

fun <T> NodeVisitor<T>.visitAll(vararg nodeLists: List<Node?>): List<T> =
  nodeLists.asSequence().flatten().filterNotNull().map { visit(it) }.toList()
