// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

fun <T> NodeVisitor<T>.visit(node: Node): T =
  when (node) {
    is Symbol -> visitSymbol(node)
    is TypeSpec -> visitTypeSpec(node)
    is Block -> visitBlock(node)
    is CompilationUnit -> visitCompilationUnit(node)
    is LetAssignment -> visitLetAssignment(node)
    is VarAssignment -> visitVarAssignment(node)
    is SetAssignment -> visitSetAssignment(node)
    is InfixOperation -> visitInfixOperation(node)
    is BooleanLiteral -> visitBooleanLiteral(node)
    is FunctionCall -> visitFunctionCall(node)
    is ArgumentSpec -> visitArgumentSpec(node)
    is FunctionDefinition -> visitFunctionDefinition(node)
    is LetDefinition -> visitLetDefinition(node)
    is If -> visitIf(node)
    is ImportPath -> visitImportPath(node)
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
    is ForInItem -> visitForInItem(node)
    is ForIn -> visitForIn(node)
    is Break -> visitBreak(node)
    is Continue -> visitContinue(node)
    is Return -> visitReturn(node)
    is NoneLiteral -> visitNoneLiteral(node)
    is NativeFunctionDescriptor -> visitNativeFunctionDescriptor(node)
    is NativeTypeDescriptor -> visitNativeTypeDescriptor(node)
    is IndexedBy -> visitIndexedBy(node)
    is TypeDefinition -> visitTypeDefinition(node)
  }

fun <T> NodeVisitor<T>.visitNodes(vararg nodes: Node?): List<T> =
  nodes.asSequence().filterNotNull().map { visit(it) }.toList()

fun <T> NodeVisitor<T>.visitAll(vararg nodeLists: List<Node?>): List<T> =
  nodeLists.asSequence().flatten().filterNotNull().map { visit(it) }.toList()
