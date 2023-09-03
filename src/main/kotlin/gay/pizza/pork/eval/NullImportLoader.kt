package gay.pizza.pork.eval

import gay.pizza.pork.ast.nodes.CompilationUnit

object NullImportLoader : ImportLoader {
  override fun load(path: String): CompilationUnit {
    throw RuntimeException("NullImportLoader cannot import compilation units.")
  }
}
