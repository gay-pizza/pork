package gay.pizza.pork.ast

import gay.pizza.pork.ast.gen.*

abstract class FunctionLevelVisitor<T> : NodeVisitor<T> {
  override fun visitForInItem(node: ForInItem): T =
    throw RuntimeException("Visiting ForInItem is not supported.")

  override fun visitSymbol(node: Symbol): T =
    throw RuntimeException("Visiting Symbol is not supported.")

  override fun visitLetDefinition(node: LetDefinition): T {
    topLevelUsedError("LetDefinition")
  }

  override fun visitArgumentSpec(node: ArgumentSpec): T =
    throw RuntimeException("Visiting ArgumentSpec is not supported.")

  override fun visitFunctionDefinition(node: FunctionDefinition): T {
    topLevelUsedError("FunctionDefinition")
  }

  override fun visitImportDeclaration(node: ImportDeclaration): T {
    topLevelUsedError("ImportDeclaration")
  }

  override fun visitImportPath(node: ImportPath): T {
    topLevelUsedError("ImportPath")
  }

  override fun visitCompilationUnit(node: CompilationUnit): T {
    topLevelUsedError("CompilationUnit")
  }

  override fun visitNativeFunctionDescriptor(node: NativeFunctionDescriptor): T {
    topLevelUsedError("NativeFunctionDescriptor")
  }

  private fun topLevelUsedError(name: String): Nothing {
    throw RuntimeException("$name cannot be visited in a FunctionVisitor.")
  }
}
